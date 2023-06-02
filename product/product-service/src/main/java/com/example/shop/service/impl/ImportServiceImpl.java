package com.example.shop.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.shop.contants.QueueConstants;
import com.example.shop.domain.*;
import com.example.shop.mapper.ProdCommMapper;
import com.example.shop.mapper.ProdMapper;
import com.example.shop.mapper.ProdTagReferenceMapper;
import com.example.shop.pool.ProductThreadPool;
import com.example.shop.repositry.ProdEsRepository;
import com.example.shop.service.ImportService;
import com.example.shop.service.ProdTagReferenceService;
import com.rabbitmq.client.Channel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author: William
 * @date: 2023-06-01 16:13
 **/

/**
 * 在数据处理和ETL（提取、转换、加载）过程中，"ES" 通常是指 Elasticsearch，一种开源搜索和分析引擎。
 *  要将数据导入到 Elasticsearch 中，有几种常见的方式，包括全量导入、增量导入和快速导入。
 *  全量导入：
 *      全量导入是指将整个数据集一次性导入 Elasticsearch。这种方法适用于初始导入或重新索引现有数据集的情况。
 *      您可以将数据集从源提取，进行必要的转换和清洗，然后将其一次性加载到 Elasticsearch 中。全量导入适用于数据量较小或导入频率较低的情况。
 *  增量导入：
 *      增量导入是指将新增的数据逐步导入到 Elasticsearch，以保持索引与源数据的同步。
 *      这种方式适用于源数据不断发生变化的情况，如实时日志或流数据。
 *      通常，您会使用一种持续的数据管道或数据流，将新增的数据从源发送到 Elasticsearch。
 *      增量导入可以确保索引中的数据是最新的，并且可以快速处理大量数据。
 *  快速导入：
 *      快速导入是指使用专门的工具或技术，以高效地将数据批量导入到 Elasticsearch。
 *      这种方式通常用于大规模数据集的导入，并且旨在提高导入速度和效率。
 *      一些常见的快速导入方法包括使用 Elasticsearch 的 Bulk API 进行批量导入，或者使用专门的数据导入工具如 Logstash、Kafka 等。
 *      快速导入能够利用并行处理和批量操作来提高导入性能。
 * 根据您的具体需求和数据特点，您可以选择适合的导入方式。如果是首次导入或需要重新索引整个数据集，全量导入是一个好的选择。
 * 如果您需要实时保持索引与源数据同步，增量导入是合适的。而对于大规模数据集的高效导入，您可以考虑使用快速导入方法。
 * 请注意，根据您的具体环境和需求，导入数据到 Elasticsearch 可能涉及到一些技术细节和配置调优。
 */
@Configuration
@RequiredArgsConstructor
public class ImportServiceImpl implements ImportService, CommandLineRunner {

    private final ProdMapper prodMapper;

    private final ProdEsRepository prodEsRepository;

    private final ProdTagReferenceMapper prodTagReferenceMapper;

    private final ProdCommMapper prodCommMapper;

    private Date t1;


    //加载每页条数  从nacos注册中心的配置中导入
    @Value("${es.import.size}")
    private int size;

//    @PostConstruct
//    public void initImportAll() {
//        importAll();
//    }

    /**
     * 全量导入：服务器启动时，就需要导入的数据，导入到es中
     * 方法1. 可以让当前类实现CommandLineRunner接口，在重写的run方法中调用importAll方法
     * 方法2. 可以通过注解@PostConstruct注解来实现，调用import方法的操作
     */
    @Override
    public void importAll() {
        System.out.println("开始全量导入");
        //核心是将数据查询出来List<prod>转换成List<ProdES>，批量导入即可
        import2ES(null,null);

        System.out.println("全量导入结束");
    }

    private void import2ES(Date t1,Date t2) {
        //查询商品的总记录数
        Integer totalCount = prodMapper.selectCount(
                new LambdaQueryWrapper<Prod>()
                        .eq(Prod::getStatus, 1)
                        .between(
                                t1 != null && t2 != null,
                                Prod::getUpdateTime,
                                t1,t2
                        )
        );

        //计算出总页数
        Integer totalPages = totalCount % size == 0 ? totalCount / size : totalCount / size + 1;

        //创建计数器对象，当所有的数据导入完成后，再执行后续的业务逻辑，也就是创建t1的时间
        CountDownLatch countDownLatch = new CountDownLatch(totalPages);

        //遍历页数，导入每一页的数据，第一页的数据的索引值是1开始的
        for (Integer i = 0; i < totalPages; i++) {
            //根据当前页和每页条数,查询当前页的数据
            Page<Prod> prodPage = prodMapper.selectPage(
                    new Page<Prod>(i + 1, size, false),
                    new LambdaQueryWrapper<Prod>()
                            .eq(Prod::getStatus, 1)
                            .between(
                                    t1 != null && t2 != null,
                                    Prod::getUpdateTime,
                                    t1,t2
                            )
            );

            List<Prod> records = prodPage.getRecords();

            if (CollectionUtils.isEmpty(records))
                return;

            //可以使用线程池进行操作，异步操作
            ProductThreadPool.poolExecutor.execute(
                    () -> {
                        //批量导入操作
                        prodEsRepository.saveAll(
                                //将List<Prod>转换为List<ProdEs>
                                translateProdEsList(records)
                        );
                    }
            );


            //计数器-1操作
            countDownLatch.countDown();

        }

        try {
            //将线程阻塞，当倒计数完成后，再向下执行
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private List<ProdEs> translateProdEsList(List<Prod> prodList) {
        //将prodIds收集起来，然后查询关联的分组，关联关系列表数据
        List<Long> prodIds = prodList.stream().map(Prod::getProdId).collect(Collectors.toList());

        List<ProdTagReference> referenceList = prodTagReferenceMapper.selectList(
                new LambdaQueryWrapper<ProdTagReference>()
                        .in(ProdTagReference::getProdId, prodIds)
        );

        //查询所有的评论数据，根据prodIds，不要这样做，因为评论数据太多了，很容易OOM(内存溢出)


       return prodList.stream().map(
                prod -> {

                    //将关联关系的集合过滤出来
                    List<Long> tagIdList = referenceList.stream().filter(
                            reference -> reference.getProdId().equals(prod.getProdId())
                    ).collect(Collectors.toList()).stream().map(ProdTagReference::getTagId).collect(Collectors.toList());

                    //根据proId查询当前商品评论数量，好评数量
                    Integer totalCommonCount = prodCommMapper.selectCount(
                            new LambdaQueryWrapper<ProdComm>()
                                    .eq(ProdComm::getProdId, prod.getProdId())
                                    .eq(ProdComm::getStatus, 1)
                    );

                    Integer totalPraiseCommCount = prodCommMapper.selectCount(
                            new LambdaQueryWrapper<ProdComm>()
                                    .eq(ProdComm::getEvaluate, 0)
                                    .eq(ProdComm::getStatus, 1)
                                    .eq(ProdComm::getProdId, prod.getProdId())
                    );

                    //BigDecimal: 用来对超过16位有效位的数进行精确的运算。
                    //计算好评率 = 好评数 / 总评数 * 100
                    BigDecimal positiveRating = BigDecimal.ZERO;

                    if (totalCommonCount.intValue() > 0)
                        positiveRating = new BigDecimal(totalPraiseCommCount)
                                .divide(
                                        //除以的数字
                                        new BigDecimal(totalCommonCount),
                                        //小数点保留的位数
                                        2,
                                        //除后的逻辑，比如向下取整、向上取整、四舍五入...
                                        RoundingMode.HALF_UP
                                ).multiply(
                                        new BigDecimal(100)
                                );


                    return ProdEs.builder()
                            .prodId(prod.getProdId())
                            .prodName(prod.getProdName())
                            .price(prod.getPrice())
                            .soldNum(prod.getSoldNum().longValue())
                            .brief(prod.getBrief())
                            .pic(prod.getPic())
                            .status(prod.getStatus())
                            .totalStocks(prod.getTotalStocks().longValue())
                            .categoryId(prod.getCategoryId())
                            .tagList(
                                    CollectionUtils.isEmpty(tagIdList) ? Collections.emptyList() : tagIdList
                            )
                            .praiseNumber(totalPraiseCommCount.longValue())
                            .positiveRating(positiveRating)
                            .build();
                }
        ).collect(Collectors.toList());
    }

    @Override
    //触发定时任务，每隔半个小时，执行一次
    // [initialDelay  第一次执行的时间间隔]
    // [fixedDelay  第一次之后的执行时间间隔]
    //@Scheduled(initialDelay = 30 * 60 * 1000,fixedDelay = 30 * 60 * 1000)
    @Scheduled(initialDelay = 30 * 1000,fixedDelay = 30 * 1000)
    public void updateImport() {
        System.out.println("开始增量导入");
        //创建t2时间
        Date t2 = new Date();

        System.out.println("t1 = " + t1);
        System.out.println("t2 = " + t2);

        //更新导入的数据
        import2ES(t1,t2);

        //将t2赋值给t1，下次查询时，就会根据当前半个小时的时间进行查询(17:00 ~ 17:30)(17:30 ~ 18:00)
        t1 = t2;
        System.out.println("增量导入结束");
    }

    /**
     *快速导入： 当下订单后，通过MQ中消费消息，完成扣除es中的库存信息操作
     *      传递参数：[{prodId:xxx,count:1/-1}]
     */
    @Override
    @RabbitListener(queues = {QueueConstants.ES_CHANGE_QUEUE})
    public void quickImport(Message message, Channel channel) {
        System.out.println("开始快速导入");
        //获取传递过来的json数据
        String json = new String(message.getBody());

        //将json转换为List<ProdEsCount>
        List<ProdEsCount> prodEsCounts = JSON.parseArray(json, ProdEsCount.class);

        //将prodIds收集起来
        List<Long> prodIds = prodEsCounts.stream().map(ProdEsCount::getProdId).collect(Collectors.toList());

        //将List<ProdEs>集合数据查询出来
        Iterable<ProdEs> iterable = prodEsRepository.findAllById(prodIds);

        //更新数量
        iterable.forEach(
                prodEs -> {
                    //将扣减的集合输出过滤出来，并封装成一个集合，因为这个集合可能存在多条相同的记录
                    //prodId=1,skuId=2,skuId=3,skuId=4
                    List<ProdEsCount> countList = prodEsCounts.stream().filter(
                            prodEsCount -> prodEsCount.getProdId().equals(prodEs.getProdId())
                    ).collect(Collectors.toList());

                    if (!CollectionUtils.isEmpty(countList)) {
                        //计算当前扣减的总数量
                        Long count = countList.stream().map(ProdEsCount::getCount).collect(Collectors.toList())
                                .stream().reduce(Long::sum).get();

                        prodEs.setTotalStocks(
                                new BigDecimal(prodEs.getTotalStocks()).add(new BigDecimal(count)).longValue()
                        );
                    }
                }
        );

        //批量更新操作
        prodEsRepository.saveAll(iterable);

        try {
            //手动消费消息
            channel.basicAck(
                    message.getMessageProperties().getDeliveryTag()
                    ,false);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.out.println("快速导入结束");
    }

    @Override
    public void run(String... args) throws Exception {
        importAll();
        //全量导入完成后，给t1进行赋值
        //增量导入可以跟库定时任务，查询更新后的数据，t1(17:00)和t2(17:30)，然后将t2的时间赋值给t1，t1(17:30)和t2(18:00)
        t1 = new Date();
        System.out.println("t1 = " + t1);
    }
}

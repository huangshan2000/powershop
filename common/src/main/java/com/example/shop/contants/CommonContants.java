package com.example.shop.contants;

/**
 * @author: William
 * @date: 2023-05-26 23:33
 **/
public interface CommonContants {

    /**
     * 令牌的请求头
     */
    String AUTHORIZATION = "Authorization";

    /**
     * 令牌的请求头的值对应的key
     */
    //String BEARER = "bearer ";

    /**
     * 公共的令牌信息
     * 用于监听消息队列进行发送请求时携带，或者第三方回调时，发送请求时携带该令牌
     */
    String BEARER_TOKEN = "bearer eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJzY29wZSI6WyJhbGwiXSwiZXhwIjozODMxODc5NTAzLCJqdGkiOiJmMDJhOGYzMC0wMTEzLTQ1NzgtOWYxYy02NDVkODM5Y2JjNGEiLCJjbGllbnRfaWQiOiJwb3dlciJ9.ar1QMRCXIwImitRE0ey4yQCbHjxLnk7Jk3llhZZ7x52XBRv9pAsCY3K_ydl-1T7tpT9I7R276b7ab2GiLh4dKe1O3F2A1a8lvKS20edDcdlnBcJLl1jw96QW96-o-TjkP3kbWEpa_ryIlqrO5Nrxl0JVRwxsNqoYkc4_dNlAK8whH4s9JUsfdPM566_aplt_mUrrM3oLF0CtbqqV7_peFxmgnEMG-4A4plrhGR6nH_bnwclLGR7u8K447kbm629Et4QVL-ug44-oIDJM1vrgUCNaNfldy4XmkVonBx5maou7MKjPcFeJpuyDZZjEiOcfKNtnr-3AvTVVLfwA5i75IA";
}

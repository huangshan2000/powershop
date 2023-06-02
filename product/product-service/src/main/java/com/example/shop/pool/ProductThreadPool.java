package com.example.shop.pool;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ProductThreadPool {

    public static ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
            8,
            Runtime.getRuntime().availableProcessors(),
            30,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(20),
            Executors.defaultThreadFactory(),
            new ThreadPoolExecutor.AbortPolicy()
    );
}
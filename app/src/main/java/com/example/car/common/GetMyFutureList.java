package com.example.car.common;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class GetMyFutureList {

    /**
     * 开启一个带返回的线程池
     * @param c
     * @return List<Future>
     * @throws ExecutionException
     * @throws InterruptedException
     */

    public List<Future> getObList(Callable c) throws ExecutionException, InterruptedException {

        ExecutorService pool = Executors.newFixedThreadPool(2);
        // 创建多个有返回值的任务
        List<Future> list = new ArrayList<Future>();
        // 执行任务并获取Future对象
        Future f = pool.submit(c);
        list.add(f);
        // 关闭线程池
        pool.shutdown();
        return list;
    }
}

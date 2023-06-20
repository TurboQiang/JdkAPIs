package com.javafields.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @author turboqiang
 * @version 1.0
 * @description 线程同步测试
 * @date 2023-06-16 17:39:03
 */
public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executorService = Executors.newFixedThreadPool(3);
        CountDownLatch countDownLatch = new CountDownLatch(3);
        List<Future<String>> futures = new ArrayList<>();
        long start = System.currentTimeMillis();
        futures.add(executorService.submit(() -> {
            countDownLatch.countDown();
            return queryApi1();
        }));

        futures.add(executorService.submit(() -> {
            countDownLatch.countDown();
            return queryApi2();
        }));

        futures.add(executorService.submit(() -> {
            countDownLatch.countDown();
            return queryApi3();
        }));

        countDownLatch.await();

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
        long end = System.currentTimeMillis();
        System.out.println("总耗时:"+(end-start)/1000);
        executorService.shutdown();
    }

    private static String queryApi1() throws InterruptedException {
        // query api1 and return result
        long cost = 1L;
        TimeUnit.SECONDS.sleep(cost);
        System.out.println("接口1耗时:"+cost);
        return "Result from API1";
    }

    private static String queryApi2() throws InterruptedException {
        // query api2 and return result
        long cost = 2L;
        TimeUnit.SECONDS.sleep(cost);
        System.out.println("接口2耗时:"+cost);
        return "Result from API2";
    }

    private static String queryApi3() throws InterruptedException {
        // query api3 and return result
        long cost = 3L;
        TimeUnit.SECONDS.sleep(cost);
        System.out.println("接口3耗时:"+cost);
        return "Result from API3";
    }
}

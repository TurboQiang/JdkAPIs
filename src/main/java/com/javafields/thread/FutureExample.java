package com.javafields.thread;

import java.util.concurrent.*;

/**
 * @author turboqiang
 */
public class FutureExample {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Integer> future = executor.submit(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                System.out.println("子任务执行耗时2秒");
                TimeUnit.SECONDS.sleep(2);
                return 123;
            }
        });
        System.out.println("主线程阻塞获取结果中...");
        System.out.println("主线程阻塞获取结果中:"+future.get());
        executor.shutdown();
    }
}

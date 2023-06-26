package com.javafields.thread;

import com.javafields.response.Result;
import com.javafields.task.CallableTask;

import java.util.concurrent.*;

/**
 * 简单来说，如果你只需要执行一个异步任务并获取其结果，那么FutureTask就足够了。
 * 但如果你需要管理多个异步任务并按照完成顺序获取它们的结果(阻塞队列)，那么使用CompletionService会更加方便。
 * @author turboqiang
 */
public class CompletionServiceExample {
    public static void main(String[] args) {
        // 控制并发度为2
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CompletionService<Result> completionService = new ExecutorCompletionService<>(executor);
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            completionService.submit(new CallableTask("CallableTask-" + i));
        }
        /*
         * 根据任务数获取返回结果,且结果顺序与子任务的完成顺序一致,
         * 即,每当某个子任务执行完毕返回结果,就会自动获取.而不是等所有子任务执行完毕后一起返回.
         */
        for (int i = 0; i < 5; i++) {
            try {
                Future<Result> future = completionService.take();
                Result result = future.get();
                System.out.println(result.getName() + ": ended at  " + result.getTimestamp());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        long end = System.currentTimeMillis();
        System.out.println("all cost:"+(end-start)/1000+" seconds");
        // 关闭线程池,结束进程
        executor.shutdown();
    }
}

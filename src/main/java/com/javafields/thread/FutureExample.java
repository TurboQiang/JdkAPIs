package com.javafields.thread;

import com.javafields.response.Result;
import com.javafields.task.CallableTask;

import java.util.concurrent.*;

/**
 * @author turboqiang
 */
public class FutureExample {
    /**
     * 获取单任务异步结果示例
     * @param args
     */
    public static void main(String[] args) {
        // 创建单任务线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // 提交一个有结果返回的异步任务
        Future<Result> future = executor.submit(new CallableTask("SubTask"));
        // 获取到结果之前,主线程会阻塞
        System.out.println(Thread.currentThread().getName()+":waiting for results...");
        try {
            Result result = future.get();
            System.out.println(result.getName() + ": ended at  " + result.getTimestamp());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // 关闭线程池,结束进程
        executor.shutdown();
    }
}

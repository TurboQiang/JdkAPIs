package com.javafields.thread;

import com.javafields.response.Result;
import com.javafields.task.CallableTask;

import java.util.concurrent.*;


/**
 * 简单来说，如果你只需要执行一个异步任务并获取其结果，那么FutureTask就足够了。
 * 但如果你需要管理多个异步任务并按照完成顺序获取它们的结果，那么使用CompletionService会更加方便。
 * @author turboqiang
 */
public class FutureTaskExample {


    /**
     * 在这个例子中，我们基于CallableTask创建了一个FutureTask
     * 然后我们使用ExecutorService来执行这个FutureTask。
     * 最后，我们调用futureTask.get()方法来获取异步计算的结果。
     * @param args
     */
    public static void main(String[] args) {
        // 创建单任务线程池
        ExecutorService executor = Executors.newSingleThreadExecutor();
        // 提交一个有结果返回的异步任务
        FutureTask<Result> futureTask = new FutureTask<>(new CallableTask("SubTask"));
        executor.execute(futureTask);
        // 获取到结果之前,主线程会阻塞
        System.out.println(Thread.currentThread().getName()+":waiting for results...");
        try {
            Result result = futureTask.get();
            System.out.println(result.getName() + ": ended at  " + result.getTimestamp());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        // 关闭线程池,结束进程
        executor.shutdown();
    }
}

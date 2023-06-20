package com.javafields.thread;

import java.util.concurrent.*;


/**
 * 简单来说，如果你只需要执行一个异步任务并获取其结果，那么FutureTask就足够了。
 * 但如果你需要管理多个异步任务并按照完成顺序获取它们的结果，那么使用CompletionService会更加方便。
 * @author turboqiang
 */
public class FutureTaskExample {


    /**
     * 在这个例子中，我们创建了一个FutureTask，它的任务是计算0到9的和。
     * 然后我们使用ExecutorService来执行这个FutureTask。
     * 最后，我们调用futureTask.get()方法来获取异步计算的结果。
     * @param args
     */
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        FutureTask<Integer> futureTask = new FutureTask<>(() -> {
            int sum = 0;
            for (int i = 0; i < 10; i++) {
                sum += i;
            }
            System.out.println("子任务执行耗时2秒");
            TimeUnit.SECONDS.sleep(2);
            return sum;
        });
        executor.execute(futureTask);
        try {
            System.out.println("主线程阻塞获取结果中...");
            System.out.println("主线程阻塞获取结果中:"+futureTask.get());
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        executor.shutdown();
    }
}

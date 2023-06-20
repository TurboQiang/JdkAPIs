package com.javafields.thread;

import java.util.Random;
import java.util.concurrent.*;

/**
 * 简单来说，如果你只需要执行一个异步任务并获取其结果，那么FutureTask就足够了。
 * 但如果你需要管理多个异步任务并按照完成顺序获取它们的结果，那么使用CompletionService会更加方便。
 * @author turboqiang
 */
public class CompletionServiceExample {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        ExecutorService executor = Executors.newFixedThreadPool(3);
        CompletionService<Integer> completionService = new ExecutorCompletionService<>(executor);
        for (int i = 0; i < 5; i++) {
            final int taskId = i;
            completionService.submit(new Callable<Integer>() {
                @Override
                public Integer call() throws Exception {
                    int nextInt = new Random().nextInt(10);
                    System.out.println("子任务执行耗时"+nextInt+"秒\t返回子任务结果:"+taskId);
                    TimeUnit.SECONDS.sleep(nextInt);
                    return taskId;
                }
            });
        }
        for (int i = 0; i < 5; i++) {
            // 根据任务数逐个获取返回结果
            Future<Integer> future = completionService.take();
            System.out.println(future.get());
        }
        executor.shutdown();
    }
}

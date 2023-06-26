package com.javafields.thread;

import com.javafields.response.Result;
import com.javafields.task.CallableTask;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
        // 控制并发度为2
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        // 定义子任务计数器
        CountDownLatch countDownLatch = new CountDownLatch(5);
        // 结果Future容器的集合,结果顺序与任务提交顺序一致
        List<Future<String>> futures = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (int i = 0; i < 5; i++) {
            int finalI = i;
            Future<String> resultFuture = executorService.submit(() -> {
                //子任务完成,计数器减一
                countDownLatch.countDown();
                return queryApi(finalI);
            });
            futures.add(resultFuture);
        }
        // 所有子任务执行完之前,主线程阻塞
        countDownLatch.await();

        for (Future<String> future : futures) {
            System.out.println(future.get());
        }
        long end = System.currentTimeMillis();
        System.out.println("总耗时:"+(end-start)/1000 +" seconds");
        executorService.shutdown();
    }

    /**
     * 模拟接口耗时
     * @return 结果
     * @throws InterruptedException
     */
    private static String queryApi(int taskId) throws InterruptedException {
        String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        System.out.printf("子任务%d: Starting at %s \n", taskId, startTime);

        long duration = (long) (Math.random() * 10);
        System.out.println("子任务"+taskId+": 预计耗时:"+duration+" seconds");
        TimeUnit.SECONDS.sleep(duration);
        String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
        return "子任务:"+taskId+" ended at "+endTime;
    }
}

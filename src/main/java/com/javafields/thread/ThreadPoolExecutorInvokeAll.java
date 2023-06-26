package com.javafields.thread;


import com.javafields.response.Result;
import com.javafields.task.CallableTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author turboqiang
 *  结果集的顺序与子任务的推送顺序保持一致,且必须等待所有的任务执行完成后统一返回
 */
public class ThreadPoolExecutorInvokeAll {

    public static void main(String[] args) {
        // 控制并发量为2
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 封装taskList
        List<CallableTask> taskList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            CallableTask task = new CallableTask("CallableTask-" + i);
            taskList.add(task);
        }

        //结果的FutureList
        List<Future<Result>> resultList = null;
        long start = System.currentTimeMillis();
        try {
            resultList = executor.invokeAll(taskList);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        long end = System.currentTimeMillis();
        System.out.println("all cost:"+(end-start)/1000+" seconds");
        System.out.println("\n========Printing the results======");

        for (int i = 0; i < Objects.requireNonNull(resultList).size(); i++) {
            Future<Result> future = resultList.get(i);
            try {
                Result result = future.get();
                System.out.println(result.getName() + ": ended at  " + result.getTimestamp());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        // 关闭线程池,结束进程
        executor.shutdown();
    }


}

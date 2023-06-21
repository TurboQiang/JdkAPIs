package com.javafields.thread;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

/**
 * @author turboqiang
 * 必须等待所有的任务执行完成后统一返回
 */
public class ThreadPoolExecutorInvokeAll {

    public static void main(String[] args) {
        // 控制并发量为2
        ExecutorService executor = Executors.newFixedThreadPool(2);

        // 封装taskList
        List<Task> taskList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Task task = new Task("Task-" + i);
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
                System.out.println(result.getName() + ": " + result.getTimestamp());
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }

        executor.shutdown();
    }

    static class Result {
        private String name;
        private String timestamp;

        public Result(String name, String timestamp) {
            super();
            this.name = name;
            this.timestamp = timestamp;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        @Override
        public String toString() {
            return "Result [name=" + name + ", value=" + timestamp + "]";
        }
    }

    static class Task implements Callable<Result> {
        private final String name;

        public Task(String name) {
            this.name = name;
        }

        @Override
        public Result call() {
            System.out.printf("%s: Starting\n", this.name);

            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return new Result(this.name, LocalDateTime.now().toString());
        }
    }
}
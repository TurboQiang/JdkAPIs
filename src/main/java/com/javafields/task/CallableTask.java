package com.javafields.task;

import com.javafields.response.Result;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;

/**
 * @author zhangqiang
 * 带有返回结果的异步任务类
 */
public class CallableTask implements Callable<Result> {
        private final String name;

        public CallableTask(String name) {
            this.name = name;
        }

        @Override
        public Result call() {
            String startTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            System.out.printf("%s: Starting at %s \n", this.name, startTime);

            try {
                long duration = (long) (Math.random() * 10);
                System.out.printf("%s: Waiting %d seconds for results.\n", this.name, duration);
                TimeUnit.SECONDS.sleep(duration);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            String endTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS"));
            return new Result(this.name, endTime);
        }
    }

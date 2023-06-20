package com.javafields.thread;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import com.javafields.thread.bean.BusinessQuotaInfoVO;

import java.math.BigDecimal;
import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class CompletableFutureTest {



    /**
     * CompletableFuture的简单应用
     * @throws InterruptedException
     * @throws ExecutionException
     */
    @Test
    public void testCallBack() throws InterruptedException, ExecutionException {
        // 提交一个任务，返回CompletableFuture（注意，并不是把CompletableFuture提交到线程池，它没有实现Runnable）
        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            log.info("=============>异步线程开始...");
            log.info("=============>异步线程为：" + Thread.currentThread().getName());
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("=============>异步线程结束...");
            return "supplierResult";
        });
        log.info(completableFuture.toString());
        // String result = completableFuture.join();
        // log.info("主线程阻塞获取结果:"+ result);
        // 异步回调：上面的Supplier#get()返回结果后，异步线程会回调BiConsumer#accept()
        CompletableFuture<String> stringCompletableFuture = completableFuture.whenComplete((s, throwable) -> {
            log.info("=============>异步任务结束时回调...获取结果:" + s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        log.info(stringCompletableFuture.toString());

        // CompletableFuture的异步线程是守护线程，一旦main结束就没了，为了看到打印结果，需要让main休眠一会儿
        log.info("main结束");
        TimeUnit.SECONDS.sleep(15);
    }


    /**
     * 多任务在异步线程中顺序执行,解决FutureTask主线程阻塞且异步线程无法复用的情况
     * @throws InterruptedException
     */
    @Test
    public void testCallBack2() throws InterruptedException {
        // 任务一：把第一个任务推进去，顺便开启异步线程
        CompletableFuture<String> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>异步线程开始...");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("=============>completableFuture1任务结束...");
            log.info("=============>执行completableFuture1的线程为：" + Thread.currentThread().getName());
            return "supplierResult";
        });
        log.info("completableFuture1:" + completableFuture1);

        // 任务二：把第二个任务推进去，等待异步回调
        CompletableFuture<String> completableFuture2 = completableFuture1.thenApply(s -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("=============>completableFuture2任务结束 result=" + s);
            log.info("=============>执行completableFuture2的线程为：" + Thread.currentThread().getName());
            return s;
        });
        log.info("completableFuture2:" + completableFuture2);

        // 任务三：把第三个任务推进去，等待异步回调
        CompletableFuture<String> completableFuture3 = completableFuture2.thenApply(s -> {
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            log.info("=============>completableFuture3任务结束 result=" + s);
            log.info("=============>执行completableFuture3的线程为：" + Thread.currentThread().getName());
            return s;
        });
        log.info("completableFuture3:" + completableFuture3);

        log.info("主线程结束");
        TimeUnit.SECONDS.sleep(40);
    }


    /**
     * 开启多个子线程执行任务,并获取所有的结果
     * @throws InterruptedException
     */
    @Test
    public void testCallBac3() throws InterruptedException {
        // resultCollection 最好用线程安全的,例如CopyOnWriteArrayList.尚未验证
        List<BusinessQuotaInfoVO> resultCollection = new ArrayList<>();
        // 提交一个任务，返回CompletableFuture（注意，并不是把CompletableFuture提交到线程池，它没有实现Runnable）
        CompletableFuture<BusinessQuotaInfoVO> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务1异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务1开始...预计执行5s");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("李白",
                10, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture1.whenComplete((s, throwable) -> {
            log.info("=============>任务1结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
            resultCollection.add(s);
        });

        CompletableFuture<BusinessQuotaInfoVO> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务2异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务2开始....预计执行5s");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("宫本",
                    20, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture2.whenComplete((s, throwable) -> {
            log.info("=============>任务2结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
            resultCollection.add(s);
        });

        CompletableFuture<BusinessQuotaInfoVO> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务3异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务3开始....预计执行5s");
            try {
                TimeUnit.SECONDS.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("韩信",
                    30, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture3.whenComplete((s, throwable) -> {
            log.info("=============>任务3结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
            resultCollection.add(s);
        });



        // CompletableFuture的异步线程是守护线程，一旦main结束就没了，为了看到打印结果，需要让main休眠一会儿
        log.info("main结束,收集子线程结果:"+ JSON.toJSONString(resultCollection));
        TimeUnit.SECONDS.sleep(15);
        log.info("main结束,收集子线程结果2:"+ JSON.toJSONString(resultCollection,true));

    }

    /**
     * 开启多线程子任务,保证最长子任务耗时内主线程不会结束即可获取所有子任务结果,或者
     * 主线程阻塞获取子任务结果(get/join 均为阻塞获取)
     * CompletableFuture#supplyAsync 默认使用的是ForkJoinPool线程池,其开启的是守护线程.
     * 主线程结束,守护线程就会跟着结束
     */
    @Test
    public void testCallBac4() {
        long start = Clock.systemDefaultZone().millis();
        List<BusinessQuotaInfoVO> resultCollection = new ArrayList<>();
        // 提交一个任务，返回CompletableFuture（注意，并不是把CompletableFuture提交到线程池，它没有实现Runnable）
        CompletableFuture<BusinessQuotaInfoVO> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务1异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务1开始...预计执行1s");
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("李白",
                    10, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture1.whenComplete((s, throwable) -> {
            log.info("=============>任务1结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // 任务2
        CompletableFuture<BusinessQuotaInfoVO> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务2异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务2开始....预计执行3s");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("宫本",
                    20, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture2.whenComplete((s, throwable) -> {
            log.info("=============>任务2结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // 任务3
        CompletableFuture<BusinessQuotaInfoVO> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务3异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务3开始....预计执行10s");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("韩信",
                    30, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture3.whenComplete((s, throwable) -> {
            log.info("=============>任务3结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });

        List<BusinessQuotaInfoVO> collect = Stream.of(completableFuture1, completableFuture2, completableFuture3).map(CompletableFuture::join).collect(Collectors.toList());
        log.info(" main结束,收集子线程结果:"+ JSON.toJSONString(collect));

        // 每个异步线程阻塞获取结果
        // com.javafields.thread.bean.BusinessQuotaInfoVO join1 = completableFuture1.join();
        // com.javafields.thread.bean.BusinessQuotaInfoVO join2 = completableFuture2.join();
        // com.javafields.thread.bean.BusinessQuotaInfoVO join3 = completableFuture3.join();
        // resultCollection.add(join1);
        // resultCollection.add(join2);
        // resultCollection.add(join3);

        // TimeUnit.SECONDS.sleep(12);
        long end = Clock.systemDefaultZone().millis();
        log.info(" main结束, 耗时:" + (end - start) + "ms, 收集子线程结果:"+ JSON.toJSONString(collect));
        // CompletableFuture的异步线程是守护线程，一旦main结束就没了，为了看到打印结果，需要让main休眠一会儿
        // TimeUnit.SECONDS.sleep(15);
        // log.info(LocalDateTime.now()+" main结束,收集子String(collect,true));

    }

    /**
     * 测试allof
     */
    @Test
    public void testCallBac5() {
        long start = Clock.systemDefaultZone().millis();
        List<BusinessQuotaInfoVO> resultCollection = new ArrayList<>();
        // 提交一个任务，返回CompletableFuture（注意，并不是把CompletableFuture提交到线程池，它没有实现Runnable）
        CompletableFuture<BusinessQuotaInfoVO> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务1异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务1开始...预计执行3s");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("李白",
                    10, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture1.whenComplete((s, throwable) -> {
            log.info("=============>任务1结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // 任务2
        CompletableFuture<BusinessQuotaInfoVO> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务2异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务2开始....预计执行6s");
            try {
                TimeUnit.SECONDS.sleep(6);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("宫本",
                    20, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture2.whenComplete((s, throwable) -> {
            log.info("=============>任务2结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // 任务3
        CompletableFuture<BusinessQuotaInfoVO> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务3异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务3开始....预计执行10s");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("韩信",
                    30, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture3.whenComplete((s, throwable) -> {
            log.info("=============>任务3结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // allof
        CompletableFuture<Void> allFutures = CompletableFuture.allOf(completableFuture1, completableFuture2, completableFuture3);
        // 多任务集合
        List<CompletableFuture<BusinessQuotaInfoVO>> collect = Stream.of(completableFuture1, completableFuture2, completableFuture3).collect(Collectors.toList());
        // 阻塞获取所有异步线程结果集合
        CompletableFuture<List<BusinessQuotaInfoVO>> listCompletableFuture = allFutures.thenApply(v -> collect.stream().map(CompletableFuture::join).collect(Collectors.toList()));
        // 获取结果
        List<BusinessQuotaInfoVO> businessQuotaInfoVOS = listCompletableFuture.join();

        long end = Clock.systemDefaultZone().millis();
        log.info(" main结束, 耗时:" + (end - start) + "ms, 收集子线程结果:"+ JSON.toJSONString(businessQuotaInfoVOS));
    }

    /**
     * 测试anyof(返回响应最快的那个任务结果)
     */
    @Test
    public void testCallBac6() {
        long start = Clock.systemDefaultZone().millis();
        List<BusinessQuotaInfoVO> resultCollection = new ArrayList<>();
        // 提交一个任务，返回CompletableFuture（注意，并不是把CompletableFuture提交到线程池，它没有实现Runnable）
        CompletableFuture<BusinessQuotaInfoVO> completableFuture1 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务1异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务1开始...预计执行3s");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("李白",
                    10, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture1.whenComplete((s, throwable) -> {
            log.info("=============>任务1结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // 任务2
        CompletableFuture<BusinessQuotaInfoVO> completableFuture2 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务2异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务2开始....预计执行3s");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("宫本",
                    20, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture2.whenComplete((s, throwable) -> {
            log.info("=============>任务2结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // 任务3
        CompletableFuture<BusinessQuotaInfoVO> completableFuture3 = CompletableFuture.supplyAsync(() -> {
            log.info("=============>任务3异步线程为：" + Thread.currentThread().getName());
            log.info("=============>任务3开始....预计执行10s");
            try {
                TimeUnit.SECONDS.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return getBean("韩信",
                    30, BigDecimal.ONE,BigDecimal.ONE,"",1,1,"",true);
        });
        completableFuture3.whenComplete((s, throwable) -> {
            log.info("=============>任务3结束时回调...获取结果:"+s + " ,回调线程为：" + Thread.currentThread().getName());
        });
        // anyof 取最先完成任务的CompletableFuture
        CompletableFuture<Object> anyResult = CompletableFuture.anyOf(completableFuture1, completableFuture2,
                completableFuture3);
        // 阻塞获取异步线程结果
        Object join = anyResult.join();

        long end = Clock.systemDefaultZone().millis();
        log.info(" main结束, 耗时:" + (end - start) + "ms, 收集子线程结果:"+ JSON.toJSONString(join));
    }



    /**
     * 定制对象
     * @param name
     * @param quotaSource
     * @param quotaAmount
     * @param quotaAvailable
     * @param expireTime
     * @param quotaType
     * @param status
     * @param remark
     * @param usePersonal
     * @return
     */
    private BusinessQuotaInfoVO getBean(String name,
                                        Integer quotaSource,
                                        BigDecimal quotaAmount,
                                        BigDecimal quotaAvailable,
                                        String expireTime,
                                        Integer quotaType,
                                        Integer status,
                                        String remark,
                                        Boolean usePersonal) {
        return new BusinessQuotaInfoVO(name,
                quotaSource,
                quotaAmount,
                quotaAvailable,
                expireTime,
                quotaType,
                status,
                remark,
                usePersonal);
    }



}

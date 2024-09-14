package com.geeke.task;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

/**
 * 利用线程池实现任务调度
 * Task任务调度器可以实现任务的调度和删除
 * 原理:
 * 实现一个类：ThreadPoolTaskScheduler线程池任务调度器，能够开启线程池进行任务调度
 * ThreadPoolTaskScheduler.schedule（）方法会创建一个定时计划ScheduleFuture,
 * 在这个方法中添加两个参数一个是Runable:线程接口类，和CronTrigger(定时任务触发器)
 * 在ScheduleFuture中有一个cancel可以停止定时任务
 * @Author: shenzy
 * @DateTime: 2021/10/28 10:51
 * @Description: TODO
 */
@Component
@EnableScheduling
@Slf4j
public class ScheduleUtil {

    private static ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
    private static Map<String, ScheduledFuture<?>> scheduledFutureMap = new HashMap<String, ScheduledFuture<?>>();

    static {
        threadPoolTaskScheduler.initialize();
        log.info("定时任务启动");
    }

    /**
     * 定时任务的启动
     * @param runnable  执行的任务
     * @param trigger   表达式
     * @param code      编码
     */
    public static void start(Runnable runnable, Trigger trigger, String code) {
        ScheduledFuture<?> scheduledFuture = threadPoolTaskScheduler.schedule(runnable, trigger);
        scheduledFutureMap.put(code, scheduledFuture);
        log.info("启动定时任务" + code);
    }

    /**
     * 定时任务关闭
     * @param code
     */
    public static void cancel(String code) {
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(code);
        if (scheduledFuture != null/* && scheduledFuture.isCancelled()*/) {
            scheduledFuture.cancel(true);
            scheduledFutureMap.remove(code);
        }
        log.info("取消定时任务" + code);
    }

    /**
     * 定时任务重启
     * @param runnable
     * @param trigger
     * @param code
     */
    public static void reset(Runnable runnable, Trigger trigger, String code) {
        ScheduledFuture<?> scheduledFuture = scheduledFutureMap.get(code);
        if (scheduledFuture != null && scheduledFuture.isCancelled()) {
            scheduledFuture.cancel(true);
        }
        scheduledFutureMap.remove(code);

        scheduledFuture = threadPoolTaskScheduler.schedule(runnable, trigger);
        scheduledFutureMap.put(code, scheduledFuture);
        log.info("修改定时任务" + code);
    }
}


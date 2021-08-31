package com.mealchak.mealchakserverapplication.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;

@Configuration
public class SchedulerConfig implements SchedulingConfigurer {

    //스케쥴러 쓰레드 개수
    private final static int THREAD_SIZE = 3;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar){

        final ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();

        threadPoolTaskScheduler.setPoolSize(THREAD_SIZE);
        threadPoolTaskScheduler.setThreadNamePrefix("Scheduler-");
        threadPoolTaskScheduler.initialize();

        taskRegistrar.setTaskScheduler(threadPoolTaskScheduler);
    }
}

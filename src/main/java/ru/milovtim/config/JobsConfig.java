package ru.milovtim.config;

import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.milovtim.service.MinerModeChange;

import java.util.Map;
import java.util.TimeZone;


@Configuration
public class JobsConfig {

    @Bean
    JobDetail normalModeJobDetail() {
        return JobBuilder.newJob(MinerModeChange.class)
                .withIdentity("modeNormal", "PERMANENT")
                .setJobData(JobDataMapSupport.newJobDataMap(Map.of(
                        "minerAlias", "asic2",
                        "minerMode", "NORMAL"
                )))
                .storeDurably()
                .requestRecovery()
                .build();
    }


    @Bean
    JobDetail sleepModeJobDetail() {
        return JobBuilder.newJob(MinerModeChange.class)
                .withIdentity("modeSleep", "PERMANENT")
                .setJobData(JobDataMapSupport.newJobDataMap(Map.of(
                        "minerAlias", "asic2",
                        "minerMode", "SLEEP"
                )))
                .storeDurably()
                .requestRecovery()
                .build();
    }

    @Bean
    Trigger nightExec() {
        return TriggerBuilder.newTrigger()
                .forJob(normalModeJobDetail())
                .withIdentity("everyDayNightTrigger", "PERMANENT")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 50 22 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger morningExec() {
        return TriggerBuilder.newTrigger()
                .forJob(sleepModeJobDetail())
                .withIdentity("everyDayMorningTrigger", "PERMANENT")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 59 6 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }
}
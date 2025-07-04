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
    JobDetail asic1NormalModeJobDetail() {
        return createJobSetNormalMode("asic1", "NORMAL");
    }

    @Bean
    JobDetail asic2NormalModeJobDetail() {
        return createJobSetNormalMode("asic2", "NORMAL");
    }

    @Bean
    JobDetail asic1SleepModeJobDetail() {
        return createJobSetNormalMode("asic1", "SLEEP");
    }

    @Bean
    JobDetail asic2SleepModeJobDetail() {
        return createJobSetNormalMode("asic2", "SLEEP");
    }

    private static JobDetail createJobSetNormalMode(String asicAlias, String targetMode) {
        return JobBuilder.newJob(MinerModeChange.class)
                .withIdentity("mode_" + targetMode, "PERMANENT")
                .setJobData(JobDataMapSupport.newJobDataMap(Map.of(
                        "minerAlias", asicAlias,
                        "minerMode", targetMode
                )))
                .storeDurably()
                .requestRecovery()
                .build();
    }


    @Bean
    Trigger asic1NightExec() {
        return TriggerBuilder.newTrigger()
                .forJob(asic1NormalModeJobDetail())
                .withIdentity("everyDayNightTrigger", "PERMANENT")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 59 22 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic1MorningExec() {
        return TriggerBuilder.newTrigger()
                .forJob(asic1NormalModeJobDetail())
                .withIdentity("everyDayMorningTrigger", "PERMANENT")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 7 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }


    @Bean
    Trigger asic2NightExec() {
        return TriggerBuilder.newTrigger()
                .forJob(asic2NormalModeJobDetail())
                .withIdentity("everyDayNightTrigger", "PERMANENT")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 50 22 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic2MorningExec() {
        return TriggerBuilder.newTrigger()
                .forJob(asic2NormalModeJobDetail())
                .withIdentity("everyDayMorningTrigger", "PERMANENT")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 59 6 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }
}
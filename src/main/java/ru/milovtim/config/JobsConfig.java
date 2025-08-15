package ru.milovtim.config;

import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.milovtim.domain.MinerModeAction.MinerMode;
import ru.milovtim.service.job.MinerModeJob;
import ru.milovtim.service.job.MinerRestartJob;

import java.util.Map;
import java.util.TimeZone;

import static ru.milovtim.domain.MinerModeAction.MinerMode.NORMAL;
import static ru.milovtim.domain.MinerModeAction.MinerMode.SLEEP;


@Configuration
public class JobsConfig {

    public static final String PERMANENT_GROUP = "PERMANENT";


    @Bean
    JobDetail asic1RestartJobDetail() {
        return JobBuilder.newJob(MinerRestartJob.class)
                .withIdentity("asic1_restart", PERMANENT_GROUP)
                .setJobData(JobDataMapSupport.newJobDataMap(Map.of("minerAlias", "asic1")))
                .storeDurably()
                .build();
    }

    @Bean
    JobDetail asic1NormalModeJobDetail() {
        return createJobSetMode("asic1", NORMAL);
    }

    @Bean
    JobDetail asic1SleepModeJobDetail() {
        return createJobSetMode("asic1", SLEEP);
    }

    @Bean
    JobDetail asic2NormalModeJobDetail() {
        return createJobSetMode("asic2", NORMAL);
    }

    @Bean
    JobDetail asic2SleepModeJobDetail() {
        return createJobSetMode("asic2", SLEEP);
    }

    private static JobDetail createJobSetMode(String asicAlias, MinerMode targetMode) {
        return JobBuilder.newJob(MinerModeJob.class)
                .withIdentity(asicAlias + "_mode_" + targetMode.name(), PERMANENT_GROUP)
                .setJobData(JobDataMapSupport.newJobDataMap(Map.of(
                        "minerAlias", asicAlias,
                        "minerMode", targetMode.name()
                )))
                .storeDurably()
                .requestRecovery()
                .build();
    }


    //-------------****************************************************

    @Bean
    Trigger asic1NightExec() {
        return TriggerBuilder.newTrigger()
                .forJob("asic1_mode_NORMAL", PERMANENT_GROUP)
                .withIdentity("asic1NightTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(22, 50)
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic1MorningExec() {
        return TriggerBuilder.newTrigger()
                .forJob("asic1_mode_SLEEP", PERMANENT_GROUP)
                .withIdentity("asic1MorningTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(7, 0)
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }


    @Bean
    Trigger asic2NightExec() {
        return TriggerBuilder.newTrigger()
                .forJob("asic2_mode_NORMAL", PERMANENT_GROUP)
                .withIdentity("asic2NightTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(23, 0)
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic2MorningExec() {
        return TriggerBuilder.newTrigger()
                .forJob("asic2_mode_SLEEP", PERMANENT_GROUP)
                .withIdentity("asic2MorningTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(7, 1)
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

}
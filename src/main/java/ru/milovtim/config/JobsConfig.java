package ru.milovtim.config;

import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.milovtim.domain.MinerModeChange.MinerMode;
import ru.milovtim.service.MinerModeChange;

import java.util.Map;
import java.util.TimeZone;

import static ru.milovtim.domain.MinerModeChange.MinerMode.NORMAL;
import static ru.milovtim.domain.MinerModeChange.MinerMode.SLEEP;


@Configuration
public class JobsConfig {

    public static final String PERMANENT_GROUP = "PERMANENT";

    @Bean
    JobDetail asic1NormalModeJobDetail() {
        return createJobSetNormalMode("asic1", NORMAL);
    }

    @Bean
    JobDetail asic1SleepModeJobDetail() {
        return createJobSetNormalMode("asic1", SLEEP);
    }

    @Bean
    JobDetail asic2NormalModeJobDetail() {
        return createJobSetNormalMode("asic2", NORMAL);
    }

    @Bean
    JobDetail asic2SleepModeJobDetail() {
        return createJobSetNormalMode("asic2", SLEEP);
    }

    private static JobDetail createJobSetNormalMode(String asicAlias, MinerMode targetMode) {
        return JobBuilder.newJob(MinerModeChange.class)
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
                .withSchedule(CronScheduleBuilder.cronSchedule("0 59 22 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic1MorningExec() {
        return TriggerBuilder.newTrigger()
                .forJob("asic1_mode_SLEEP", PERMANENT_GROUP)
                .withIdentity("asic1MorningTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 7 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }


    @Bean
    Trigger asic2NightExec() {
        return TriggerBuilder.newTrigger()
                .forJob("asic2_mode_NORMAL", PERMANENT_GROUP)
                .withIdentity("asic2NightTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 50 22 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic2MorningExec() {
        return TriggerBuilder.newTrigger()
                .forJob("asic2_mode_SLEEP", PERMANENT_GROUP)
                .withIdentity("asic2MorningTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder.cronSchedule("0 59 6 ? * *")
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

}
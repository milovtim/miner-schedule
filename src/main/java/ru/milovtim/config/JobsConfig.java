package ru.milovtim.config;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.core.jmx.JobDataMapSupport;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.milovtim.config.AppConfig.AsicWorkPeriod;
import ru.milovtim.domain.MinerModeAction.MinerMode;
import ru.milovtim.service.job.MinerModeJob;
import ru.milovtim.service.job.MinerRestartJob;

import java.util.Map;
import java.util.TimeZone;

import static ru.milovtim.domain.MinerModeAction.MinerMode.NORMAL;
import static ru.milovtim.domain.MinerModeAction.MinerMode.SLEEP;


@Configuration
@Slf4j
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


    private AsicWorkPeriod asic1WorkPeriod;

    @Value("#{appConfig.workPeriods['asic1']}")
    public void setAsic1WorkPeriod(AsicWorkPeriod appConf) {
        this.asic1WorkPeriod = appConf;
    }

    @Bean
    Trigger asic1NightExec() {
        log.info("Asic1 mode=NORMAL trigger for {}:{}", asic1WorkPeriod.getStart().getHour(), asic1WorkPeriod.getStart().getMinute());
        return TriggerBuilder.newTrigger()
                .forJob("asic1_mode_NORMAL", PERMANENT_GROUP)
                .withIdentity("asic1NightTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder
                        .dailyAtHourAndMinute(asic1WorkPeriod.getStart().getHour(), asic1WorkPeriod.getStart().getMinute())
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic1MorningExec() {
        log.info("Asic1 mode=SLEEP trigger for {}:{}", asic1WorkPeriod.getStop().getHour(), asic1WorkPeriod.getStop().getMinute());
        return TriggerBuilder.newTrigger()
                .forJob("asic1_mode_SLEEP", PERMANENT_GROUP)
                .withIdentity("asic1MorningTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder
                        .dailyAtHourAndMinute(asic1WorkPeriod.getStop().getHour(), asic1WorkPeriod.getStop().getMinute())
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }


    private AsicWorkPeriod asic2WorkPeriod;

    @Value("#{appConfig.workPeriods['asic2']}")
    public void setAsic2WorkPeriod(AsicWorkPeriod awp) {
        this.asic2WorkPeriod = awp;
    }

    @Bean
    Trigger asic2NightExec() {
        log.info("Asic2 mode=NORMAL trigger for {}:{}", asic2WorkPeriod.getStart().getHour(), asic2WorkPeriod.getStart().getMinute());
        return TriggerBuilder.newTrigger()
                .forJob("asic2_mode_NORMAL", PERMANENT_GROUP)
                .withIdentity("asic2NightTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder
                        .dailyAtHourAndMinute(asic2WorkPeriod.getStart().getHour(), asic2WorkPeriod.getStart().getMinute())
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

    @Bean
    Trigger asic2MorningExec() {
        log.info("Asic2 mode=SLEEP trigger for {}:{}", asic2WorkPeriod.getStop().getHour(), asic2WorkPeriod.getStop().getMinute());
        return TriggerBuilder.newTrigger()
                .forJob("asic2_mode_SLEEP", PERMANENT_GROUP)
                .withIdentity("asic2MorningTrigger", PERMANENT_GROUP)
                .withSchedule(CronScheduleBuilder
                        .dailyAtHourAndMinute(asic2WorkPeriod.getStop().getHour(), asic2WorkPeriod.getStop().getMinute())
                        .inTimeZone(TimeZone.getTimeZone("Europe/Moscow")))
                .build();
    }

}
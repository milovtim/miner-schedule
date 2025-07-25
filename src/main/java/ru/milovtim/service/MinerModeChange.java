package ru.milovtim.service;


import lombok.RequiredArgsConstructor;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.milovtim.domain.MinerModeChange.MinerMode;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class MinerModeChange extends QuartzJobBean {

    private final SeleniumService seleniumService;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String minerAlias = jobDataMap.getString("minerAlias");
        String minerMode = jobDataMap.getString("minerMode");
        seleniumService.changeAsicByAlias(minerAlias, MinerMode.valueOf(minerMode));
    }
}

package ru.milovtim.service.job;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.milovtim.domain.MinerModeAction.MinerMode;
import ru.milovtim.service.SeleniumService;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class MinerModeJob extends QuartzJobBean {

    private final SeleniumService seleniumService;

    @Setter
    private String minerAlias;
    @Setter
    private MinerMode minerMode;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        seleniumService.changeAsicModeByAlias(minerAlias, minerMode);
    }
}

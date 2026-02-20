package ru.milovtim.service.job;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.milovtim.service.SimpleTcpPoller;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class MinerStatsJob extends QuartzJobBean {

    private final SimpleTcpPoller simpleTcpPoller;

    @Setter
    private String minerAlias;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        simpleTcpPoller.pollHost();
    }
}

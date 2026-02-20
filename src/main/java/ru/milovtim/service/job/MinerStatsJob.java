package ru.milovtim.service.job;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.flipkart.zjsonpatch.JsonDiff;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.quartz.*;
import org.springframework.scheduling.quartz.QuartzJobBean;
import ru.milovtim.service.SimpleTcpPoller;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.collections4.IteratorUtils.toList;

@Slf4j
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class MinerStatsJob extends QuartzJobBean {

    private final SimpleTcpPoller simpleTcpPoller;
    private final ObjectMapper objectMapper;

    @Setter
    private String minerAlias;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        CollectionType listOfStrings = objectMapper.getTypeFactory().constructCollectionType(List.class, String.class);

        JobDataMap data = context.getJobDetail().getJobDataMap();
        @SuppressWarnings("unchecked")
        String statsHistoryStr = data.getOrDefault("stats", "").toString();
        List<String> statsHistory = new ArrayList<>();
        if (!statsHistoryStr.trim().isEmpty()) {
            try {
                statsHistory = objectMapper.readValue(statsHistoryStr, listOfStrings);
            } catch (JsonProcessingException e) {
                log.error("Cant read history list", e);
            }
        }
        List<String> finalStatsHistory = statsHistory;
        simpleTcpPoller.pollHost().ifPresent(result -> {
            if (result != null && !result.trim().isEmpty()) {
                finalStatsHistory.add(result);
            }
        });
        try {
            data.put("stats", objectMapper.writeValueAsString(finalStatsHistory));
        } catch (JsonProcessingException e) {
            log.error("Cant save history to job context", e);
        }

        if (statsHistory.size() >= 2) {
            String lastRes = statsHistory.get(statsHistory.size() - 1);
            String beforeLastRes = statsHistory.get(statsHistory.size() - 2);
            try {
                JsonNode last = objectMapper.readTree(lastRes);
                JsonNode beforeLast = objectMapper.readTree(beforeLastRes);
                JsonNode diff = JsonDiff.asJson(beforeLast, last);
                log.info(objectMapper.writeValueAsString(diff));
            } catch (JsonProcessingException e) {
                log.warn("Cant read/write json", e);
            }
        }
    }
}

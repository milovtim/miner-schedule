package ru.milovtim.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

@Component
@ConfigurationProperties(prefix = "app")
@Setter @Getter
public class AppConfig {

    private Map<String, AsicWorkPeriod> workPeriods;

    @Setter @Getter
    public static class AsicWorkPeriod {
        private LocalTime start;
        private LocalTime stop;
    }
}

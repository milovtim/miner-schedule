package ru.milovtim.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "app")
@Component
public class AppConfig {


}

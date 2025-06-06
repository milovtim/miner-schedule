package ru.milovtim.domain;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.UUID;

@Data
@Accessors(fluent = true)
public class MinerItem {
    private UUID id;
    private String alias;
    private String ipAddr;
    private String login;
    private String password;
}

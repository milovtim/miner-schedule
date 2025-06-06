package ru.milovtim.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class MinerModeChange {
    private String login;
    private String password;
    private String address;
    private MinerMode mode;

    public MinerModeChange(MinerItem minerItem, MinerMode mode) {
        this.login = minerItem.login();
        this.password = minerItem.password();
        this.address = minerItem.ipAddr();
        this.mode = mode;
    }

    @Getter
    public enum MinerMode {
        NORMAL("0"),
        SLEEP("1");

        private final String code;

        MinerMode(String code) {
            this.code = code;
        }

    }
}

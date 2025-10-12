package ru.milovtim.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.milovtim.domain.MinerModeAction.MinerMode;
import ru.milovtim.service.SeleniumService;

@RestController
@RequiredArgsConstructor
public class ActionController {
    private final SeleniumService seleniumService;

    @RequestMapping("/")
    String hello() {
        return "Hello World!";
    }

    @PostMapping("/mode")
    public void setMode(@RequestParam("mode") MinerMode minerMode,
                        @RequestParam("alias") String alias) {
        seleniumService.changeAsicModeByAlias(alias, minerMode);
    }
}

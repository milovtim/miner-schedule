package ru.milovtim.service;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.milovtim.domain.MinerModeChange;
import ru.milovtim.domain.MinerModeChange.MinerMode;
import ru.milovtim.repo.MinerItemRepo;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class SeleniumService {
    
    private final MinerItemRepo minerItemRepo;

    public void changeAsicByAlias(String alias, MinerMode mode) {
        minerItemRepo.findByAlias(alias)
                .map(minerItem -> new MinerModeChange(minerItem, mode))
                .ifPresent(this::runScenario);
    }
    
    public void runScenario(MinerModeChange minerChange) {
        String login = minerChange.getLogin();
        String password = minerChange.getPassword();
        String address = minerChange.getAddress();
        MinerMode targetMode = minerChange.getMode();

        WebDriver driver = initFFDriver();
        UriComponents minerAddr = UriComponentsBuilder.newInstance()
                .scheme("http")
                .host("%s:%s@".formatted(login, password) + address)
                .pathSegment("#miner")
                .build();
        String uriString = minerAddr.toUriString();
        driver.get(uriString);

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> getModeSelect(d).getOptions().size() >= 3);

        Select modeSelect = getModeSelect(driver);
        String currentMode = modeSelect.getFirstSelectedOption().getAttribute("value");

        if (!targetMode.getCode().equals(currentMode)) {
            modeSelect.selectByValue(targetMode.getCode());
            driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div/div/div/div/input"))
                    .click();
        }

        driver.quit();
    }

    private static WebDriver initFFDriver() {
        FirefoxOptions opts = new FirefoxOptions();
        opts.addArguments("-headless");
        opts.setImplicitWaitTimeout(Duration.ofSeconds(20));
        return new FirefoxDriver(opts);
    }

    private static Select getModeSelect(WebDriver d) {
        return new Select(d.findElement(By.id("protoSelect")));
    }
}

package ru.milovtim.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.HasAuthentication;
import org.openqa.selenium.UsernameAndPassword;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.Augmenter;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import ru.milovtim.domain.MinerItem;
import ru.milovtim.domain.MinerModeAction;
import ru.milovtim.domain.MinerModeAction.MinerMode;
import ru.milovtim.domain.MinerRestartAction;
import ru.milovtim.repo.MinerItemRepo;

import java.net.URL;
import java.util.function.Consumer;


@Slf4j
@Service
@RequiredArgsConstructor
public class SeleniumService {

    private final MinerItemRepo minerItemRepo;
    @Value("${selenium.driver.remote}")
    private URL remote;

    public void restartAsicByAlias(String alias) {
        applyActionByAlias(alias, new MinerRestartAction());
    }

    public void changeAsicModeByAlias(String alias, MinerMode mode) {
        applyActionByAlias(alias, new MinerModeAction(mode));
    }

    private void applyActionByAlias(String alias, Consumer<WebDriver> action) {
        minerItemRepo.findByAlias(alias)
                .ifPresentOrElse(
                        mi -> runScenario(mi, action),
                        () -> log.warn("Cant run selenium action on {} because not found", alias));
    }

    private void runScenario(MinerItem minerItem, Consumer<WebDriver> minerAction) {
        String login = minerItem.login();
        String password = minerItem.password();
        String address = minerItem.ipAddr();
        log.info("[Asic: {}] Run scenario: {}", minerItem.alias(), minerAction);
        ChromeOptions chromeOptions = new ChromeOptions();
        WebDriver driver = this.remote != null ? new RemoteWebDriver(this.remote, chromeOptions) : new ChromeDriver();
        try {
            basicAuth(driver, login, password);
            UriComponents minerAddr = UriComponentsBuilder.newInstance()
                    .scheme("http")
                    .host(address)
                    .pathSegment("#miner")
                    .build();
            String uriString = minerAddr.toUriString();
            driver.get(uriString);

            minerAction.accept(driver);
        } catch (Exception e) {
            log.error("Cant execute on web driver.", e);
        } finally {
            driver.quit();
        }
    }

    private static void basicAuth(WebDriver driver, String login, String password) {
        driver = new Augmenter().augment(driver);
        if (driver instanceof HasAuthentication) {
            ((HasAuthentication) driver).register(UsernameAndPassword.of(login, password));
        }
    }
}

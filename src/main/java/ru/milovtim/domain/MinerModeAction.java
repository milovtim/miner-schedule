package ru.milovtim.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;

@Getter
@RequiredArgsConstructor
public class MinerModeAction implements Consumer<WebDriver> {

    /* target mode */
    private final MinerMode targetMode;

    @Override
    public void accept(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> getModeSelect(d).getOptions().size() >= 3);

        Select modeSelect = getModeSelect(driver);
        String currentMode = modeSelect.getFirstSelectedOption().getAttribute("value");

        if (!targetMode.getCode().equals(currentMode)) {
            modeSelect.selectByValue(targetMode.getCode());
            driver.findElement(By.xpath("/html/body/div[2]/div[2]/div/div/div/div/div/div/input"))
                    .click();
        }
    }

    private static Select getModeSelect(WebDriver d) {
        return new Select(d.findElement(By.id("protoSelect")));
    }

    @Override
    public String toString() {
        return "MinerModeAction{" +
                "targetMode=" + targetMode +
                '}';
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

package ru.milovtim.domain;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.function.Consumer;

public class MinerRestartAction implements Consumer<WebDriver> {
    @Override
    public void accept(WebDriver driver) {
        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> getRestartBtn(d, "restart"))
                .click();

        new WebDriverWait(driver, Duration.ofSeconds(5))
                .until(d -> getRestartBtn(driver, "restartFun"))
                .click();
    }

    private static WebElement getRestartBtn(WebDriver d, String id) {
        return d.findElement(By.id(id));
    }

    @Override
    public String toString() {
        return "MinerRestartAction{}";
    }
}

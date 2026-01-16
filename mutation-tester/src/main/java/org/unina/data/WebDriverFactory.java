package org.unina.data;

import com.google.common.collect.ImmutableList;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;

import java.time.Duration;
import java.util.Optional;

public class WebDriverFactory {
    private static ChromeDriver driver;

    private WebDriverFactory() {}

    public static void init() {
        if (driver == null) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments(
                    //"--headless",
                    "--disable-gpu",
                    "--window-size=1920,1080",
                    "--no-sandbox",
                    "--ignore-certificate-errors");
            driver = new ChromeDriver(options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(30));

            DevTools devTools = driver.getDevTools();
            devTools.createSession();
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            devTools.send(Network.setBlockedURLs(ImmutableList.of("*sentry*", "*ingest.sentry.io*")));
        }
    }

    public static WebDriver getDriver() { return driver; }

    public static void quitDriver() {
        driver.quit();
        driver = null;
    }
}

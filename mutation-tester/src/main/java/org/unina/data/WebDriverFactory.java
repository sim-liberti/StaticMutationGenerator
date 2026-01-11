package org.unina.data;

import com.google.common.collect.ImmutableList;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.devtools.DevTools;
import org.openqa.selenium.devtools.v131.network.Network;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

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
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            // driver.manage().window().maximize();

            DevTools devTools = driver.getDevTools();
            devTools.createSession();
            devTools.send(Network.enable(Optional.empty(), Optional.empty(), Optional.empty()));
            devTools.send(Network.setBlockedURLs(ImmutableList.of("*sentry*", "*ingest.sentry.io*")));

            // Aggiunge un hook per chiudere il driver quando la JVM termina (tutti i test finiti)
//            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
//                if (driver != null) {
//                    driver.quit();
//                    driver = null;
//                }
//            }));
        }
    }

    public static WebDriver getDriver() { return driver; }

    public static void quitDriver() {
        driver.quit();
        driver = null;
    }
}

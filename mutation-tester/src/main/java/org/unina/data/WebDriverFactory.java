package org.unina.data;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.util.concurrent.TimeUnit;

public class WebDriverFactory {
    private static WebDriver driver;

    private WebDriverFactory() {}

    public static void init() {
        if (driver == null) {
            FirefoxOptions options = new FirefoxOptions();
            options.addArguments(
                    //"--headless",
                    "--disable-gpu",
                    "--window-size=1920,1200",
                    "--no-sandbox",
                    "--ignore-certificate-errors");
            driver = new FirefoxDriver(options);
            driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
            driver.manage().window().maximize();

            // Aggiunge un hook per chiudere il driver quando la JVM termina (tutti i test finiti)
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                if (driver != null) {
                    driver.quit();
                    driver = null;
                }
            }));
        }
    }

    public static WebDriver getDriver() { return driver; }

    public static void quitDriver() {
        driver.quit();
        driver = null;
    }
}

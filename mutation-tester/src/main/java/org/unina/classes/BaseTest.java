package org.unina.classes;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public abstract class BaseTest {
    protected WebDriver driver;
    protected String baseUrl;
    protected JavascriptExecutor js;
    WebDriverWait wait;

    public BaseTest() {}

    public void init(String baseUrl, WebDriver driver) throws Exception {
        this.driver = driver;
        this.baseUrl = baseUrl;
        this.driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    public void authenticate() throws RuntimeException {
        driver.get(baseUrl);
        try {
            // Username
            WebElement element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='username']"))
            );
            element.click();
            element.clear();
            element.sendKeys("sim.liberti@gmail.com");

            // Continue
            element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Continu')]"))
            );
            element.click();

            // Go to password
            element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Log in with a password')]"))
            );
            element.click();

            // Password
            element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//input[@id='password']"))
            );
            element.click();
            element.clear();
            element.sendKeys("Nuovospotify.cucci01");

            // Submit
            element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Log in')]"))
            );
            element.click();

            element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Agree')]"))
            );
            element.click();
        } catch (TimeoutException e) {
            throw new TimeoutException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void runTest() throws RuntimeException {}
}
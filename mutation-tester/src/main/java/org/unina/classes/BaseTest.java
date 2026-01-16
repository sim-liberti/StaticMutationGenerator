package org.unina.classes;

import org.junit.After;
import org.junit.Before;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.unina.data.WebDriverFactory;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.fail;

public abstract class BaseTest {
    public abstract String getLocator();

    public WebDriver driver;
    public String baseUrl;
    public boolean acceptNextAlert = true;
    public StringBuffer verificationErrors = new StringBuffer();
    public WebDriverWait wait;

    @Before
    public final void setUp() {
        baseUrl = "https://192.168.20.4:4200";
        driver = WebDriverFactory.getDriver();
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        if (driver == null) {
            WebDriverFactory.init();
            driver = WebDriverFactory.getDriver();
            wait = new WebDriverWait(driver, Duration.ofSeconds(10));
            authenticate();
        }
        driver.get(baseUrl);
        driver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
    }

    @After
    public final void tearDown() {
        String verificationErrorString = verificationErrors.toString();
        if (!verificationErrorString.isEmpty()) {
            fail(verificationErrorString);
        }
    }

    public final void authenticate() throws RuntimeException {
        try {
            driver.get(baseUrl);
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
            element.sendKeys("Spotify2025.cucci01");

            // Submit
            element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Log in')]"))
            );
            element.click();

            element = wait.until(
                    ExpectedConditions.elementToBeClickable(By.xpath("//button[contains(., 'Agree')]"))
            );
            element.click();
            Thread.sleep(1000);
        } catch (TimeoutException e) {
            throw new TimeoutException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

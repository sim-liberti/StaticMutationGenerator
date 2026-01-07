package org.unina.classes;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;

public class SearchSongTest extends BaseTest {
    @Test
    public void testSearchSong() throws Exception {
        driver.get(baseUrl);
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Search"))).click();
        WebElement searchInput = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//input")));
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);
        WebElement hoverTarget = driver.findElement(By.xpath("(//as-media-order//div[contains(@class, 'group')])[1]"));
        Actions action = new Actions(driver);
        action.moveToElement(hoverTarget).perform();

        WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("(.//*[normalize-space(text())='Title'])[1]/following::*[name()='svg'][2]")
        ));
        icon.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Home"))).click();

        String text = driver.findElement(
                By.xpath("//as-now-playing-bar//as-track-current-info//a[contains(@class, 'text-white')]")
        ).getText();

        assertEquals("Billie Jean", text);
    }

    private boolean isElementPresent(By by) {
        try {
            driver.findElement(by);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    private boolean isAlertPresent() {
        try {
            driver.switchTo().alert();
            return true;
        } catch (NoAlertPresentException e) {
            return false;
        }
    }

    private String closeAlertAndGetItsText() {
        try {
            Alert alert = driver.switchTo().alert();
            String alertText = alert.getText();
            if (acceptNextAlert) {
                alert.accept();
            } else {
                alert.dismiss();
            }
            return alertText;
        } finally {
            acceptNextAlert = true;
        }
    }
}
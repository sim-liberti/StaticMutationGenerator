package org.unina.classes;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RobulaXPathTest extends BaseTest {

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@ng-reflect-router-link='/search']")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(
                        By.xpath("//input")
                )
        );
        searchInput.clear();
        searchInput.sendKeys("Michael Jackson");
        Thread.sleep(1000);

        // Click the artist
        wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@_ngcontent-ng-c810943523='' and @ng-reflect-router-link='/artist/3fMbdgg4jU18AjLCKBhRSm']")
        )).click();

        // Assert that the text of the current artist is the correct one
        String text = driver.findElement(
                By.xpath("//*[@x-test-hook-h2-4='']")
        ).getText();
        assertEquals("Michael Jackson", text);
    }

}

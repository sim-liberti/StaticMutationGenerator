package org.unina.classes;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class HookTest extends BaseTest {
    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][2]//*[@x-test-hook-a-5]")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-input-3]//*[@x-test-tpl-div-1]//*[@x-test-hook-input-4]")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);

        // Click the play button
        WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-main-view-3]//*[@x-test-tpl-div-2]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-album-track-13][1]//*[@x-test-tpl-as-media-table-row-1]//*[@x-test-hook-as-media-order-3]//*[@x-test-tpl-div-1]//*[@x-test-hook-as-play-button-5]//*[@x-test-tpl-button-1]//*[@x-test-hook-svg-icon-2]")
        ));
        icon.click();
        wait.until(ExpectedConditions.elementToBeClickable(By.linkText("Home"))).click();

        // Go back to home
        driver.findElement(
            By.xpath("//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-nav-bar-1]//*[@x-test-tpl-ul-3]//*[@x-test-hook-li-4][1]//*[@x-test-hook-a-5]")
        ).click();

        // Assert that the text of the current playing song is the correct one
        String text = driver.findElement(
            By.xpath("//*[@x-test-tpl-html-1]//*[@x-test-hook-angular-spotify-root-29]//*[@x-test-tpl-as-now-playing-bar-4]//*[@x-test-tpl-footer-1]//*[@x-test-hook-as-track-current-info-3]//*[@x-test-tpl-div-3]//*[@x-test-hook-a-5]")
        ).getText();
        assertEquals("Billie Jean", text);
    }

}

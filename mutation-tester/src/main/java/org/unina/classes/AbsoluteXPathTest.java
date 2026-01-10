package org.unina.classes;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class AbsoluteXPathTest extends BaseTest {

    @Test
    public void testAbsoluteXPath() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[2]/a")
            )
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[1]/as-input/div/input")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);

        // Click the play button
        WebElement icon = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-main-view/div[2]/as-search/div/div[2]/div/as-album-track[1]/as-media-table-row/as-media-order/div/as-play-button/button/svg-icon/svg")
        ));
        icon.click();
        Thread.sleep(500);

        // Go back to home
        driver.findElement(
                By.xpath("/html/body/angular-spotify-root/as-layout/as-nav-bar/ul/li[1]/a")
        ).click();

        // Assert that the text of the current playing song is the correct one
        String text = driver.findElement(
            By.xpath("/html/body/angular-spotify-root/as-layout/as-now-playing-bar/footer/div[1]/as-track-current-info/div[2]/div[1]/a")
        ).getText();
        assertEquals("Billie Jean", text);
    }

}

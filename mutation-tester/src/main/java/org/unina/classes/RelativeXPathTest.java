package org.unina.classes;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;

import static org.junit.Assert.assertEquals;

public class RelativeXPathTest extends BaseTest {
    @Test
    public void testSearchSong() throws Exception {
        driver.get(baseUrl);
        // Search link in sidebar
        wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//a[normalize-space()='Search']"))
        ).click();

        // Search input
        WebElement searchInput = wait.until(
            ExpectedConditions.visibilityOfElementLocated(
                By.xpath("//input[@placeholder='Artists, songs, albums, or playlists']")
            )
        );
        searchInput.clear();
        searchInput.sendKeys("Billie Jean");
        Thread.sleep(1000);

        // Click the play button
        WebElement icon = wait.until(
            ExpectedConditions.elementToBeClickable(
                By.xpath("//as-media-order//div[contains(@class, 'flex group')]//*[local-name()='svg']")
            )
        );
        icon.click();

        // Go back to home
        driver.findElement(
            By.xpath("//a[normalize-space()='Home']")
        ).click();

        // Assert that the text of the current playing song is the correct one
        String text = driver.findElement(
                By.xpath("//as-now-playing-bar//as-track-current-info//a[contains(@class, 'text-white')]")
        ).getText();
        assertEquals("Billie Jean", text);
    }
}

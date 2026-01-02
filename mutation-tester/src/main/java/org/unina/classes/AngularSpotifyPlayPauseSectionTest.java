package org.unina.classes;

import org.openqa.selenium.TimeoutException;

public class AngularSpotifyPlayPauseSectionTest extends BaseTest {
    @Override
    public void runTest() throws RuntimeException {
        System.out.println("Testing class.");
        //throw new TimeoutException("Trying to fail test.");
        throw new RuntimeException("Trying to brake test.");
    }

//    @Override
//    public void runTest() throws RuntimeException {
//        driver.get(baseUrl);
//        try {
//            authenticate();
//
//            WebElement element = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Eminem'])[3]/following::button[1]"))
//            );
//            element.click();
//
//            element = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.cssSelector("svg-icon.svg-icon-step-forward > svg > path"))
//            );
//            element.click();
//
//            element = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.cssSelector("svg-icon.svg-icon-step-backward > svg > path"))
//            );
//            element.click();
//
//            element = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.xpath("//nz-slider/div/div"))
//            );
//            element.click();
//
//            element = wait.until(
//                    ExpectedConditions.elementToBeClickable(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Eminem'])[3]/following::div[12]"))
//            );
//            element.click();
//
//        } catch (TimeoutException e) {
//            throw new TimeoutException(e);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
}

package org.unina.classes;

import java.util.regex.Pattern;
import java.util.concurrent.TimeUnit;
import org.junit.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;
import org.openqa.selenium.*;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.ui.Select;

public class SearchSong extends BaseTest {
  @Override
  public void runTest() throws RuntimeException {
    try {
      authenticate();
      driver.get(baseUrl);
      driver.findElement(By.linkText("Search")).click();
      driver.findElement(By.xpath("//input")).clear();
      driver.findElement(By.xpath("//input")).sendKeys("Machael Jackson");
      driver.findElement(By.xpath("(.//*[normalize-space(text()) and normalize-space(.)='Title'])[1]/following::*[name()='svg'][2]")).click();
      driver.findElement(By.linkText("Home")).click();
    } catch (TimeoutException e){
      throw new TimeoutException(e);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
}

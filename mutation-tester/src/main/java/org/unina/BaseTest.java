package org.unina;

import org.junit.Before;
import org.openqa.selenium.chrome.ChromeOptions;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;

import java.io.IOException;

public abstract class BaseTest {
    protected WebDriver driver;
    protected String baseUrl;
    protected JavascriptExecutor js;

    public void setUp() throws IOException {

    }
}

package org.unina;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.time.Duration;

public class TestRunner {

    public void runTestFromXml(String xmlFilePath, WebDriver driver) {
        try {
            File inputFile = new File(xmlFilePath);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("selenese");

            System.out.println("Starting test for " + inputFile.getName() + ": " + nList.getLength() + " steps found.");

            for (int i = 0; i < nList.getLength(); i++) {
                Node node = nList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String command = getTagValue("command", element);
                    String target = getTagValue("target", element);
                    String value = getTagValue("value", element);

                    System.out.println("Eseguendo: " + command + " su " + target);

                    executeStep(driver, command, target, value);
                }
            }
            System.out.println("Test completato con successo.");

        } catch (Exception e) {
            throw new RuntimeException("Exception during test execution: ", e);
        }
    }

    private void executeStep(WebDriver driver, String command, String target, String value) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        By locator = null;
        if (!target.isEmpty()) {
            locator = parseLocator(target);
        }

        switch (command.toLowerCase()) {
            case "open":
                driver.get(target);
                break;
            case "click":
                WebElement elementToClick = wait.until(ExpectedConditions.elementToBeClickable(locator));
                elementToClick.click();
                break;
            case "type":
            case "sendkeys":
                WebElement field = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
                field.clear();
                field.sendKeys(value);
                break;
            case "pause":
                try { Thread.sleep(Long.parseLong(target)); } catch (InterruptedException ignored) {}
                break;

            default:
                System.out.println("Comando non riconosciuto: " + command);
        }
    }

    private By parseLocator(String target) {
        if (target.startsWith("xpath=")) {
            return By.xpath(target.replace("xpath=", ""));
        } else if (target.startsWith("id=")) {
            return By.id(target.replace("id=", ""));
        } else if (target.startsWith("link=")) {
            return By.linkText(target.replace("link=", ""));
        } else if (target.startsWith("css=")) {
            return By.cssSelector(target.replace("css=", ""));
        } else if (target.startsWith("//") || target.startsWith("(")) {
            return By.xpath(target);
        }

        return By.id(target);
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node node = (Node) nodeList.item(0);
        return node != null ? node.getNodeValue().trim() : "";
    }
}

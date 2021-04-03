package com.webtestautomationframework.utils.core;

import com.webtestautomationframework.utils.GeneralUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.junit.Assert;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import static com.webtestautomationframework.service.PropertyService.getProperty;
import static com.webtestautomationframework.utils.ToolConfigProperties.BROWSER_TYPE;
import static com.webtestautomationframework.utils.ToolConfigProperties.PROJECT_URL;
import static com.webtestautomationframework.utils.core.DriverFactory.instantiateDriverObject;

public class BrowserOperations {
    private static final Logger logger = LoggerFactory.getLogger(BrowserOperations.class);

    public enum Browser {
        CHROME,FIREFOX,SAFARI, HEADLESS_CHROME, EDGE
    }

    public static void openUrl(){
        instantiateDriverObject();
        boolean pageHasLoaded;
        int retries = 0;
        String projectUrl = getProperty(PROJECT_URL);
        do {
            loadUrl(projectUrl);
            manageBrowserResolution(DriverFactory.getDriver());
            GeneralUtils.waitSeconds(1);
            pageHasLoaded = DriverFactory.getDriver().getCurrentUrl().equals(projectUrl);
            if (!pageHasLoaded) {
                logger.info("Page has not loaded. Retrying");
                retries++;
            }
        } while (!pageHasLoaded && retries < 5);
        Assert.assertTrue("The page has not loaded after 5 retries!", pageHasLoaded);
        logger.info("Finished opening page " + projectUrl);
    }

    private static void loadUrl(String url) {
        try {
            DriverFactory.getDriver().navigate().to(new URL(url));
        } catch (MalformedURLException e) {
            Assert.fail(ExceptionUtils.getMessage(e));
        }
    }

    private static Browser getCurrentBrowser() {
        if (isChrome())
            return Browser.CHROME;
        else if (isFirefox())
            return Browser.FIREFOX;
        else if (isEdge())
            return Browser.EDGE;
        else if (isHeadlessChrome())
            return Browser.HEADLESS_CHROME;
        else if (isSafari())
            return Browser.SAFARI;
        else
            return null;
    }

    private static boolean isChrome() {
        return  getProperty(BROWSER_TYPE).equalsIgnoreCase("chrome");
    }

    private static boolean isFirefox() {
        return  getProperty(BROWSER_TYPE).equalsIgnoreCase("firefox");
    }

    static boolean isEdge() {
        return  getProperty(BROWSER_TYPE).equalsIgnoreCase("edge");
    }

    private static boolean isHeadlessChrome() {
        return  getProperty(BROWSER_TYPE).equalsIgnoreCase("headless-chrome");
    }

    private static boolean isSafari() {
        return  getProperty(BROWSER_TYPE).equalsIgnoreCase("safari");
    }

    private static void manageBrowserResolution(WebDriver driver){
        switch (Objects.requireNonNull(getCurrentBrowser())){
            case EDGE:
            case SAFARI:
                maximizeBrowser(driver);
                break;
            case FIREFOX:
                requestFullscreen();
                break;
            case CHROME:
                logger.info("maximize browser is handled as part of chrome capabilities.");
                break;
            default:
                requestFullscreen();
                break;
        }
    }

    private static void maximizeBrowser(WebDriver driver) {
        WebDriver.Window window = driver.manage().window();
        String javascriptSnippet = "window.focus();";
        ((JavascriptExecutor) driver).executeScript(javascriptSnippet);
            window.setSize(new Dimension(getScreenWidth(), getScreenHeight()));
            window.maximize();
    }

    private static void requestFullscreen() {
        DriverFactory.getDriver().manage().window().fullscreen();
    }

    private static JavascriptExecutor getJSExecutor() {
        return (JavascriptExecutor) DriverFactory.getDriver();
    }

    private static int getScreenWidth() {
        return Integer.parseInt(getJSExecutor().executeScript("return window.screen.availWidth").toString());
    }

    public static int getScreenHeight() {
        return Integer.parseInt(getJSExecutor().executeScript("return window.screen.availHeight").toString());
    }



}

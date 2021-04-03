package com.webtestautomationframework.utils.core;


import com.webtestautomationframework.ScenarioExecution;
import com.webtestautomationframework.actions.WebDriverActions;
import com.webtestautomationframework.utils.gifcreation.GifCreatorListener;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.DesiredCapabilities;

import static com.webtestautomationframework.service.PropertyService.getProperty;
import static com.webtestautomationframework.utils.ToolConfigProperties.*;
import static com.webtestautomationframework.utils.core.DriverType.valueOf;

public class DriverThread {
    private Logger log = Logger.getLogger(DriverThread.class);
    private WebDriver driver;
    private DriverType selectedDriverType;
    public WebDriverActions actions;
    GifCreatorListener eventListener;

    public WebDriver getDriver() {
        if (null == driver) {
            selectedDriverType = determineEffectiveDriverType();
            DesiredCapabilities desiredCapabilities;
            synchronized (this) {
                desiredCapabilities = selectedDriverType.getDesiredCapabilities();
                desiredCapabilities.setCapability("scenarioName", ScenarioExecution.getCurrentScenario().getName());
            }
            instantiateWebDriver(desiredCapabilities);
            actions = selectedDriverType.getActions(driver);
            if(Boolean.parseBoolean(getProperty(IS_REMOTE_SERVER_URL_DEFINED))
                    && Boolean.parseBoolean(getProperty(GIFFER))){
                eventListener = selectedDriverType.getEventListener();
            }
        }
        return driver;
    }



    public WebDriver getDriverInstance() {
        return driver;
    }

    DriverType getDriverType() {
        return selectedDriverType;
    }

    void quitDriver() {
        if (null != driver) {
            log.info("Attempting to quit driver.");
            driver.quit();
            log.info("Driver quit was successful");
        }
    }

    private void instantiateWebDriver(DesiredCapabilities desiredCapabilities) {
        driver = selectedDriverType.getWebDriverObject(desiredCapabilities);
    }

    private DriverType determineEffectiveDriverType() {
        DriverType driverType = null;
        try {
            driverType = valueOf(getProperty(BROWSER_TYPE).toUpperCase());
        } catch (IllegalArgumentException ignored) {
            Assert.fail("Unknown driver specified");
        }
        return driverType;
    }
}
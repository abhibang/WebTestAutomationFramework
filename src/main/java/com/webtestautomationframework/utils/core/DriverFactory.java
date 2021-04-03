package com.webtestautomationframework.utils.core;


import com.webtestautomationframework.actions.WebDriverActions;
import com.webtestautomationframework.utils.gifcreation.GifCreatorListener;
import org.openqa.selenium.WebDriver;

public class DriverFactory {
    private static final ThreadLocal<DriverThread> driverThread = new ThreadLocal<>();

    public static void instantiateDriverObject() {
        driverThread.set(new DriverThread());
        getDriver();
    }

    public static void closeDriverObject() {
        getDriverThread().quitDriver();
    }

    public static DriverThread getDriverThread() {
        return driverThread.get();
    }

    public static WebDriver getDriver() {
        return getDriverThread().getDriver();
    }

    public static WebDriver getDriverInstance() {
        return getDriverThread().getDriverInstance();
    }

    public static DriverType getDriverType() {
        return getDriverThread().getDriverType();
    }

    public static WebDriverActions getActions() {
        return getDriverThread().actions;
    }

    public static GifCreatorListener getEventListener() {
        return getDriverThread().eventListener;
    }


}

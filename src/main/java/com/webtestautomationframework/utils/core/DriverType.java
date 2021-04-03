package com.webtestautomationframework.utils.core;


import com.webtestautomationframework.ScenarioExecution;
import com.webtestautomationframework.actions.WebDriverActions;
import com.webtestautomationframework.utils.Timeout;
import com.webtestautomationframework.utils.gifcreation.GifCreatorListener;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.support.events.EventFiringWebDriver;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import static com.webtestautomationframework.ScenarioExecution.getScenarioID;
import static com.webtestautomationframework.service.PropertyService.getProperty;
import static com.webtestautomationframework.utils.ToolConfigProperties.*;
import static org.openqa.selenium.remote.CapabilityType.BROWSER_NAME;

public enum DriverType {
    CHROME {
        @Override
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = new DesiredCapabilities(); //DesiredCapabilities.chrome();
            capabilities.setCapability(BROWSER_NAME, "Chrome");
            capabilities.setCapability(ChromeOptions.CAPABILITY, getChromeOptions());
            return capabilities;
        }

        @Override
        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            if(Boolean.parseBoolean(getProperty(IS_REMOTE_SERVER_URL_DEFINED))){
                if(!Boolean.parseBoolean(getProperty(GIFFER))){
                    return getRemoteDriver(capabilities);
                }
                return getGifDriver(capabilities);
            }
            setChromeDriverProperty();
            return new ChromeDriver(getChromeOptions());

        }

        @Override
        public GifCreatorListener getEventListener(){
            return eventListener;
        }
        @Override
        public WebDriverActions getActions(WebDriver driver) {
            return new WebDriverActions(driver);
        }

        String ChromeBrowserFlags = getProperty(CHROME_BROWSER_FLAGS);
        private ChromeOptions getChromeOptions() {
            ChromeOptions chromeOptions = new ChromeOptions();
            int flagValue = ChromeBrowserFlags.equals("Enabled") ? 1 : (ChromeBrowserFlags.equals("Disabled") ? 2 : 0);
            HashMap<String, Object> chromeLocalStatePrefs = new HashMap<String, Object>();
            List<String> experimentalFlags = new ArrayList<String>();
            experimentalFlags.add("same-site-by-default-cookies@" + flagValue);
            experimentalFlags.add("cookies-without-same-site-must-be-secure@" + flagValue);
            chromeLocalStatePrefs.put("browser.enabled_labs_experiments", experimentalFlags);
            chromeOptions.setExperimentalOption("localState", chromeLocalStatePrefs);

            chromeOptions.setExperimentalOption("prefs", chromeProfilePreferences());
            chromeOptions.addArguments("--start-maximized");
            chromeOptions.addArguments("--no-sandbox");
            chromeOptions.addArguments("--disable-web-security");
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.addArguments("--disable-popup-blocking");
            return chromeOptions;
        }

        private void setChromeDriverProperty(){
            if (System.getProperty("os.name").startsWith("Windows")){
                System.setProperty("webdriver.chrome.driver", getProperty(PATH_CHROME_DRIVER_SERVER_EXE));
            }
            else {
                System.setProperty("webdriver.chrome.driver", getProperty(PATH_CHROME_DRIVER_SERVER));
            }
            System.setProperty("webdriver.chrome.logfile", "chromedriver.log");
            System.setProperty("webdriver.chrome.verboseLogging", "true");
        }

    },
    FIREFOX {
        @Override
        public DesiredCapabilities getDesiredCapabilities() {
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(BROWSER_NAME, "firefox");
            //capabilities.setCapability("moz:firefoxOptions.binary","C:\\Program Files\\Mozilla Firefox\\firefox.exe");
            capabilities.setCapability("firefox_binary","C:\\Program Files\\Mozilla Firefox\\firefox.exe");
            capabilities.setCapability("BROWSER_BINARY","C:\\Program Files\\Mozilla Firefox\\firefox.exe");

            return capabilities;
        }

        @Override
        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            if(Boolean.parseBoolean(getProperty(IS_REMOTE_SERVER_URL_DEFINED))){
                if(!Boolean.parseBoolean(getProperty(GIFFER))) {
                    return startFirefoxOnGrid(capabilities);
                }
                return getGifDriver(capabilities);
            }
            setFireFoxDriverProperty();
            return new FirefoxDriver();

        }

        private void setFireFoxDriverProperty() {
            if (System.getProperty("os.name").startsWith("Windows")){
                System.setProperty("webdriver.gecko.driver", getProperty(PATH_GECKO_DRIVER_EXE));
            }
            else {
                System.setProperty("webdriver.gecko.driver", getProperty(PATH_GECKO_DRIVER));
            }
        }

        @Override
        public GifCreatorListener getEventListener(){
            return eventListener;
        }

        @Override
        public WebDriverActions getActions(WebDriver driver) {
            return new WebDriverActions(driver);
        }

        private WebDriver startFirefoxOnGrid(DesiredCapabilities capabilities){
            int count = 0;
            RemoteWebDriver driver = null;
            do {
                try {
                    driver = new RemoteWebDriver(new URL(getProperty(HUB_URL)), capabilities);
                } catch (WebDriverException  | MalformedURLException e) {
                    log.info("startFirefoxOnGrid() raised WebDriverException while starting " + ScenarioExecution.getCurrentScenario().getName(), e);
                    if (e.toString().contains("Unable to bind to locking port") || e.toString().contains("Unable to connect to host"))
                        driver = forceInitFirefox(capabilities, 60, e);
                    count++;
                } catch (NullPointerException e) {
                    sleep(Timeout.ONE_SECOND);
                    count++;
                }
            } while (count < 10 && driver == null);

            return driver;
        }

        private RemoteWebDriver forceInitFirefox(DesiredCapabilities capabilities, int timeToWait, Exception e) {
            if ((e.toString().contains("Unable to bind to locking port") || e.toString().contains("Unable to connect to host")) && timeToWait > 0) {
                sleep(Timeout.ONE_SECOND);
                try {
                    return new RemoteWebDriver(new URL(getProperty(HUB_URL)), getNewPort(capabilities));
                } catch (WebDriverException exp) {
                    log.info("Trying to create another webdriver instance ... ");
                    forceInitFirefox(capabilities, timeToWait - 1, exp);
                } catch (MalformedURLException e1) {
                    e1.printStackTrace();
                }
            } else if ((e.toString().contains("Unable to bind to locking port") || e.toString().contains("Unable to connect to host")) && timeToWait == 0)
                DriverFactory.closeDriverObject();
            return null;
        }




        public void sleep(int seconds) {
            try {
                Thread.sleep(seconds * 1000);
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }

        private DesiredCapabilities getNewPort(DesiredCapabilities capabilities) {
            Integer newPort = 7060 + new Random().nextInt(10);
            log.info("newPort = " + newPort);
            capabilities.setCapability("webdriver_firefox_port", newPort);

            return capabilities;
        }

    },
    SAFARI{},
    CHROME_HEADLESS{
        @Override
        public DesiredCapabilities getDesiredCapabilities(){
            DesiredCapabilities capabilities = new DesiredCapabilities();
            capabilities.setCapability(BROWSER_NAME, "chrome");
            capabilities.setCapability(ChromeOptions.CAPABILITY, getChromeHeadlessOptions());
            return capabilities;
        }

        @Override
        public WebDriver getWebDriverObject(DesiredCapabilities capabilities) {
            if(!Boolean.parseBoolean(getProperty(GIFFER))) {
                return getRemoteDriver(capabilities);
            }
            return getGifDriver(capabilities);
        }

        @Override
        public GifCreatorListener getEventListener(){
            return eventListener;
        }

        @Override
        public WebDriverActions getActions(WebDriver driver) {
            return new WebDriverActions(driver);
        }

        private ChromeOptions getChromeHeadlessOptions() {
            ChromeOptions chromeOptions = new ChromeOptions();
            chromeOptions.setExperimentalOption("prefs", chromeProfilePreferences());
            chromeOptions.addArguments("--headless");
            chromeOptions.addArguments("--disable-gpu");
            chromeOptions.addArguments("--window-size=1280,800");
            chromeOptions.addArguments("--ignore-certificate-errors");
            chromeOptions.addArguments("--disable-popup-blocking");
            chromeOptions.addArguments("--silent");
            return chromeOptions;
        }

    };

    private static WebDriver getRemoteDriver(DesiredCapabilities capabilities) {
        URL remoteAddress = null;
        try {
            remoteAddress = new URL(getProperty(HUB_URL));
        } catch (MalformedURLException e) {
            Assert.fail("Check the validity of your remote server URL!\n" + ExceptionUtils.getStackTrace(e));
        }
        return new RemoteWebDriver(remoteAddress, capabilities);
    }



    private static EventFiringWebDriver getGifDriver(DesiredCapabilities capabilities) {
        URL remoteAddress = null;
        EventFiringWebDriver gifDriver = null;
        try {
            remoteAddress = new URL(getProperty(HUB_URL));
            RemoteWebDriver driver = new RemoteWebDriver(remoteAddress, capabilities);
            gifDriver = new EventFiringWebDriver(driver);
            eventListener = new GifCreatorListener(driver,getProperty(GIF_FILE_PATH) , getScenarioID(), "gifs", true);
            eventListener.getGifScreenshotWorker().setLoopContinuously(false);
            gifDriver.register(eventListener);
        } catch (MalformedURLException e) {
            Assert.fail("Check the validity of your remote server URL!\n" + ExceptionUtils.getStackTrace(e));
        }
        return gifDriver;
    }


    private static Map<String, Object> chromeProfilePreferences() {
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("profile.default_content_settings.popups", 0);
        prefs.put("profile.default_content_setting_values.automatic_downloads", 1);
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("download.default_directory", getProperty(BROWSER_DOWNLOAD_PATH));
        prefs.put("protocol_handler.excluded_schemes.msteams", false);
        prefs.put("plugins.plugins_list", Collections.singletonList(chromePlugins()));
        return prefs;
    }

    private static HashMap<String, Object> chromePlugins() {
        HashMap<String, Object> plugin = new HashMap<String, Object>();
        plugin.put("enabled", true);
        plugin.put("name", "Chrome PDF Viewer");
        return plugin;
    }

    private static final Logger log = Logger.getLogger(DriverType.class);
    private static GifCreatorListener eventListener;

    WebDriver getWebDriverObject(DesiredCapabilities desiredCapabilities) {
        return null;
    }

    DesiredCapabilities getDesiredCapabilities() {
        return null;
    }

    WebDriverActions getActions(WebDriver driver) {
        return null;
    }

    GifCreatorListener getEventListener(){return null;}



}
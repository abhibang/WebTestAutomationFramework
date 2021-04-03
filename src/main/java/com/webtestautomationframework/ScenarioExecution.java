package com.webtestautomationframework;

import com.webtestautomationframework.utils.GeneralUtils;
import com.webtestautomationframework.utils.core.BrowserOperations;
import com.webtestautomationframework.utils.core.DriverFactory;
import com.google.common.base.Joiner;
import cucumber.api.Scenario;
import cucumber.api.java.After;
import cucumber.api.java.Before;
import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriverException;

import java.io.File;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;

import static com.webtestautomationframework.service.PropertyService.getProperty;
import static com.webtestautomationframework.utils.ToolConfigProperties.*;


public class ScenarioExecution {
    private Logger log = Logger.getLogger(ScenarioExecution.class);

    private static ThreadLocal<Scenario> currentScenario = new ThreadLocal<>();
    private static ThreadLocal<String> currentScenarioStartTime = new ThreadLocal<>();
    private static ThreadLocal<Instant> currentLoginTime = new ThreadLocal<>();
    private static ThreadLocal<String> currentScenarioTags = new ThreadLocal<>();
    GeneralUtils generalUtils = new GeneralUtils();


    @Before
    public void setUp(Scenario scenario) throws Exception {
        setCurrentScenarioDetails(scenario);
        log.info(String.format("Attempting to start scenario '%s'.", scenario.getName()));
        BrowserOperations.openUrl();

    }

    @After
    public void tearDown(Scenario scenario) {
        log.info(String.format("Scenario '%s' finished with status %s.", scenario.getName(), scenario.getStatus()));
        if (DriverFactory.getDriverInstance() != null) {
            try {
                if (scenario.isFailed()) {
                    if(Boolean.parseBoolean(getProperty(IS_REMOTE_SERVER_URL_DEFINED))
                            && Boolean.parseBoolean(getProperty(GIFFER))){
                        generalUtils.createFailedGif(scenario,DriverFactory.getDriverInstance());
                        generalUtils.deleteFolderDirectory(getProperty(GIF_FILE_PATH) + File.separator + "ScreenshotsFolder" +
                                File.separator + getScenarioID() + File.separator);
                    } else{
                        generalUtils.takeScreenShot(scenario,DriverFactory.getDriverInstance());
                    }
                }
            } catch (IllegalStateException | WebDriverException e) {
                log.error(e.getMessage());
            }
        }
        DriverFactory.closeDriverObject();
    }


    private void setCurrentScenarioDetails(Scenario scenario) {
        currentScenario.set(scenario);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MILLISECOND, Math.toIntExact(Thread.currentThread().getId()));
        Date currentDate = calendar.getTime();
        currentScenarioStartTime.set(new SimpleDateFormat("yyyyMMdd-HHmmssSSS").format(currentDate));
        currentScenarioTags.set(Joiner.on(" ").join(scenario.getSourceTagNames()));
    }

    public static Scenario  getCurrentScenario() {
        return currentScenario.get();
    }
    public static String getScenarioID(){
        String[] scenarioName = getCurrentScenario().getName().split("\\.");
        return scenarioName[0];
    }

    public static String getCurrentScenarioTags() {
        return currentScenarioTags.get();
    }

    public static String getCurrentScenarioStartTime() {
        return currentScenarioStartTime.get();
    }

    private static Instant getCurrentLoginTime() {
        return currentLoginTime.get();
    }

    public static void setCurrentLoginTime(Instant loginTime) {
        currentLoginTime.set(loginTime);
    }






}

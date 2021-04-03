package com.webtestautomationframework.utils;

import com.webtestautomationframework.ScenarioExecution;
import com.webtestautomationframework.utils.core.DriverFactory;
import cucumber.api.Scenario;
import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.webtestautomationframework.service.PropertyService.getProperty;
import static com.webtestautomationframework.utils.ToolConfigProperties.GIF_FILE_PATH;

public class GeneralUtils {
    private Logger log = Logger.getLogger(GeneralUtils.class);

    // Use of arbitrary waits is discouraged, but in some cases they are needed
    public static void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException ignored) {
        }
    }

    public static void waitSeconds(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException ignored) {
        }
    }

    public void createFailedGif(Scenario scenario, WebDriver driver) {
        byte[] byteGif = null;
        DriverFactory.getEventListener().getGifScreenshotWorker().takeScreenshot("Failed");
        DriverFactory.getEventListener().getGifScreenshotWorker().createGif(ScenarioExecution.getScenarioID());

        try {
            Path path = Paths.get(getProperty(GIF_FILE_PATH) + File.separator + "gifs" + File.separator + ScenarioExecution.getScenarioID() + ".gif");
            byteGif = Files.readAllBytes(path);
            scenario.embed(byteGif, "image/gif");
        } catch (Exception e) {
            log.info("Unable to create Gif for " + scenario.getName() + "'." + "\n" + e.toString());
        }
    }


    public void takeScreenShot(Scenario scenario, WebDriver driver) {
        boolean flag = true;
        File fileScreenShot = null;
        byte[] screenShotData = null;
        try {
            fileScreenShot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            screenShotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        } catch (Exception e) {
            flag = false;
            log.info("No screenshot for '" + scenario.getName() + "'.");
        }
        if (flag) {
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
            Date currentDate = new Date();
            try {
                FileUtils.copyFile(fileScreenShot, new File(getProperty(GIF_FILE_PATH) + dateFormat.format(currentDate) + ".png"), true);
                scenario.embed(screenShotData, "image/png");
            } catch (Exception e) {
                log.info("Unable to take screen shot for " + scenario.getName() + "'." + "\n" + e.toString());
            }
        }
    }


    public void deleteFolderDirectory(String path) {
        try {
            File file = new File(path);
            if (file.isDirectory()) {
                FileUtils.deleteDirectory(file);
            } else {
                file.delete();
            }
        } catch (IOException e) {
            log.info("Exception in deleting directory of Screenshots taken for gifCreation ");
        }
    }
}

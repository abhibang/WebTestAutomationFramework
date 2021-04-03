package com.webtestautomationframework.utils.gifcreation;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GifScreenshotWorker {


    private final List<String> screenshotsTaken = new ArrayList<>();
    private final WebDriver driver;
    private final String uniqueName = RandomStringUtils.randomAlphabetic(10);
    private final String separator = File.separator;
    private String rootDir;
    private String screenshotsFolderName;
    private String generatedGIFsFolderName;
    private int timeBetweenFramesInMilliseconds = 1500;
    private boolean loopContinuously = false;
    private int counter = 0;

    public GifScreenshotWorker(RemoteWebDriver driver) {
        this.driver = driver;

        setRootDir(String.format("gifScreenshotWorker%s%s", separator, getUniqueName()));
        setScreenshotsFolderName("screenshots");
        setGeneratedGIFsFolderName("generatedGifs");
    }

    public GifScreenshotWorker(WebDriver driver, String rootDir, String screenshotsFolder,
                               String generatedGIFsFolderName, boolean loopContinuously) {
        this.driver = driver;
        this.loopContinuously = loopContinuously;

        setRootDir(rootDir);
        setScreenshotsFolderName(screenshotsFolder);
        setGeneratedGIFsFolderName(generatedGIFsFolderName);
    }

    public List<String> getScreenshotsTaken() {
        return screenshotsTaken;
    }

    public void takeScreenshot(String last) {
        try {
            byte[] screenShotData = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
            byte[] compressedScreenShotData = compressImages(screenShotData);
            if (last.equals("Failed")) {
                BufferedImage failedScreenshot = addPadding(toBufferedImage(compressedScreenShotData), Color.RED, 15);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(failedScreenshot, "jpg", baos);
                compressedScreenShotData = baos.toByteArray();
            }

            File screenshotFile = new File(getScreenshotsFolderName() + "Shot" + counter++ + ".jpg");
            FileUtils.writeByteArrayToFile(screenshotFile, compressedScreenShotData);
            getScreenshotsTaken().add(screenshotFile.getAbsolutePath());
        } catch (Throwable e) {
            e.printStackTrace();
            System.out.println("Screenshot could not be taken or saved");

        }
    }

    /**
     * Creates the GIF and writes it on the disk
     *
     * @return - generated GIF {@link File}, null when the gif could not be generated due to lack of screenshots
     */
    public File createGif(String gifName) {
        if (getScreenshotsTaken().isEmpty()) {
            return null;
        }

        try {
            BufferedImage firstImage = ImageIO.read(new File(getScreenshotsTaken().get(0)));
            File outputFile = new File(getGeneratedGIFsFolderName() + gifName + ".gif");
            if (!outputFile.exists()) {
                outputFile.getParentFile().mkdirs();
                outputFile.createNewFile();
            }
            ImageOutputStream output = new FileImageOutputStream(outputFile);

            Giffer gif = new Giffer(
                    output,
                    firstImage.getType(),
                    getTimeBetweenFramesInMilliseconds(),
                    isLoopContinuously());


            for (int i = 0; i < getScreenshotsTaken().size(); i++) {
                BufferedImage nextImage = ImageIO.read(new File(getScreenshotsTaken().get(i)));
                gif.writeToSequence(nextImage);
            }

            gif.close();
            output.close();

            getScreenshotsTaken().clear();
            return outputFile;
        } catch (Throwable e) {
            System.out.println("Gif could not be created or saved");

        }
        return null;
    }

    /**
     * Defaults to 500ms
     *
     * @return delay used to switch from one image to another on the generated GIF
     */
    public int getTimeBetweenFramesInMilliseconds() {
        return timeBetweenFramesInMilliseconds;
    }

    /**
     * Set the delay used to switch from one image to another on thegenerated GIV
     *
     * @param timeBetweenFramesInMilliseconds - value in milliseconds
     */
    public void setTimeBetweenFramesInMilliseconds(int timeBetweenFramesInMilliseconds) {
        this.timeBetweenFramesInMilliseconds = timeBetweenFramesInMilliseconds;
    }

    /**
     * Defaults to false
     *
     * @return true/false weather the generated GIF will loop
     */
    public boolean isLoopContinuously() {
        return loopContinuously;
    }

    /**
     * Set weather the generated GIF will loop
     *
     * @param loopContinuously true / false
     */
    public void setLoopContinuously(boolean loopContinuously) {
        this.loopContinuously = loopContinuously;
    }

    /**
     * @return - Unique name generated used to store every screenshot and GIF as an unique file
     */
    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * @return - folder on disk where the screenshots and generated GIF will get stored
     */
    public String getRootDir() {
        return rootDir;
    }

    /**
     * Folder on disk where screenshots and generated GIFs will be stored
     * Defaults to "project.dir/gifScreenshotsFolder/uniqueId/"
     *
     * @param rootDir - path to folder absolute or relative to project dir
     */
    public void setRootDir(String rootDir) {
        this.rootDir = rootDir + separator;
    }

    /**
     * Defaults to "project.dir/rootDir/uniqueId/screenshots"
     *
     * @return - path to folder where screenshots will be stored relative to rootDir
     */
    public String getScreenshotsFolderName() {
        return screenshotsFolderName;
    }

    /**
     * Set location where screenshots will be stored on disk
     *
     * @param screenshotsFolderName - path to folder where screenshots will be stored relative to rootDir
     */
    public void setScreenshotsFolderName(String screenshotsFolderName) {
        this.screenshotsFolderName = getRootDir() + separator + "ScreenshotsFolder" + separator + screenshotsFolderName + separator;
    }

    /**
     * Defaults to "project.dir/rootDir/uniqueId/generatedGifs"
     *
     * @return - folder where the generated GIF will be stored on disk relative to the rootDir
     */
    public String getGeneratedGIFsFolderName() {
        return generatedGIFsFolderName;
    }

    /**
     * Set location where generated GIFs will be stored on disk
     *
     * @param generatedGIFsFolderName - path to folder for generated GIFS relative to rootDir
     */
    public void setGeneratedGIFsFolderName(String generatedGIFsFolderName) {
        this.generatedGIFsFolderName = getRootDir() + generatedGIFsFolderName + separator;
    }

    /**
     * compressing the image before saving it on disck to reduce the data size of the image taken
     */
    public byte[] compressImages(byte[] bytes) throws Exception {
        BufferedImage bufferedImage = ImageIO.read(new ByteArrayInputStream(bytes));
        if (bufferedImage.getColorModel().getTransparency() != Transparency.OPAQUE) {
            BufferedImage bImage = fillTransparentPixels(bufferedImage, Color.WHITE);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            ImageOutputStream ios = ImageIO.createImageOutputStream(bos);
            writer.setOutput(ios);

            ImageWriteParam param = writer.getDefaultWriteParam();
            if (param.canWriteCompressed()) {
                param.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
                param.setCompressionQuality(0.9f);
            }
            writer.write(null, new IIOImage(bImage, null, null), param);
            ios.close();
            writer.dispose();

            ImageIO.write(bImage, "jpg", bos);
            byte[] data = bos.toByteArray();
            return data;
        } else {
            return bytes;
        }
    }

    public BufferedImage fillTransparentPixels(BufferedImage image, Color fillColor) {
        int w = image.getWidth();
        int h = image.getHeight();
        BufferedImage image2 = new BufferedImage(w, h,
                BufferedImage.TYPE_INT_RGB);
        Graphics2D g = image2.createGraphics();
        g.setColor(fillColor);
        g.fillRect(0, 0, w, h);
        g.drawRenderedImage(image, null);
        g.dispose();
        return image2;
    }

    public BufferedImage toBufferedImage(byte[] bytes) throws IOException {
        return ImageIO.read(new ByteArrayInputStream(bytes));
    }

    public BufferedImage addPadding(BufferedImage bufferedImage, Color color, int paddingSize) throws Exception {
        BufferedImage newImage = new BufferedImage(bufferedImage.getWidth() + (paddingSize * 2), bufferedImage.getHeight() + (paddingSize * 2), bufferedImage.getType());

        Graphics g = newImage.getGraphics();
        g.setColor(color);
        g.fillRect(0, 0, newImage.getWidth(), newImage.getHeight());
        g.drawImage(bufferedImage, paddingSize, paddingSize, null);
        g.dispose();

        return newImage;
    }

}


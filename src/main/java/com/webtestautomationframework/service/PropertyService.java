package com.webtestautomationframework.service;

import com.webtestautomationframework.utils.ToolConfigProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyService {
    private static final Logger logger = LoggerFactory.getLogger(PropertyService.class);
    private static Properties properties;
    private static PropertyService propertyService;

    private PropertyService(){
        /*restrict instantiation outside the class*/
    }

    public static String getProperty(String key) {
        if(properties == null){
            readProperties();
        }
        return properties.getProperty(key);
    }
    public static String getProperty(Enum key){
        return getProperty(key.name());
    }

    private static void readProperties() {
        try {
            properties = new Properties();
            properties.load(getInputStream());
            logLoadedProperties();
            logger.info("Initialization properties done sucessfully");
        } catch (Throwable e) {
            logger.error("error while loading properties", e);
        }
    }

    private static InputStream getInputStream() throws FileNotFoundException {
        InputStream inputStream;
            logger.info("Loading the properties from bundled file");
            inputStream = getInputStreamOfBundledFile();
        return inputStream;
    }

    private static InputStream getInputStreamOfBundledFile() {
        String configFlePath = "config.properties";
        return PropertyService.class.getClassLoader().getResourceAsStream(configFlePath);
    }

    private static void logLoadedProperties() {
        logger.info("Config properties");
        logger.info("Property {} : {} ", ToolConfigProperties.PROJECT_URL, properties.getProperty(ToolConfigProperties.PROJECT_URL.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.IS_REMOTE_SERVER_URL_DEFINED, properties.getProperty(ToolConfigProperties.IS_REMOTE_SERVER_URL_DEFINED.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.HUB_URL, properties.getProperty(ToolConfigProperties.HUB_URL.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.BROWSER_TYPE, properties.getProperty(ToolConfigProperties.BROWSER_TYPE.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.BROWSER_DOWNLOAD_PATH,properties.getProperty(ToolConfigProperties.BROWSER_DOWNLOAD_PATH.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.CHROME_BROWSER_FLAGS,properties.getProperty(ToolConfigProperties.CHROME_BROWSER_FLAGS.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.PATH_CHROME_DRIVER_SERVER_EXE,properties.getProperty(ToolConfigProperties.PATH_CHROME_DRIVER_SERVER_EXE.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.PATH_CHROME_DRIVER_SERVER,properties.getProperty(ToolConfigProperties.PATH_CHROME_DRIVER_SERVER.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.PATH_EDGE_DRIVER_SERVER_EXE,properties.getProperty(ToolConfigProperties.PATH_EDGE_DRIVER_SERVER_EXE.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.PATH_GECKO_DRIVER_EXE,properties.getProperty(ToolConfigProperties.PATH_GECKO_DRIVER_EXE.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.PATH_GECKO_DRIVER,properties.getProperty(ToolConfigProperties.PATH_GECKO_DRIVER.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.GIF_FILE_PATH,properties.getProperty(ToolConfigProperties.GIF_FILE_PATH.name()));
        logger.info("Property {} : {} ", ToolConfigProperties.GIFFER,properties.getProperty(ToolConfigProperties.GIFFER.name()));

    }
}

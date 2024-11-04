package javacucumberappiumtemplate.singletons;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.remote.options.BaseOptions;
import javacucumberappiumtemplate.util.PropertiesReader;

public class AppiumDriverSingleton {

    private static AppiumDriver driver;
    private static String appiumServerUrl = "http://localhost:4723/";

    @SuppressWarnings("rawtypes")
    public static AndroidDriver getAndroidDriver() throws IOException, MalformedURLException {
        if(driver == null) {
            PropertiesReader reader = PropertiesReaderSingleton.getReader();
            BaseOptions options = new BaseOptions<>()
                .amend("platformName", reader.getProperty("appium.platformName"))
                .amend("appium:automationName", reader.getProperty("appium.automationName"))
                .amend("appium:platformVersion", reader.getProperty("appium.platformVersion"))
                .amend("appium:app", reader.getProperty("appium.app"));

            driver = new AndroidDriver(new URL(appiumServerUrl), options);
        }
        return (AndroidDriver) driver;
    }

    public static void closeDriver() {
        if(driver != null) {
            driver.quit();
        }
    }

}

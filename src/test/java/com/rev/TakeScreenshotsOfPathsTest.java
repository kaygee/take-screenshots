package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.rev.beans.Path;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.cutter.CutStrategy;
import ru.yandex.qatools.ashot.shooting.cutter.FixedCutStrategy;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TakeScreenshotsOfPathsTest {

    protected WebDriver webDriver;
    private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshotsOfPathsTest.class);
    private static final String EXTENSION = ".PNG";
    private static final String URL = "http://stage.rev.com";

    @Before
    public void setWebDriver() {
        try {
            webDriver = provideChromeDriver();
        } catch (IllegalArgumentException e) {
            System.exit(1);
        }
        webDriver.manage().deleteAllCookies();
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @Test
    public void takeScreenshots() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Path path = mapper.readValue(new File("./resources/spider_paths.yaml"), Path.class);
            for (Map.Entry<String, String> entry : path.getPaths().entrySet()) {
                LOG.info("Navigating to [" + URL + entry.getValue() + "].");
                webDriver.navigate().to(URL + entry.getValue());
                float dpr = 2;
                CutStrategy cutStrategy = new FixedCutStrategy(0, 0);
                Screenshot screenshot = getScreenshot(dpr, cutStrategy);
                String filename = getDriverType(webDriver) + webDriver.getCurrentUrl().replace("/", "_").replace(":",
                        "").replace("http", "").replace("https", "") + EXTENSION;
                ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        webDriver.quit();
    }

    private Screenshot getScreenshot(float dpr, CutStrategy cutStrategy) {
        return new AShot().
                shootingStrategy(ShootingStrategies.viewportRetina(100, cutStrategy, dpr)).takeScreenshot(webDriver);
    }

    private WebDriver provideChromeDriver() {
        DesiredCapabilities desiredCapabilities = DesiredCapabilities.chrome();

        ChromeOptions options = new ChromeOptions();
        Map<String, Object> prefs = new HashMap<String, Object>();
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        options.setExperimentalOption("prefs", prefs);
        desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);

        LoggingPreferences logPrefs = new LoggingPreferences();
        logPrefs.enable(LogType.BROWSER, Level.ALL);
        desiredCapabilities.setCapability(CapabilityType.LOGGING_PREFS, logPrefs);
        return new ChromeDriver(desiredCapabilities);
    }

    private String getDriverType(WebDriver webDriver) {
        if (webDriver instanceof FirefoxDriver) {
            return "Firefox";
        } else if (webDriver instanceof ChromeDriver) {
            return "Chrome";
        } else {
            throw new RuntimeException();
        }
    }
}

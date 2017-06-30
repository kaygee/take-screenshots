package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.rev.beans.Path;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
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
import util.FilenameCleaner;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class TakeScreenshotsUsingChromeTest {

    private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshotsUsingChromeTest.class);
    private static final String MAINTENANCE_TITLE = "Rev.com will be back soon";
    private static final String EXTENSION = ".PNG";
    private static final String URL = "http://stage.rev.com";
    protected WebDriver webDriver;

    @Parameterized.Parameter
    public String currentPath;

    @Parameterized.Parameters
    public static Iterable<? extends Object> data() throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        Path path = mapper.readValue(new File("./resources/spider_paths.yaml"), Path.class);
        ArrayList<String> paths = new ArrayList<>();
        for (Map.Entry<String, String> entry : path.getPaths().entrySet()) {
            paths.add(entry.getValue());
        }
        return paths;
    }

    @BeforeClass
    public static void setupClass() {
//        ChromeDriverManager.getInstance().setup();
    }

    @Before
    public void setWebDriver() {
        ChromeDriverManager.getInstance().setup();
        webDriver = new ChromeDriver();
//        try {
//            webDriver = provideChromeDriver();
//        } catch (IllegalArgumentException e) {
//            LOG.info(e.getLocalizedMessage());
//            System.exit(1);
//        }
        webDriver.manage().deleteAllCookies();
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @After
    public void afterEachTest() {
        webDriver.quit();
    }

    @Test
    public void takeScreenshots() throws IOException {
        webDriver.navigate().to(URL + currentPath);
        exitIfMaintenance();
        Screenshot screenshot = getScreenshot();
        String filename = getFilename();
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }

    @Ignore("Only for running on Retina laptops.")
    @Test
    public void takeRetinaScreenshots() throws IOException {
        webDriver.navigate().to(URL + currentPath);
        exitIfMaintenance();
        float dpr = 2;
        CutStrategy cutStrategy = new FixedCutStrategy(0, 0);
        Screenshot screenshot = getRetinaScreenshot(dpr, cutStrategy);
        String filename = getFilename();
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }

    private void exitIfMaintenance() {
        if (webDriver.getTitle().contains(MAINTENANCE_TITLE)) {
            LOG.info(MAINTENANCE_TITLE);
            System.exit(1);
        }
    }

    private String getFilename() {
        return FilenameCleaner.cleanFileName("Chrome" + "_" + webDriver.getCurrentUrl() + "_" + EXTENSION).replace
                ("https", "");
    }

    private Screenshot getScreenshot() {
        return new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(webDriver);
    }

    private Screenshot getRetinaScreenshot(float dpr, CutStrategy cutStrategy) {
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

}

package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.rev.beans.Path;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import util.FilenameCleaner;

import javax.imageio.ImageIO;
import java.io.File;
import java.util.Map;

public class TakeScreenshotsUsingEdgeTest {

    private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshotsUsingEdgeTest.class);

    private static final String EXTENSION = ".PNG";
    private static final String URL = "http://stage.rev.com";
    protected WebDriver webDriver;

    @Before
    public void setWebDriver() {
        try {
            System.setProperty("webdriver.edge.driver", "C:\\MicrosoftWebDriver.exe");
            webDriver = provideEdgeDriver();
        } catch (IllegalArgumentException e) {
            LOG.info(e.getLocalizedMessage());
            System.exit(1);
        }
        webDriver.manage().deleteAllCookies();
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @After
    public void afterEveryTest() {
        webDriver.quit();
    }

    @Test
    public void takeScreenshots() {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        try {
            Path path = mapper.readValue(new File("./resources/spider_paths.yaml"), Path.class);
            for (Map.Entry<String, String> entry : path.getPaths().entrySet()) {
                LOG.info("Getting [" + URL + entry.getValue() + "].");
                webDriver.get(URL + entry.getValue());
                Screenshot screenshot = getScreenshot();
                String filename = getFilename();
                LOG.info("Filename [" + filename + "].");
                ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
            }
        } catch (Exception e) {
            LOG.info("Ouch.");
            e.printStackTrace();
        }
    }

    private String getFilename() {
        return FilenameCleaner.cleanFileName("EDGE" + "_" + webDriver.getCurrentUrl() + "_" + EXTENSION).replace
                ("http", "").replace("https", "");
    }

    private Screenshot getScreenshot() {
        return new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(webDriver);
    }

    private WebDriver provideEdgeDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.edge();
        return new EdgeDriver(capabilities);
    }

}

package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.rev.beans.Path;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RunWith(Parameterized.class)
public class TakeScreenshotsUsingEdgeTest {

    private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshotsUsingEdgeTest.class);
    private static final String MAINTENANCE_TITLE = "Rev.com will be back soon";
    private static final String EXTENSION = ".PNG";
    private static final String URL = "http://stage.rev.com";
    private WebDriver webDriver;

    @Parameter
    public String currentPath;

    @Parameters
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
//        EdgeDriverManager.getInstance().setup();
    }

    @Before
    public void setWebDriver() {
        EdgeDriverManager.getInstance().version("14393").setup();
        webDriver = new EdgeDriver();
        //        try {
        //            System.setProperty("webdriver.edge.driver", "C:\\MicrosoftWebDriver.exe");
        //            webDriver = provideEdgeDriver();
        //        } catch (IllegalArgumentException e) {
        //            LOG.info(e.getLocalizedMessage());
        //            System.exit(1);
        //        }
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @After
    public void afterEveryTest() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    @Test
    public void takeScreenshots() throws IOException {
        webDriver.get(URL + currentPath);
        exitIfMaintenance();
        Screenshot screenshot = getScreenshot();
        String filename = getFilename();
        LOG.info("Filename [" + filename + "].");
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }

    private void exitIfMaintenance() {
        if (webDriver.getTitle().contains(MAINTENANCE_TITLE)) {
            LOG.info(MAINTENANCE_TITLE);
            System.exit(1);
        }
    }

    private String getFilename() {
        return FilenameCleaner.cleanFileName("EDGE" + "_" + webDriver.getCurrentUrl().replace("https", "") + EXTENSION);
    }

    private Screenshot getScreenshot() {
        return new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(webDriver);
    }

    private WebDriver provideEdgeDriver() {
        DesiredCapabilities capabilities = DesiredCapabilities.edge();
        return new EdgeDriver(capabilities);
    }

}

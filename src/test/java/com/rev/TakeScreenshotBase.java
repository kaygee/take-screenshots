package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.rev.beans.Path;
import org.junit.After;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.cutter.CutStrategy;
import util.ConfigUtil;
import util.FilenameCleaner;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Map;

@RunWith(Parameterized.class)
public class TakeScreenshotBase {

    private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshotBase.class);
    protected static final String MAINTENANCE_TITLE = "Rev.com will be back soon";
    protected static final String EXTENSION = ".PNG";
    protected static final String URL = ConfigUtil.provideEndpointUrl();
    protected WebDriver webDriver;

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

    @After
    public void afterEachTest() {
        if (webDriver != null) {
            webDriver.quit();
        }
    }

    protected void exitIfMaintenance() {
        if (webDriver.getTitle().contains(MAINTENANCE_TITLE)) {
            LOG.info(MAINTENANCE_TITLE);
            System.exit(1);
        }
    }

    protected String getFilename(String browser, String url) throws MalformedURLException {
        String expectedPath = new URL(url).getPath();
        String actualPath = new URL(webDriver.getCurrentUrl()).getPath();
        if (expectedPath.equals(actualPath)) {
            return FilenameCleaner.cleanFileName(browser + "_" + webDriver.getCurrentUrl().replace("/", "_") +
                    EXTENSION).replace("https", "");
        } else {
            throw new RuntimeException("There's an unexpected redirect. Expected [" + expectedPath + "] but got [" +
                    actualPath + "].");
        }
    }

    protected Screenshot getScreenshot() {
        return new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(webDriver);
    }

    protected Screenshot getRetinaScreenshot(float dpr, CutStrategy cutStrategy) {
        return new AShot().
                shootingStrategy(ShootingStrategies.viewportRetina(100, cutStrategy, dpr)).takeScreenshot(webDriver);
    }
}

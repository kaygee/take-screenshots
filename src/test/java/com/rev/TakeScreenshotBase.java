package com.rev;

import org.junit.After;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.yandex.qatools.ashot.AShot;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.ShootingStrategies;
import ru.yandex.qatools.ashot.shooting.cutter.CutStrategy;
import util.FilenameCleaner;

public class TakeScreenshotBase {

    private static final Logger LOG = LoggerFactory.getLogger(TakeScreenshotBase.class);
    protected static final String MAINTENANCE_TITLE = "Rev.com will be back soon";
    protected static final String EXTENSION = ".PNG";
    protected static final String URL = "http://stage.rev.com";
    protected WebDriver webDriver;


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

    protected String getFilename() {
        return FilenameCleaner.cleanFileName("Chrome" + "_" + webDriver.getCurrentUrl() + "_" + EXTENSION).replace
                ("https", "");
    }

    protected Screenshot getScreenshot() {
        return new AShot().shootingStrategy(ShootingStrategies.viewportPasting(100)).takeScreenshot(webDriver);
    }

    protected Screenshot getRetinaScreenshot(float dpr, CutStrategy cutStrategy) {
        return new AShot().
                shootingStrategy(ShootingStrategies.viewportRetina(100, cutStrategy, dpr)).takeScreenshot(webDriver);
    }
}

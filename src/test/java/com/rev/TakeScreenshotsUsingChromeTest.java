package com.rev;

import com.google.common.base.Preconditions;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.cutter.CutStrategy;
import ru.yandex.qatools.ashot.shooting.cutter.FixedCutStrategy;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TakeScreenshotsUsingChromeTest extends TakeScreenshotBase {

    @BeforeClass
    public static void setupClass() {
        ChromeDriverManager.getInstance().setup();
    }

    @Before
    public void setWebDriver() {
        webDriver = new ChromeDriver();
        webDriver.manage().deleteAllCookies();
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @Test
    public void takeScreenshots() throws IOException {
        webDriver.navigate().to(URL + currentPath);
        exitIfMaintenance();
        Screenshot screenshot = getScreenshot();
        String filename = getFilename("Chrome", URL + currentPath);
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
        String filename = getFilename("Chrome", URL + currentPath);
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }

}

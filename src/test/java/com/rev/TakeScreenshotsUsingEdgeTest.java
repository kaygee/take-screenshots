package com.rev;

import com.google.common.base.Preconditions;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.openqa.selenium.edge.EdgeDriver;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

@RunWith(Parameterized.class)
public class TakeScreenshotsUsingEdgeTest extends TakeScreenshotBase {

    private static final String MS_WEBDRIVER_VERSION = "3.14393";

    @BeforeClass
    public static void setupClass() {
        EdgeDriverManager.getInstance().version(MS_WEBDRIVER_VERSION).setup();
    }

    @Before
    public void setWebDriver() {
        webDriver = new EdgeDriver();
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @Test
    public void takeScreenshots() throws IOException {
        webDriver.get(URL + currentPath);
        exitIfMaintenance();
        Screenshot screenshot = getScreenshot();
        String filename = getFilename("Edge", URL + currentPath);
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }

}

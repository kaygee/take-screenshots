package com.rev;

import com.google.common.base.Preconditions;
import io.github.bonigarcia.wdm.OperaDriverManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.opera.OperaDriver;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class TakeScreenshotsUsingOperaTest extends TakeScreenshotBase {

    @BeforeClass
    public static void setupClass() {
        OperaDriverManager.getInstance().setup();
    }

    @Before
    public void setWebDriver() {
        webDriver = new OperaDriver();
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @Ignore("webdrivermanager can't find the binary at the moment.")
    @Test
    public void takeScreenshots() throws IOException {
        webDriver.get(URL + currentPath);
        exitIfMaintenance();
        Screenshot screenshot = getScreenshot();
        String filename = getFilename("Edge", URL + currentPath);
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }
}

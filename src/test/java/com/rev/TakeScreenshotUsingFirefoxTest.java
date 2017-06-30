package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.rev.beans.Path;
import io.github.bonigarcia.wdm.FirefoxDriverManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runners.Parameterized;
import org.openqa.selenium.firefox.FirefoxDriver;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class TakeScreenshotUsingFirefoxTest extends TakeScreenshotBase {

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

    public TakeScreenshotUsingFirefoxTest(String currentPath) {
        this.currentPath = currentPath;
    }

    @BeforeClass
    public static void setupClass() {
        FirefoxDriverManager.getInstance().setup();
    }

    @Before
    public void setWebDriver() {
        webDriver = new FirefoxDriver();
        webDriver.manage().deleteAllCookies();
        webDriver.manage().window().maximize();
        Preconditions.checkNotNull(webDriver, "Failed to set up the WebDriver");
    }

    @Test
    public void takeScreenshots() throws IOException {
        webDriver.navigate().to(URL + currentPath);
        exitIfMaintenance();
        Screenshot screenshot = getScreenshot();
        String filename = getFilename("Firefox");
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }

}

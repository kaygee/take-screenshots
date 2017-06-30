package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.rev.beans.Path;
import io.github.bonigarcia.wdm.EdgeDriverManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.edge.EdgeDriver;
import ru.yandex.qatools.ashot.Screenshot;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RunWith(Parameterized.class)
public class TakeScreenshotsUsingEdgeTest extends TakeScreenshotBase {

    private static final String MS_WEBDRIVER_VERSION = "3.14393";
    public String currentPath;

    public TakeScreenshotsUsingEdgeTest(String currentPath) {
        this.currentPath = currentPath;
    }

    @BeforeClass
    public static void setupClass() {
        EdgeDriverManager.getInstance().version(MS_WEBDRIVER_VERSION).setup();
    }

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
        String filename = getFilename("Edge");
        ImageIO.write(screenshot.getImage(), "PNG", new File("./target/" + filename));
    }

}

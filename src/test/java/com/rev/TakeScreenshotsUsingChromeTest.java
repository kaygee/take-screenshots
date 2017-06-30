package com.rev;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Preconditions;
import com.rev.beans.Path;
import io.github.bonigarcia.wdm.ChromeDriverManager;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;
import org.openqa.selenium.chrome.ChromeDriver;
import ru.yandex.qatools.ashot.Screenshot;
import ru.yandex.qatools.ashot.shooting.cutter.CutStrategy;
import ru.yandex.qatools.ashot.shooting.cutter.FixedCutStrategy;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

@RunWith(Parameterized.class)
public class TakeScreenshotsUsingChromeTest extends TakeScreenshotBase {

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

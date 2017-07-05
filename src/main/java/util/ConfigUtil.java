package util;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import static org.apache.commons.validator.routines.UrlValidator.ALLOW_LOCAL_URLS;
import static org.junit.Assert.fail;

public class ConfigUtil {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigUtil.class);

    private static final String CONFIG_FILE_NAME = "screenshots.properties";
    private static final Properties CONFIG_PROPERTIES = new Properties();

    private static Properties getProperties() {
        try {
            InputStream inputStream = new FileInputStream(CONFIG_FILE_NAME);
            CONFIG_PROPERTIES.load(inputStream);
        } catch (IOException e) {
            LOG.info("The file '" + CONFIG_FILE_NAME + "' couldn't be found or isn't a valid properties file.");
        }
        return CONFIG_PROPERTIES;
    }

    private static String getEnvVarOrProperty(String key) {
        String value = null;
        if (System.getenv().containsKey(key)) {
            value = System.getenv(key);
        } else if (System.getProperties().containsKey(key)) {
            value = System.getProperty(key);
        } else if (ConfigUtil.getProperties().containsKey(key)) {
            value = ConfigUtil.getProperties().getProperty(key);
        } else {
            LOG.info("Environment variable or property file entry not found for '" + key + "'.");
            System.exit(1);
        }
        return value;
    }

    public static String provideEndpointUrl() {
        String url = ConfigUtil.getEnvVarOrProperty("TARGET_URL");
        return getUrl(url);
    }

    private static String getUrl(String url) {
        UrlValidator defaultValidator = new UrlValidator(ALLOW_LOCAL_URLS);
        if (!defaultValidator.isValid(url)) {
            fail("The url [" + url + "] isn't a valid URL.");
        }
        return url;
    }
}

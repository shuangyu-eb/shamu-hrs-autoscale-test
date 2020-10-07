package util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public abstract class PropertiesUtils {

  private static Properties properties;

  private PropertiesUtils() {}

  public static String getProperty(String key) throws IOException {
    if (properties == null) {
      properties = new Properties();
      InputStream inputStream =
          PropertiesUtils.class.getResourceAsStream(File.separator + "datasource.properties");

      properties.load(inputStream);
    }

    return properties.getProperty(key);
  }
}

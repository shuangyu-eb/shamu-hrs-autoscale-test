package database;

import java.io.IOException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import util.PropertiesUtils;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataSourceConfig {

  private static String targetUrlTemplate;

  private static String targetUserName;

  private static String targetPassword;

  private static String tenantPrefix;

  private static String defaultSchema;

  static {
    try {

      targetUserName = PropertiesUtils.getProperty("target.user");
      targetPassword = PropertiesUtils.getProperty("target.password");
      targetUrlTemplate = PropertiesUtils.getProperty("target.jdbc-url-template");

      tenantPrefix = PropertiesUtils.getProperty("tenant.prefix");
      defaultSchema = PropertiesUtils.getProperty("default.schema");
    } catch (final IOException e) {
      log.error("Failed to init data source config");
    }
  }

  public static String getTargetUrlTemplate() {
    return targetUrlTemplate;
  }

  public static String getTargetUserName() {
    return targetUserName;
  }

  public static String getTargetPassword() {
    return targetPassword;
  }

  public static String getTenantPrefix() {
    return tenantPrefix;
  }

  public static String getDefaultSchema() {
    return defaultSchema;
  }
}

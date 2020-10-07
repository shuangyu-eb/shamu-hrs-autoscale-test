package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

public abstract class ExecuteLogUtils {
  private static final Properties logProperties = new Properties();

  private static final Properties errorLogProperties = new Properties();

  private static final String DONE_MARK_KEY = "is_done";

  private static final String IS_DUMPED_KEY = "is_dumped";

  private static final String DEFAULT_LOG_DIR = "execute-log/log.properties";

  private static final String DEFAULT_ERROR_LOG_DIR = "execute-log/error-log.properties";

  private static File logFile;

  private static File errorLogFile;

  static {
    try {
      logFile = new File(DEFAULT_LOG_DIR);
      errorLogFile = new File(DEFAULT_ERROR_LOG_DIR);
      if (logFile.exists()) {
        logProperties.load(new FileInputStream(DEFAULT_LOG_DIR));
      }
      if (errorLogFile.exists()) {
        errorLogProperties.load(new FileInputStream(DEFAULT_ERROR_LOG_DIR));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ExecuteLogUtils() {}

  public static List<String> filterUndoneCompaniesByLog(List<String> companyIds) {
    markUnDone();
    return companyIds.stream()
        .filter(
            companyId -> {
              String result = logProperties.getProperty(companyId);
              return result == null
                  || result.equals(ExecuteStatus.FAILED.getStatusCode())
                  || result.equals(ExecuteStatus.EXECUTING.getStatusCode());
            })
        .collect(Collectors.toList());
  }

  public static void recordCompany(String companyId, ExecuteStatus executeStatus) {
    record(companyId, executeStatus.getStatusCode());
  }

  public static void recordFailedLog(String companyId, String errorMessage) {
    errorLogProperties.setProperty(companyId, errorMessage);
    exportLog(errorLogFile, errorLogProperties);
  }

  public static void record(String key, String value) {
    logProperties.setProperty(key, value);
    exportLog(logFile, logProperties);
  }

  private static void exportLog(File logFile, Properties logProperties) {
    if (!logFile.exists()) {
      if (!logFile.getParentFile().exists()) {
        logFile.getParentFile().mkdirs();
      }
      try {
        logFile.createNewFile();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    try (FileWriter fileWriter = new FileWriter(logFile)) {
      logProperties.store(fileWriter, "");
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static Boolean companyExecuted(String companyId) {
    // result should not be null
    String result = logProperties.getProperty(companyId);
    return result.equals(ExecuteStatus.SUCCESS.getStatusCode());
  }

  public static Boolean companyRecorded(final String companyId) {
    String result = logProperties.getProperty(companyId);
    return result != null;
  }

  public static Boolean isDumped() {
    return Boolean.valueOf(logProperties.getProperty(IS_DUMPED_KEY));
  }

  public static void markDumped(Boolean isDumped) {
    record(IS_DUMPED_KEY, isDumped.toString());
  }

  public static void markDone() {
    record(DONE_MARK_KEY, Boolean.TRUE.toString());
  }

  public static void markUnDone() {
    record(DONE_MARK_KEY, Boolean.FALSE.toString());
  }

  public static boolean isDone() {
    return Boolean.parseBoolean(logProperties.getProperty(DONE_MARK_KEY));
  }

  public enum ExecuteStatus {
    SUCCESS(3),
    EXECUTING(2),
    FAILED(1);

    private final Integer statusCode;

    ExecuteStatus(final Integer statusCode) {
      this.statusCode = statusCode;
    }

    public String getStatusCode() {
      return statusCode.toString();
    }
  }
}

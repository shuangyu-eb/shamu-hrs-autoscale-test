package util;

import entity.Company;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;
import liquibase.util.StringUtil;

public abstract class ExecuteLogUtils {

  private static final Properties logProperties = new Properties();

  private static final String DONE_MARK_KEY = "is_done";

  private static final String IS_DUMPED_KEY = "is_dumped";

  private static final String DEFAULT_LOG_DIR = "execute-log/log.properties";

  private static final String CURRENT_EXECUTED_COMPANY = "current_executed_company";

  private static File logFile;

  static {
    try {
      logFile = new File(DEFAULT_LOG_DIR);
      if (logFile.exists()) {
        logProperties.load(new FileInputStream(DEFAULT_LOG_DIR));
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private ExecuteLogUtils() {

  }

  public static List<Company> filterUndoneCompaniesByLog(List<Company> companies) throws IOException {
    markUnDone();
    return companies.stream().filter(company -> {
      String companyId = company.getId();
      String result = logProperties.getProperty(companyId);
      if(StringUtil.isEmpty(result) || Boolean.FALSE.toString().equals(result)) {
        record(company.getId(), Boolean.FALSE.toString());
        return true;
      }
      return false;
    }).collect(Collectors.toList());
  }

  public static void recordCompany(Company company, Boolean result) {
    record(company.getId(), result.toString());
  }

  public static void record(String key, String value) {
    logProperties.setProperty(key, value);
    exportLog();
  }

  private static void exportLog() {
    if(!logFile.exists()) {
      if(!logFile.getParentFile().exists()) {
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

  public static void recordCurrentExecutedCompanyId(String companyId) {
    record(CURRENT_EXECUTED_COMPANY, companyId);
  }

  public static String getCurrentExecutedCompanyId() {
    return logProperties.getProperty(CURRENT_EXECUTED_COMPANY);
  }

  public static Boolean isDumped() {
    return Boolean.valueOf(logProperties.getProperty(IS_DUMPED_KEY));
  }

  public static void markDumped(Boolean isDumped) {
    record(IS_DUMPED_KEY, isDumped.toString());
  }

  public static void markDone() {
    record(DONE_MARK_KEY, Boolean.TRUE.toString());
    record(CURRENT_EXECUTED_COMPANY, "");
  }

  public static void markUnDone() {
    record(DONE_MARK_KEY, Boolean.FALSE.toString());
  }

  public static boolean isDone() {
    return Boolean.valueOf(logProperties.getProperty(DONE_MARK_KEY));
  }
}

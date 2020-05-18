package main;

import datasource.ConnectionManager;
import datasource.LiquibaseManager;
import entity.Company;
import executor.DocumentRecipientGroupTableExecutor;
import executor.DocumentRequestTableExecutor;
import executor.DocumentRequestsUserTableExecutor;
import executor.DocumentTableExecutor;
import executor.FolderTableExecutor;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import util.BackupUtils;
import util.CompanyUtils;
import util.ExecuteLogUtils;

@Slf4j
public class TenantInitializer {

  public static final String DATABASE_PREV_NAME = "tenant_";

  private static TenantInitializer tenantInitializer;

  static TenantInitializer get() {
    if(tenantInitializer == null) {
      tenantInitializer = new TenantInitializer();
    }
    return tenantInitializer;
  }

  void start() throws Exception {
    if (ExecuteLogUtils.isDone()) {
      log.info("According the execute-log, the data migrating had been done.");
      return;
    }
    BackupUtils.dump();
    List<Company> companies = CompanyUtils.getCompaniesFromCompanyService();
    List<Company> filterCompanies = ExecuteLogUtils.filterUndoneCompaniesByLog(companies);
    log.info("============== START ===============");
    for (Company company : filterCompanies) {
      String id = company.getId();
      String databaseName = DATABASE_PREV_NAME + id;
      String currentExecutedCompanyId = ExecuteLogUtils.getCurrentExecutedCompanyId();
      if(id.equals(currentExecutedCompanyId)) {
        dropDatabase(databaseName);
      }
      try {
        ExecuteLogUtils.recordCurrentExecutedCompanyId(id);
        createTenantDatabaseByCompany(databaseName);
        initDatabaseByCompany(company);
        ExecuteLogUtils.recordCompany(company, true);
      } catch (Exception e) {
        dropDatabase(databaseName);
        throw e;
      } finally {
        ConnectionManager.commit();
      }
    }
    ExecuteLogUtils.markDone();
    log.info("============== COMPLETE ===============");
  }

  private void createTenantDatabaseByCompany(String databaseName) throws Exception {
    log.info("Init database by Liquibase.");
    LiquibaseManager.initDatabaseSchema(databaseName);
    LiquibaseManager.improveDatabaseChangeLogTable(databaseName);
  }

  private void initDatabaseByCompany(Company company) throws Exception {
    String companyId = company.getId();
    initNormalTables(companyId);
  }

  private void initNormalTables(String companyId) throws Exception {
    FolderTableExecutor.get(companyId).execute();
    DocumentTableExecutor.get(companyId).execute();
    DocumentRecipientGroupTableExecutor.get(companyId).execute();
    DocumentRequestTableExecutor.get(companyId).execute();
    DocumentRequestsUserTableExecutor.get(companyId).execute();
  }

  private static void dropDatabase(String fullDatabaseName) {
    String sql = "DROP DATABASE ";
    Connection connection = null;
    try {
      connection = ConnectionManager.getTenantConnection();
    } catch (Exception e) {
      log.error("Tenant Database connection is lost.");
      e.printStackTrace();
    }
    if (connection != null) {
      try(Statement statement = connection.createStatement()) {
          statement.execute(sql + fullDatabaseName);
      } catch (SQLException e) {
        log.error("Database {} drop failed.", fullDatabaseName);
        e.printStackTrace();
      }
    } else {
      log.error("Database {} drop failed.", fullDatabaseName);
    }
  }
}

package main;

import database.DataSource;
import database.DataSourceConfig;
import executor.qrtz.QrtzBlobTriggersExecutor;
import executor.qrtz.QrtzCalendarsExecutor;
import executor.qrtz.QrtzCronTriggersExecutor;
import executor.qrtz.QrtzFiredTriggersExecutor;
import executor.qrtz.QrtzJobDetailsExecutor;
import executor.qrtz.QrtzLocksExecutor;
import executor.qrtz.QrtzPausedTriggerGrpsExecutor;
import executor.qrtz.QrtzSchedulerStateExecutor;
import executor.qrtz.QrtzSimpleTriggersExecutor;
import executor.qrtz.QrtzSimpropTriggersExecutor;
import executor.qrtz.QrtzTriggersExecutor;
import executor.user.SystemAnnouncementExecutor;
import lombok.extern.slf4j.Slf4j;
import manager.LiquibaseManager;
import manager.TenantManager;
import util.CompanyIdUtils;
import util.ExecuteLogUtils;
import util.ExecuteLogUtils.ExecuteStatus;
import java.sql.Connection;
import java.sql.Statement;
import java.util.List;

@Slf4j
public class Main {

  public static void main(final String[] args) throws Exception {
    final List<String> companyIds = CompanyIdUtils.getAllCompanyIds();
    final List<String> filterCompanyIds = ExecuteLogUtils.filterUndoneCompaniesByLog(companyIds);

    log.info("=============== Start ===============");
    LiquibaseManager.initDefaultDatabaseSchema();
    // Table 'system_announcements' in schema 'company_default'.
    if (ExecuteLogUtils.companyRecorded("system_announcements")
        && !ExecuteLogUtils.companyExecuted("system_announcements")) {
      SystemAnnouncementExecutor.getInstance().execute();
      ExecuteLogUtils.recordCompany("system_announcements", ExecuteLogUtils.ExecuteStatus.SUCCESS);
    }
    // QRTZ module
    QrtzFiredTriggersExecutor.getInstance().execute();
    QrtzJobDetailsExecutor.getInstance().execute();
    QrtzTriggersExecutor.getInstance().execute();
    QrtzSimpleTriggersExecutor.getInstance().execute();
    QrtzSimpropTriggersExecutor.getInstance().execute();
    QrtzCronTriggersExecutor.getInstance().execute();
    QrtzBlobTriggersExecutor.getInstance().execute();
    QrtzSchedulerStateExecutor.getInstance().execute();
    QrtzCalendarsExecutor.getInstance().execute();
    QrtzPausedTriggerGrpsExecutor.getInstance().execute();
    QrtzLocksExecutor.getInstance().execute();
    for (final String companyId : filterCompanyIds) {
      if (ExecuteLogUtils.companyRecorded(companyId)
          && !ExecuteLogUtils.companyExecuted(companyId)) {
        log.error(
            String.format(
                "Tenant with id: %s hasn't been executed successfully and drop it to retry.",
                companyId));
        TenantManager.dropTenantDatabase(companyId);
      }
      boolean success = false;
      int retryCount = 0;
      while (!success && retryCount < 5) {
        try {
          LiquibaseManager.initTenantDatabaseSchema(companyId);
          ExecuteLogUtils.recordCompany(companyId, ExecuteStatus.EXECUTING);
          TenantManager.migrateDataByCompanyId(companyId);
          // record to execution log
          ExecuteLogUtils.recordCompany(companyId, ExecuteStatus.SUCCESS);
          success = true;
          retryCount = 0;
        } catch (final Exception e) {
          retryCount += 1;
          log.error(
              String.format("Tenant with id: %s init failed, retry %d.", companyId, retryCount), e);
          ExecuteLogUtils.recordCompany(companyId, ExecuteStatus.FAILED);
          ExecuteLogUtils.recordFailedLog(
              String.format("Failed reason for company: %s", companyId), e.getMessage());
          TenantManager.dropTenantDatabase(companyId);
        }
      }
    }
    try (final Connection connection = DataSource.getTargetConnection()) {
      // Delete duplicate company record in default schema table 'tenants' due to
      // some unexpected suspension of the script
      String sql =
          String.format(
              "delete from %s.tenants "
                  + "where `name` not in ( SELECT * FROM (select `name` from %s.tenants group by `name`) AS S)",
              DataSourceConfig.getDefaultSchema(), DataSourceConfig.getDefaultSchema());
      try (final Statement statement = connection.createStatement()) {
        statement.execute(sql);
      }
    }
    ExecuteLogUtils.markDone();
    log.info("============== COMPLETE ===============");
  }
}

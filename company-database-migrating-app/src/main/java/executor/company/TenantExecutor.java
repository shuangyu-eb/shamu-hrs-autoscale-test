package executor.company;

import database.DataSourceConfig;

public class TenantExecutor extends CompanyExecutor{

  private TenantExecutor(final String companyId) {
    super(companyId);
    tableName = "companies";
    schemaName = DataSourceConfig.getDefaultSchema();
    tableFullName = schemaName + "." + "tenants";
  }

  public static TenantExecutor getInstance(final String companyId) {
    return new TenantExecutor(companyId);
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, company_id, name, image_url, "
            + "EIN, country_id, created_at, "
            + "updated_at, is_paid_holidays_auto_enroll) "
            + "VALUES(unhex(replace(uuid(), '-', '')), ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }
}

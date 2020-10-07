package executor.company;

import entity.company.Company;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class CompanyExecutor extends BaseExecutor<Company> {

  protected CompanyExecutor(final String companyId) {
    super(companyId, "company");
  }

  public static CompanyExecutor getInstance(final String companyId) {
    return new CompanyExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql = "SELECT * FROM %s " + "WHERE id = unhex(?)";
    return String.format(sql, "companies");
  }

  @Override
  protected Company buildEntityByResultSet(final ResultSet resultSet) throws Exception {
    final Company company = new Company();
    company.setName(resultSet.getString("name"));
    company.setImageUrl(resultSet.getString("image_url"));
    company.setEin(resultSet.getString("EIN"));
    company.setCountryId(resultSet.getBinaryStream("country_id"));
    company.setIsPaidHolidaysAutoEnroll(resultSet.getBoolean("is_paid_holidays_auto_enroll"));
    return company;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, name, image_url, "
            + "EIN, country_id, created_at, "
            + "updated_at, is_paid_holidays_auto_enroll) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(final PreparedStatement statement, final Company entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getName());
    statement.setString(3, entity.getImageUrl());
    statement.setString(4, entity.getEin());
    statement.setBinaryStream(5, getRelatedForeignKeyByRefId("countries", entity.getCountryId()));
    statement.setTimestamp(6, entity.getCreatedAt());
    statement.setTimestamp(7, entity.getUpdatedAt());
    statement.setBoolean(8, entity.getIsPaidHolidaysAutoEnroll());
  }
}

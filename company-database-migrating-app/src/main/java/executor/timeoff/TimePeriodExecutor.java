package executor.timeoff;

import entity.timeoff.TimePeriod;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class TimePeriodExecutor extends BaseExecutor<TimePeriod> {

  private TimePeriodExecutor(final String companyId) {
    super(companyId, "time_period");
  }

  public static TimePeriodExecutor getInstance(final String companyId) {
    return new TimePeriodExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT tp.* FROM company.time_period tp "
            + "WHERE tp.id IN (SELECT t.time_period_id FROM company.timesheets t "
            + "WHERE t.employee_id IN ("
            + "SELECT u.id FROM company.users u WHERE u.company_id=unhex(?)))";
    return String.format(sql, tableName);
  }

  @Override
  protected TimePeriod buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final TimePeriod timePeriod = new TimePeriod();
    timePeriod.setEndDate(resultSet.getTimestamp("end_date"));
    timePeriod.setStartDate(resultSet.getTimestamp("start_date"));
    return timePeriod;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, start_date, end_date, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, TimePeriod entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setTimestamp(2, entity.getStartDate());
    statement.setTimestamp(3, entity.getEndDate());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}

package executor.job;

import entity.job.Job;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JobExecutor extends BaseExecutor<Job> {

  private JobExecutor(final String companyId) {
    super(companyId, "jobs");
  }

  public static JobExecutor getInstance(final String companyId) {
    return new JobExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s " + "WHERE company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected Job buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final Job job = new Job();
    job.setTitle(resultSet.getString("title"));
    return job;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, title, created_at, " + "updated_at) " + "VALUES(?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, Job entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getTitle());
    statement.setTimestamp(3, entity.getCreatedAt());
    statement.setTimestamp(4, entity.getUpdatedAt());
  }
}

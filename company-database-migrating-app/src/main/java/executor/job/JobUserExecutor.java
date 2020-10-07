package executor.job;

import entity.job.JobUser;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class JobUserExecutor extends BaseExecutor<JobUser> {

  private JobUserExecutor(final String companyId) {
    super(companyId, "jobs_users");
  }

  public static JobUserExecutor getInstance(final String companyId) {
    return new JobUserExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT ju.* FROM %s ju "
            + "LEFT JOIN users u ON u.id = ju.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected JobUser buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final JobUser jobUser = new JobUser();
    jobUser.setUserId(resultSet.getBinaryStream("user_id"));
    jobUser.setJobId(resultSet.getBinaryStream("job_id"));
    jobUser.setEmploymentTypeId(resultSet.getBinaryStream("employment_type_id"));
    jobUser.setStartDate(resultSet.getTimestamp("start_date"));
    jobUser.setEndDate(resultSet.getTimestamp("end_date"));
    jobUser.setOfficeId(resultSet.getBinaryStream("office_id"));
    jobUser.setUserCompensationId(resultSet.getBinaryStream("user_compensation_id"));
    jobUser.setEmployeeTypeId(resultSet.getBinaryStream("employee_type_id"));
    jobUser.setDepartmentId(resultSet.getBinaryStream("department_id"));
    return jobUser;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, job_id, "
            + "employment_type_id, start_date, end_date, "
            + "office_id, created_at, updated_at, "
            + "user_compensation_id, employee_type_id, department_id) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, JobUser entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setBinaryStream(3, entity.getJobId());
    statement.setBinaryStream(
        4, getRelatedForeignKeyByName("employment_types", entity.getEmploymentTypeId()));
    statement.setTimestamp(5, entity.getStartDate());
    statement.setTimestamp(6, entity.getEndDate());
    statement.setBinaryStream(7, entity.getOfficeId());
    statement.setTimestamp(8, entity.getCreatedAt());
    statement.setTimestamp(9, entity.getUpdatedAt());
    statement.setBinaryStream(10, entity.getUserCompensationId());
    statement.setBinaryStream(
        11, getRelatedForeignKeyByName("employee_types", entity.getEmployeeTypeId()));
    statement.setBinaryStream(12, entity.getDepartmentId());
  }
}

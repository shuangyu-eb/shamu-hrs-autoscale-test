package executor.user;

import entity.user.UserEducation;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserEducationExecutor extends BaseExecutor<UserEducation> {

  private UserEducationExecutor(final String companyId) {
    super(companyId, "user_educations");
  }

  public static UserEducationExecutor getInstance(final String companyId) {
    return new UserEducationExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT ue.* FROM %s ue "
            + "LEFT JOIN users u ON u.id = ue.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected UserEducation buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final UserEducation userEducation = new UserEducation();
    userEducation.setUserId(resultSet.getBinaryStream("user_id"));
    userEducation.setSchool(resultSet.getString("school"));
    userEducation.setDegree(resultSet.getString("degree"));
    userEducation.setMajor(resultSet.getString("major"));
    userEducation.setGpa(resultSet.getString("gpa"));
    userEducation.setStartDate(resultSet.getTimestamp("start_date"));
    userEducation.setEndDate(resultSet.getTimestamp("end_date"));
    return userEducation;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, user_id, school, "
            + "degree, major, gpa, "
            + "start_date, end_date, created_at, "
            + "updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, UserEducation entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setString(3, entity.getSchool());
    statement.setString(4, entity.getDegree());
    statement.setString(5, entity.getMajor());
    statement.setString(6, entity.getGpa());
    statement.setTimestamp(7, entity.getStartDate());
    statement.setTimestamp(8, entity.getEndDate());
    statement.setTimestamp(9, entity.getCreatedAt());
    statement.setTimestamp(10, entity.getUpdatedAt());
  }
}

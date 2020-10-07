package executor.user;

import entity.user.UserContactInformation;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserContactInformationExecutor extends BaseExecutor<UserContactInformation> {

  private UserContactInformationExecutor(final String companyId) {
    super(companyId);
    this.tableName = "user_contact_information";
    this.tableFullName = schemaName + "." + tableName;
  }

  public static UserContactInformationExecutor getInstance(final String companyId) {
    return new UserContactInformationExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT uci.* "
            + "FROM %s uci "
            + "LEFT JOIN users u ON uci.id = u.user_contact_information_id "
            + "WHERE u.company_id = unhex(?) ";
    return String.format(sql, tableName);
  }

  @Override
  protected UserContactInformation buildEntityByResultSet(final ResultSet resultSet)
      throws Exception {
    final UserContactInformation entity = new UserContactInformation();
    entity.setEmailHome(resultSet.getString("email_home"));
    entity.setEmailWork(resultSet.getString("email_work"));
    entity.setPhoneHome(resultSet.getString("phone_work"));
    entity.setPhoneMobile(resultSet.getString("phone_mobile"));
    entity.setPhoneWork(resultSet.getString("phone_work"));
    entity.setPhoneWorkExtension(resultSet.getString("phone_work_extension"));
    return entity;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s "
            + "(id, phone_work, phone_work_extension, "
            + "phone_mobile, phone_home, email_work, "
            + "email_home, created_at, updated_at) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(
      final PreparedStatement statement, final UserContactInformation entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getPhoneWork());
    statement.setString(3, entity.getPhoneWorkExtension());
    statement.setString(4, entity.getPhoneMobile());
    statement.setString(5, entity.getPhoneHome());
    statement.setString(6, entity.getEmailWork());
    statement.setString(7, entity.getEmailHome());
    statement.setTimestamp(8, entity.getCreatedAt());
    statement.setTimestamp(9, entity.getUpdatedAt());
  }
}

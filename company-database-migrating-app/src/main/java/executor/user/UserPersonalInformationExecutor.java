package executor.user;

import entity.user.UserPersonalInformation;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserPersonalInformationExecutor extends BaseExecutor<UserPersonalInformation> {

  private UserPersonalInformationExecutor(final String companyId) {
    super(companyId);
    this.tableName = "user_personal_information";
    this.tableFullName = schemaName + "." + tableName;
  }

  public static UserPersonalInformationExecutor getInstance(final String companyId) {
    return new UserPersonalInformationExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT upi.* "
            + "FROM %s upi "
            + "LEFT JOIN users u ON upi.id = u.user_personal_information_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected UserPersonalInformation buildEntityByResultSet(final ResultSet resultSet)
      throws Exception {
    final UserPersonalInformation entity = new UserPersonalInformation();
    entity.setFirstName(resultSet.getString("first_name"));
    entity.setMiddleName(resultSet.getString("middle_name"));
    entity.setLastName(resultSet.getString("last_name"));
    entity.setPreferredName(resultSet.getString("preferred_name"));
    entity.setBirthDate(resultSet.getTimestamp("birth_date"));
    entity.setSsn(resultSet.getString("ssn"));
    entity.setGenderId(resultSet.getBinaryStream("gender_id"));
    entity.setMaritalStatusId(resultSet.getBinaryStream("marital_status_id"));
    entity.setEthnicityId(resultSet.getBinaryStream("ethnicity_id"));
    entity.setCitizenshipStatusId(resultSet.getBinaryStream("citizenship_status_id"));
    return entity;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s "
            + "(id, first_name, middle_name, "
            + "last_name, preferred_name, birth_date, "
            + "ssn, gender_id, marital_status_id, "
            + "ethnicity_id, citizenship_status_id, "
            + "created_at, updated_at) "
            + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(
      final PreparedStatement statement, final UserPersonalInformation entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setString(2, entity.getFirstName());
    statement.setString(3, entity.getMiddleName());
    statement.setString(4, entity.getLastName());
    statement.setString(5, entity.getPreferredName());
    statement.setTimestamp(6, entity.getBirthDate());
    statement.setString(7, entity.getSsn());
    statement.setBinaryStream(8, getRelatedForeignKeyByRefId("genders", entity.getGenderId()));
    statement.setBinaryStream(9, getRelatedForeignKeyByRefId("marital_status", entity.getMaritalStatusId()));
    statement.setBinaryStream(10, getRelatedForeignKeyByRefId("ethnicities", entity.getEthnicityId()));
    statement.setBinaryStream(11, entity.getCitizenshipStatusId());
    statement.setTimestamp(12, entity.getCreatedAt());
    statement.setTimestamp(13, entity.getUpdatedAt());
  }
}

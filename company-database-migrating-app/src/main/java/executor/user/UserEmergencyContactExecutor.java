package executor.user;

import entity.user.UserEmergencyContact;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserEmergencyContactExecutor extends BaseExecutor<UserEmergencyContact> {

  private UserEmergencyContactExecutor(final String companyId) {
    super(companyId, "user_emergency_contacts");
  }

  public static UserEmergencyContactExecutor getInstance(final String companyId) {
    return new UserEmergencyContactExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT uec.* FROM %s uec "
            + "LEFT JOIN users u ON u.id = uec.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected UserEmergencyContact buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final UserEmergencyContact userEmergencyContact = new UserEmergencyContact();
    userEmergencyContact.setUserId(resultSet.getBinaryStream("user_id"));
    userEmergencyContact.setFirstName(resultSet.getString("first_name"));
    userEmergencyContact.setLastName(resultSet.getString("last_name"));
    userEmergencyContact.setRelationship(resultSet.getString("relationship"));
    userEmergencyContact.setPhone(resultSet.getString("phone"));
    userEmergencyContact.setEmail(resultSet.getString("email"));
    userEmergencyContact.setStreet1(resultSet.getString("street_1"));
    userEmergencyContact.setStreet2(resultSet.getString("street_2"));
    userEmergencyContact.setCity(resultSet.getString("city"));
    userEmergencyContact.setStateId(resultSet.getBinaryStream("state_id"));
    userEmergencyContact.setPostalCode(resultSet.getString("postal_code"));
    userEmergencyContact.setIsPrimary(resultSet.getBoolean("is_primary"));
    userEmergencyContact.setCountryId(resultSet.getBinaryStream("country_id"));
    return userEmergencyContact;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, first_name, "
            + "last_name, relationship, phone, "
            + "email, street_1, street_2, "
            + "city, state_id, postal_code, "
            + "is_primary, created_at, updated_at, "
            + "country_id) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, UserEmergencyContact entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setString(3, entity.getFirstName());
    statement.setString(4, entity.getLastName());
    statement.setString(5, entity.getRelationship());
    statement.setString(6, entity.getPhone());
    statement.setString(7, entity.getEmail());
    statement.setString(8, entity.getStreet1());
    statement.setString(9, entity.getStreet2());
    statement.setString(10, entity.getCity());
    statement.setBinaryStream(11, getRelatedForeignKeyByName("states_provinces", entity.getStateId()));
    statement.setString(12, entity.getPostalCode());
    statement.setBoolean(13, entity.getIsPrimary());
    statement.setTimestamp(14, entity.getCreatedAt());
    statement.setTimestamp(15, entity.getUpdatedAt());
    statement.setBinaryStream(16, getRelatedForeignKeyByRefId("countries", entity.getCountryId()));
  }
}

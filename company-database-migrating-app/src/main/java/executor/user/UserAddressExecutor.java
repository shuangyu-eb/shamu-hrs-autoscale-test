package executor.user;

import lombok.extern.slf4j.Slf4j;
import org.zeroturnaround.zip.commons.IOUtils;

import entity.user.UserAddress;
import executor.BaseExecutor;
import util.UuidUtils;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

@Slf4j
public class UserAddressExecutor extends BaseExecutor<UserAddress> {

  private UserAddressExecutor(final String companyId) {
    super(companyId, "user_addresses");
  }

  public static UserAddressExecutor getInstance(final String companyId) {
    return new UserAddressExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT ua.* FROM %s ua "
            + "WHERE ua.user_id IN ("
            + "SELECT u.id FROM users u WHERE u.company_id = unhex(?))";
    return String.format(sql, tableName);
  }

  @Override
  protected UserAddress buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final UserAddress userAddress = new UserAddress();
    userAddress.setUserId(resultSet.getBinaryStream("user_id"));
    userAddress.setStreet1(resultSet.getString("street_1"));
    userAddress.setStreet2(resultSet.getString("street_2"));
    userAddress.setStateProvinceId(resultSet.getBinaryStream("state_province_id"));
    userAddress.setCountryId(resultSet.getBinaryStream("country_id"));
    userAddress.setPostalCode(resultSet.getString("postal_code"));
    userAddress.setCity(resultSet.getString("city"));
    return userAddress;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, user_id, street_1, "
            + "street_2, state_province_id, country_id, "
            + "postal_code, created_at, updated_at, "
            + "city) "
            + "VALUES(unhex(?), unhex(?), ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, UserAddress entity)
      throws Exception {
    byte[] addressIdBytes = IOUtils.toByteArray(entity.getId());
    UUID addressIdUUID = UuidUtils.fromBytes(addressIdBytes);
    byte[] userIdBytes = IOUtils.toByteArray(entity.getUserId());
    UUID userIdUUID = UuidUtils.fromBytes(userIdBytes);
    log.info("starting buildInsertStatement: id={}, userId={}", addressIdUUID.toString(), userIdUUID.toString());
    statement.setObject(1, addressIdUUID.toString().replace("-",""));
    statement.setObject(2, userIdUUID.toString().replace("-",""));
    statement.setString(3, entity.getStreet1());
    statement.setString(4, entity.getStreet2());
    statement.setBinaryStream(5, getRelatedForeignKeyByName("states_provinces", entity.getStateProvinceId()));
    statement.setBinaryStream(6, getRelatedForeignKeyByRefId("countries", entity.getCountryId()));
    statement.setString(7, entity.getPostalCode());
    statement.setTimestamp(8, entity.getCreatedAt());
    statement.setTimestamp(9, entity.getUpdatedAt());
    statement.setString(10, entity.getCity());
  }
}

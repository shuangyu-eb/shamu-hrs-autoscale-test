package executor.user;

import entity.user.User;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class UserExecutor extends BaseExecutor<User> {

  private UserExecutor(final String companyId) {
    super(companyId);
    tableName = "users";
    tableFullName = schemaName + "." + tableName;
    shouldDealWithSelfReference = Boolean.TRUE;
  }

  public static UserExecutor getInstance(final String companyId) {
    return new UserExecutor(companyId);
  }

  @Override
  protected List<User> dealWithSelfReference(List<User> entities) {
    Queue<User> userQueue = new LinkedList<>(entities);
    // use LinkedHashMap to order users to deal with self reference problems
    HashMap<String, User> userHashMap = new LinkedHashMap<>();
    while (!userQueue.isEmpty()) {
      User user = userQueue.poll();
      List<String> userIds = userQueue.stream().map(User::getHexedId).collect(Collectors.toList());
      if (user.getHexedManagerUserId() == null || userHashMap.containsKey(user.getHexedManagerUserId())) {
        userHashMap.put(user.getHexedId(), user);
      }
      else if (userIds.contains(user.getHexedManagerUserId())) {
        userQueue.offer(user);
      }
      else {
        // filter invalid users whose manager not in the same company as others
        user.setHexedManagerUserId(null);
        user.setManagerUserId(null);
        userHashMap.put(user.getHexedId(), user);
      }
    }
    return new ArrayList<>(userHashMap.values());
  }

  @Override
  protected String getQuerySql() {
    return "SELECT *, hex(id) as hexed_id, hex(manager_user_id) as hexed_manager_user_id "
        + "FROM users WHERE company_id = unhex(?)";
  }

  @Override
  protected User buildEntityByResultSet(final ResultSet resultSet) throws Exception {
    final User user = new User();
    user.setHexedId(resultSet.getString("hexed_id"));
    user.setLatestLogin(resultSet.getTimestamp("latest_login"));
    user.setUserStatusId(
        getRelatedForeignKeyByRefId("user_statuses", resultSet.getBinaryStream("user_status_id")));
    user.setImageUrl(resultSet.getString("image_url"));
    user.setManagerUserId(resultSet.getBinaryStream("manager_user_id"));
    user.setHexedManagerUserId(resultSet.getString("hexed_manager_user_id"));
    user.setUserPersonalInformationId(resultSet.getBinaryStream("user_personal_information_id"));
    user.setUserContactInformationId(resultSet.getBinaryStream("user_contact_information_id"));
    user.setInvitationEmailToken(resultSet.getString("invitation_email_token"));
    user.setInvitedAt(resultSet.getTimestamp("invited_at"));
    user.setResetPasswordSentAt(resultSet.getTimestamp("reset_password_sent_at"));
    user.setResetPasswordToken(resultSet.getString("reset_password_token"));
    user.setVerificationToken(resultSet.getString("verification_token"));
    user.setVerifiedAt(resultSet.getTimestamp("verified_at"));
    user.setDeactivationReasonId(
        getRelatedForeignKeyByRefId(
            "deactivation_reasons", resultSet.getBinaryStream("deactivation_reason_id")));
    user.setDeactivatedAt(resultSet.getTimestamp("deactivated_at"));
    user.setChangeWorkEmail(resultSet.getString("change_work_email"));
    user.setChangeWorkEmailToken(resultSet.getString("change_work_email_token"));
    user.setVerifyChangeWorkEmailAt(resultSet.getTimestamp("verify_change_work_email_at"));
    user.setUserRoleId(
        getRelatedForeignKeyByRefId("user_roles", resultSet.getBinaryStream("user_role_id")));
    user.setSalt(resultSet.getString("salt"));
    user.setTimeZoneId(
        getRelatedForeignKeyByRefId("static_timezones", resultSet.getBinaryStream("time_zone_id")));
    user.setInvitationCapabilityFrozenAt(resultSet.getTimestamp("invitation_capability_frozen_at"));
    return user;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, latest_login, user_status_id, "
            + "image_url, manager_user_id, user_personal_information_id, "
            + "user_contact_information_id, invitation_email_token, invited_at, "
            + "reset_password_sent_at, reset_password_token, verification_token, "
            + "verified_at, created_at, updated_at, deactivation_reason_id, "
            + "deactivated_at, change_work_email, change_work_email_token, "
            + "verify_change_work_email_at, user_role_id, salt, "
            + "time_zone_id, invitation_capability_frozen_at) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(final PreparedStatement statement, final User entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setTimestamp(2, entity.getLatestLogin());
    statement.setBinaryStream(3, entity.getUserStatusId());
    statement.setString(4, entity.getImageUrl());
    statement.setBinaryStream(5, entity.getManagerUserId());
    statement.setBinaryStream(6, entity.getUserPersonalInformationId());
    statement.setBinaryStream(7, entity.getUserContactInformationId());
    statement.setString(8, entity.getInvitationEmailToken());
    statement.setTimestamp(9, entity.getInvitedAt());
    statement.setTimestamp(10, entity.getResetPasswordSentAt());
    statement.setString(11, entity.getResetPasswordToken());
    statement.setString(12, entity.getVerificationToken());
    statement.setTimestamp(13, entity.getVerifiedAt());
    statement.setTimestamp(14, entity.getCreatedAt());
    statement.setTimestamp(15, entity.getUpdatedAt());
    statement.setBinaryStream(16, entity.getDeactivationReasonId());
    statement.setTimestamp(17, entity.getDeactivatedAt());
    statement.setString(18, entity.getChangeWorkEmail());
    statement.setString(19, entity.getChangeWorkEmailToken());
    statement.setTimestamp(20, entity.getVerifyChangeWorkEmailAt());
    statement.setBinaryStream(21, entity.getUserRoleId());
    statement.setString(22, entity.getSalt());
    statement.setBinaryStream(23, entity.getTimeZoneId());
    statement.setTimestamp(24, entity.getInvitationCapabilityFrozenAt());
  }
}

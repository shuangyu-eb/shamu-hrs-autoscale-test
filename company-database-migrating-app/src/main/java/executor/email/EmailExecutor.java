package executor.email;

import database.DataSource;
import database.DataSourceConfig;
import entity.email.Email;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Slf4j
public class EmailExecutor {
  private static final String TABLE_NAME = "emails";

  private final String targetTableName;

  private final String companyId;

  private EmailExecutor(final String companyId) {
    String schemaName = DataSourceConfig.getTenantPrefix() + companyId;
    this.targetTableName = schemaName + "." + TABLE_NAME;
    this.companyId = companyId;
  }

  public static EmailExecutor getInstance(final String companyId) {
    return new EmailExecutor(companyId);
  }

  public void execute() throws Exception {
    log.info("Start execute, tableName: {}", TABLE_NAME);

    try (final Connection originConnection = DataSource.getOriginConnection();
        final Connection targetConnection = DataSource.getTargetConnection();
        final PreparedStatement targetConnectionStatement =
            targetConnection.prepareStatement(getInsertSql())) {

      final String selectEmailsWithNullUserId =
          String.format(
              "SELECT e.* FROM %s e "
                  + "WHERE e.user_id IS NULL "
                  + "AND e.to IN ("
                  + "SELECT uci.email_work FROM user_contact_information uci WHERE uci.id IN ("
                  + "SELECT u.user_contact_information_id FROM users u WHERE u.company_id = unhex(?)))",
              TABLE_NAME);

      try (final PreparedStatement selectWithNullUserIdStatement =
          originConnection.prepareStatement(selectEmailsWithNullUserId)) {
        selectWithNullUserIdStatement.setString(1, companyId);
        log.info(
            "email select query with null userId success: {}",
            selectWithNullUserIdStatement.toString());
        try (final ResultSet emailsWithNullUserIdResultsSet =
            selectWithNullUserIdStatement.executeQuery()) {
          while (emailsWithNullUserIdResultsSet.next()) {
            buildInsertStatement(
                targetConnectionStatement, buildEntityByResultSet(emailsWithNullUserIdResultsSet));
            targetConnectionStatement.execute();
          }
        }
      }

      final String selectEmailsWithNotNullUserId =
          String.format(
              "SELECT e.* FROM %s e "
                  + "WHERE e.user_id IS NOT NULL "
                  + "AND e.user_id IN ("
                  + "SELECT u.id FROM users u WHERE u.company_id = unhex(?))",
              TABLE_NAME);

      try (final PreparedStatement selectWithUserIdStatement =
          originConnection.prepareStatement(selectEmailsWithNotNullUserId)) {
        selectWithUserIdStatement.setString(1, companyId);
        log.info(
            "email select query with null userId success: {}",
            selectWithUserIdStatement.toString());
        try (final ResultSet emailsWithNullUserIdResultsSet =
            selectWithUserIdStatement.executeQuery()) {
          while (emailsWithNullUserIdResultsSet.next()) {
            buildInsertStatement(
                targetConnectionStatement, buildEntityByResultSet(emailsWithNullUserIdResultsSet));
            targetConnectionStatement.execute();
          }
        }
      }

      log.info("{} execute completed", TABLE_NAME);
    }
  }

  private Email buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final Email email = new Email();
    email.setId(resultSet.getBinaryStream("id"));
    email.setUserId(resultSet.getBinaryStream("user_id"));
    email.setFrom(resultSet.getString("from"));
    email.setTo(resultSet.getString("to"));
    email.setSubject(resultSet.getString("subject"));
    email.setContent(resultSet.getString("content"));
    email.setSendDate(resultSet.getTimestamp("send_date"));
    email.setSentAt(resultSet.getTimestamp("sent_at"));
    email.setRetryCount(resultSet.getInt("retry_count"));
    email.setFromName(resultSet.getString("from_name"));
    email.setToName(resultSet.getString("to_name"));
    email.setMessageId(resultSet.getString("message_id"));
    email.setStatus(resultSet.getString("status"));
    email.setCreatedAt(resultSet.getTimestamp("created_at"));
    email.setUpdatedAt(resultSet.getTimestamp("updated_at"));
    return email;
  }

  private String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, user_id, `from`, "
            + "`to`, `subject`, content, "
            + "send_date, sent_at, created_at, "
            + "updated_at, retry_count, from_name, "
            + "to_name, message_id, `status`) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(sql, targetTableName);
  }

  private void buildInsertStatement(PreparedStatement statement, Email entity) throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setString(3, entity.getFrom());
    statement.setString(4, entity.getTo());
    statement.setString(5, entity.getSubject());
    statement.setString(6, entity.getContent());
    statement.setTimestamp(7, entity.getSendDate());
    statement.setTimestamp(8, entity.getSentAt());
    statement.setTimestamp(9, entity.getCreatedAt());
    statement.setTimestamp(10, entity.getUpdatedAt());
    statement.setInt(11, entity.getRetryCount());
    statement.setString(12, entity.getFromName());
    statement.setString(13, entity.getToName());
    statement.setString(14, entity.getMessageId());
    statement.setString(15, entity.getStatus());
  }
}

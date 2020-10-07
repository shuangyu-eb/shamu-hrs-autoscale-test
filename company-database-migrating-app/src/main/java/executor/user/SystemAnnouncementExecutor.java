package executor.user;

import database.DataSourceConfig;
import entity.user.SystemAnnouncement;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SystemAnnouncementExecutor extends BaseExecutor<SystemAnnouncement> {

  private SystemAnnouncementExecutor() {
    super("");
    tableName = "system_announcements";
    schemaName = DataSourceConfig.getDefaultSchema();
    tableFullName = schemaName + "." + tableName;
    companyIdRequired = Boolean.FALSE;
  }

  public static SystemAnnouncementExecutor getInstance() {
    return new SystemAnnouncementExecutor();
  }

  @Override
  protected String getQuerySql() {
    final String querySql = "SELECT * FROM %s";
    return String.format(querySql, tableName);
  }

  @Override
  protected SystemAnnouncement buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final SystemAnnouncement systemAnnouncement = new SystemAnnouncement();
    systemAnnouncement.setUserId(resultSet.getBinaryStream("user_id"));
    systemAnnouncement.setContent(resultSet.getString("content"));
    systemAnnouncement.setIsPastAnnouncement(resultSet.getBoolean("is_past_announcement"));
    return systemAnnouncement;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, user_id, content, "
            + "is_past_announcement, created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, SystemAnnouncement entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setString(3, entity.getContent());
    statement.setBoolean(4, entity.getIsPastAnnouncement());
    statement.setTimestamp(5, entity.getCreatedAt());
    statement.setTimestamp(6, entity.getUpdatedAt());
  }
}

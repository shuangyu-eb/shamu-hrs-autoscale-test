package executor;

import datasource.ConnectionManager;
import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import main.TenantInitializer;

@Slf4j
abstract class BaseExecutor<E extends BaseEntity> {

  String tableName;

  String databaseName;

  String companyId;

  String tableFullName;

  BaseExecutor(String companyId) {
    this.databaseName = TenantInitializer.DATABASE_PREV_NAME + companyId;
    this.companyId = companyId;
  }

  public void execute() throws Exception {
    log.info("Start execute, tableName: {}, companyId: {} ", tableName, companyId);
    List<E> entities = getDataFromOldDatabase();
    insertDataToNewDatabase(entities);
    log.info("{} execute completed", tableFullName);
  }

  List<E> getDataFromOldDatabase() throws Exception {
    String sql = "SELECT * FROM " + tableName + " WHERE company_id = unhex(?) ";
    Connection connection = ConnectionManager.getDocumentConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, companyId);
      List<E> entities = new ArrayList<>();
      try(ResultSet resultSet = preparedStatement.executeQuery()) {
        while(resultSet.next()) {
          E entity = buildEntityByResultSet(resultSet);
          entities.add(entity);
        }
        return entities;
      }
    }
  }

  abstract E buildEntityByResultSet(ResultSet resultSet) throws Exception;

  abstract void insertDataToNewDatabase(List<E> entities) throws Exception;

  void buildBaseEntity(ResultSet resultSet, E entity) throws SQLException {
    entity.setId(resultSet.getBinaryStream("id"));
    entity.setCreatedAt(resultSet.getTimestamp("created_at"));
    entity.setUpdatedAt(resultSet.getTimestamp("updated_at"));
  }

  InputStream getForeignKeyByRefIdFromNewDatabase(String relatedTableName, Integer refId) throws Exception {
    String sql = "SELECT id from %s WHERE ref_id = %d";
    String relatedTableFullName = databaseName + "." + relatedTableName;
    String formatSql = String.format(sql, relatedTableFullName, refId);
    Connection connection = ConnectionManager.getTenantConnection();

    try (Statement statement = connection.createStatement()) {
      try (ResultSet resultSet = statement.executeQuery(formatSql)) {
        if (resultSet.next()) {
          return resultSet.getBinaryStream("id");
        }
      }
    }
    return null;
  }
}

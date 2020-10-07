package executor;

import database.DataSource;
import database.DataSourceConfig;
import entity.BaseEntity;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@NoArgsConstructor
public abstract class BaseExecutor<E extends BaseEntity> {

  protected String tableName;

  protected String schemaName;

  protected String companyId;

  protected String tableFullName;

  protected Boolean companyIdRequired = Boolean.TRUE;

  protected Boolean shouldDealWithSelfReference = Boolean.FALSE;

  protected BaseExecutor(final String companyId) {
    this.schemaName = DataSourceConfig.getTenantPrefix() + companyId;
    this.companyId = companyId;
  }

  protected BaseExecutor(final String companyId, final String tableName) {
    this(companyId);
    this.tableName = tableName;
    this.tableFullName = schemaName + "." + tableName;
  }

  public void execute() throws Exception {
    log.info("Start execute, tableName: {}", tableFullName);
    log.info("Get data from original database");
    List<E> entities = this.getDataFromOriginDatabase();
    log.info("Get data successfully");
    // Sort entities to avoid the problem caused by self-reference
    if (Boolean.TRUE.equals(shouldDealWithSelfReference)) {
      entities = dealWithSelfReference(entities);
    }
    log.info("Insert data to target tenant");
    insertDataToNewDatabase(entities);
    log.info("{} execute completed", tableFullName);
  }

  protected List<E> getDataFromOriginDatabase() throws Exception {
    final String sql = getQuerySql();
    try (final Connection connection = DataSource.getOriginConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      if (Boolean.TRUE.equals(companyIdRequired)) {
        preparedStatement.setString(1, companyId);
      }
      final List<E> entities = new ArrayList<>();
      try (final ResultSet resultSet = preparedStatement.executeQuery()) {
        while (resultSet.next()) {
          final E entity = getEntity(resultSet);
          entities.add(entity);
        }
        return entities;
      }
    }
  }

  protected List<E> dealWithSelfReference(List<E> entities) {
    return entities;
  }

  protected abstract String getQuerySql();

  private E getEntity(final ResultSet resultSet) throws Exception {
    final E entity = buildEntityByResultSet(resultSet);
    buildBaseEntity(resultSet, entity);
    return entity;
  }

  protected abstract E buildEntityByResultSet(ResultSet resultSet) throws Exception;

  protected void buildBaseEntity(final ResultSet resultSet, final E entity) throws SQLException {
    entity.setId(resultSet.getBinaryStream("id"));
    entity.setCreatedAt(resultSet.getTimestamp("created_at"));
    entity.setUpdatedAt(resultSet.getTimestamp("updated_at"));
  }

  protected void insertDataToNewDatabase(final List<E> entities) throws Exception {
    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(getInsertSql())) {
      for (final E entity : entities) {
        buildInsertStatement(preparedStatement, entity);
        log.info("buildInsertStatement success: {}", preparedStatement.toString());
        preparedStatement.execute();
      }
    }
  }

  protected abstract String getInsertSql();

  protected abstract void buildInsertStatement(PreparedStatement statement, E entity)
      throws Exception;

  protected InputStream getRelatedForeignKeyByRefId(final String relatedTable, final InputStream id)
      throws Exception {
    if (id == null) {
      return null;
    }

    final String originSql =
        String.format("SELECT ref_id from %s WHERE id = ? LIMIT 1", relatedTable);
    try (final Connection originConnection = DataSource.getOriginConnection();
        final PreparedStatement statement = originConnection.prepareStatement(originSql)) {
      statement.setBinaryStream(1, id);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          final Integer refId = resultSet.getInt("ref_id");
          return getRelatedIdByRefId(refId, relatedTable);
        }
        return null;
      }
    }
  }

  protected InputStream getRelatedForeignKeyByName(final String relatedTable, final InputStream id)
      throws Exception {
    if (id == null) {
      return null;
    }

    final String originSql =
        String.format("SELECT name from %s WHERE id = ? LIMIT 1", relatedTable);
    try (final Connection originConnection = DataSource.getOriginConnection();
        final PreparedStatement statement = originConnection.prepareStatement(originSql)) {
      statement.setBinaryStream(1, id);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          final String name = resultSet.getString("name");
          return getRelatedIdByName(name, relatedTable);
        }
        return null;
      }
    }
  }

  private InputStream getRelatedIdByRefId(final Integer refId, final String relatedTable)
      throws Exception {
    final String sql = "SELECT id from %s WHERE ref_id = ? LIMIT 1";
    final String relatedTableFullName = schemaName + "." + relatedTable;
    final String formatSql = String.format(sql, relatedTableFullName);
    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement statement = connection.prepareStatement(formatSql)) {
      statement.setInt(1, refId);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getBinaryStream("id");
        }
        return null;
      }
    }
  }

  private InputStream getRelatedIdByName(final String name, final String relatedTable)
      throws Exception {
    final String sql = "SELECT id from %s WHERE name = ? LIMIT 1";
    final String relatedTableFullName = schemaName + "." + relatedTable;
    final String formatSql = String.format(sql, relatedTableFullName);

    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement statement = connection.prepareStatement(formatSql)) {
      statement.setString(1, name);
      try (final ResultSet resultSet = statement.executeQuery()) {
        if (resultSet.next()) {
          return resultSet.getBinaryStream("id");
        }
        return null;
      }
    }
  }
}

package executor;

import datasource.ConnectionManager;
import entity.Document;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DocumentTableExecutor extends BaseExecutor<Document> {

  private DocumentTableExecutor(String companyId) {
    super(companyId);
    this.tableName = "documents";
    this.tableFullName = databaseName + "." + tableName;
  }

  public static DocumentTableExecutor get(String companyId) {
    return new DocumentTableExecutor(companyId);
  }

  @Override
  Document buildEntityByResultSet(ResultSet resultSet) throws SQLException {
    Document document = new Document();
    buildBaseEntity(resultSet, document);
    document.setFolderId(resultSet.getBinaryStream("folder_id"));
    document.setSize(resultSet.getBigDecimal("size"));
    document.setTitle(resultSet.getString("title"));
    document.setType(resultSet.getString("type"));
    document.setUrl(resultSet.getString("url"));
    document.setUserId(resultSet.getBinaryStream("user_id"));
    return document;
  }


  @Override
  void insertDataToNewDatabase(List<Document> documents) throws Exception {
    String sql = "INSERT INTO " + tableFullName + "( "
        + "id, title, url, user_id, folder_id, size, type, created_at, updated_at "
        + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
    Connection connection = ConnectionManager.getTenantConnection();
    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      for(Document document : documents) {
        preparedStatement.setBinaryStream(1, document.getId());
        preparedStatement.setString(2, document.getTitle());
        preparedStatement.setString(3, document.getUrl());
        preparedStatement.setBinaryStream(4, document.getUserId());
        preparedStatement.setBinaryStream(5, document.getFolderId());
        preparedStatement.setBigDecimal(6, document.getSize());
        preparedStatement.setString(7, document.getType());
        preparedStatement.setTimestamp(8, document.getCreatedAt());
        preparedStatement.setTimestamp(9, document.getUpdatedAt());
        preparedStatement.execute();
      }
    }
  }
}

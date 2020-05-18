package executor;

import datasource.ConnectionManager;
import entity.DocumentRequest;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DocumentRequestTableExecutor extends BaseExecutor<DocumentRequest> {

  private static final String FOREIGN_KEY_TABLE_NAME = "document_request_types";

  private DocumentRequestTableExecutor(String companyId) {
    super(companyId);
    this.tableName = "document_requests";
    this.tableFullName = databaseName + "." + tableName;
  }

  public static DocumentRequestTableExecutor get(String companyId) {
    return new DocumentRequestTableExecutor(companyId);
  }

  @Override
  public List<DocumentRequest> getDataFromOldDatabase() throws Exception {
    String sql = "SELECT t1.*, t2.ref_id "
        + "FROM %s t1 LEFT JOIN %s t2 "
        + "ON t1.document_request_type_id = t2.id "
        + "WHERE t1.company_id = unhex(?)";
    String formatSql = String.format(sql, tableName, FOREIGN_KEY_TABLE_NAME);
    Connection connection = ConnectionManager.getDocumentConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(formatSql)) {
      preparedStatement.setString(1, companyId);
      List<DocumentRequest> entities = new ArrayList<>();
      try(ResultSet resultSet = preparedStatement.executeQuery()) {
        while(resultSet.next()) {
          DocumentRequest entity = buildEntityByResultSet(resultSet);
          entities.add(entity);
        }
        return entities;
      }
    }
  }

  @Override
  DocumentRequest buildEntityByResultSet(ResultSet resultSet) throws Exception {
    DocumentRequest documentRequest = new DocumentRequest();
    buildBaseEntity(resultSet, documentRequest);
    documentRequest.setDocumentId(resultSet.getBinaryStream("document_id"));
    documentRequest.setSendUserId(resultSet.getBinaryStream("sender_user_id"));
    documentRequest.setExpiredAt(resultSet.getTimestamp("expired_at"));
    documentRequest.setMessage(resultSet.getString("message"));

    Integer documentRequestRefId = resultSet.getInt("ref_id");
    documentRequest.setDocumentRequestTypeId(
        getForeignKeyByRefIdFromNewDatabase(FOREIGN_KEY_TABLE_NAME, documentRequestRefId));

    return documentRequest;
  }

  @Override
  void insertDataToNewDatabase(List<DocumentRequest> entities) throws Exception {
    String sql = "INSERT INTO " + tableFullName + " ( "
        + "id, sender_user_id, expired_at, created_at, updated_at, message, "
        + "document_id, document_request_type_id "
        + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

    Connection connection = ConnectionManager.getTenantConnection();
    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      for(DocumentRequest documentRequest : entities) {
        preparedStatement.setBinaryStream(1, documentRequest.getId());
        preparedStatement.setBinaryStream(2, documentRequest.getSendUserId());
        preparedStatement.setTimestamp(3, documentRequest.getExpiredAt());
        preparedStatement.setTimestamp(4, documentRequest.getCreatedAt());
        preparedStatement.setTimestamp(5, documentRequest.getUpdatedAt());
        preparedStatement.setString(6, documentRequest.getMessage());
        preparedStatement.setBinaryStream(7, documentRequest.getDocumentId());
        preparedStatement.setBinaryStream(8, documentRequest.getDocumentRequestTypeId());
        preparedStatement.execute();
      }
    }
  }


}

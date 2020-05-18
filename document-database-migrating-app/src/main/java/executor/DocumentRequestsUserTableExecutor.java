package executor;

import datasource.ConnectionManager;
import entity.DocumentRequestsUser;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import util.CompanyUtils;

public class DocumentRequestsUserTableExecutor extends BaseExecutor<DocumentRequestsUser> {

  private static final String FOREIGN_KEY_TABLE_NAME = "recipient_action_types";

  private DocumentRequestsUserTableExecutor(String companyId) {
    super(companyId);
    this.tableName = "document_requests_users";
    this.tableFullName = databaseName + "." + tableName;
  }

  public static DocumentRequestsUserTableExecutor get(String companyId) {
    return new DocumentRequestsUserTableExecutor(companyId);
  }

  @Override
  List<DocumentRequestsUser> getDataFromOldDatabase() throws Exception {
    String sql = buildQuerySql();
    Connection connection = ConnectionManager.getDocumentConnection();
    try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      List<DocumentRequestsUser> entities = new ArrayList<>();
      try(ResultSet resultSet = preparedStatement.executeQuery()) {
        while(resultSet.next()) {
          DocumentRequestsUser entity = buildEntityByResultSet(resultSet);
          entities.add(entity);
        }
        return entities;
      }
    }
  }

  private String buildQuerySql() throws Exception {
    String sql = "SELECT t1.*, t2.ref_id "
        + "FROM %s t1 LEFT JOIN %s t2 "
        + "ON t1.recipient_action_type_id = t2.id "
        + "WHERE t1.user_id in(%s)";

    return String.format(sql, tableName, FOREIGN_KEY_TABLE_NAME, getCondition());
  }

  private String getCondition() throws Exception {
    List<String> userIds = CompanyUtils.getUserIdsByCompanyFromCompanyService(companyId);
    List<String> hexedIds = userIds.stream().map(id -> "unhex('" + id + "')").collect(Collectors.toList());
    return String.join(",", hexedIds);

  }

  @Override
  DocumentRequestsUser buildEntityByResultSet(ResultSet resultSet) throws Exception {
    DocumentRequestsUser documentRequestsUser = new DocumentRequestsUser();
    buildBaseEntity(resultSet, documentRequestsUser);
    documentRequestsUser.setRecipientGroupId(resultSet.getBinaryStream("recipient_group_id"));
    documentRequestsUser.setUserId(resultSet.getBinaryStream("user_id"));
    documentRequestsUser.setOrder(resultSet.getInt("order"));
    documentRequestsUser.setActionedAt(resultSet.getTimestamp("actioned_at"));
    documentRequestsUser.setDocumentRequestId(resultSet.getBinaryStream("document_request_id"));

    Integer refId = resultSet.getInt("ref_id");
    InputStream recipientActionTypeId = getForeignKeyByRefIdFromNewDatabase(FOREIGN_KEY_TABLE_NAME, refId);
    documentRequestsUser.setRecipientActionTypeId(recipientActionTypeId);

    return documentRequestsUser;
  }

  @Override
  void insertDataToNewDatabase(List<DocumentRequestsUser> entities) throws Exception {
    String sql = "INSERT INTO " + tableFullName + " ( "
        + "id, recipient_group_id, recipient_action_type_id, user_id, actioned_at, "
        + "created_at, updated_at, document_request_id"
        + ") VALUES (?, ?, ?, ?, ?, ?, ?,?)";

    Connection connection = ConnectionManager.getTenantConnection();
    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      for(DocumentRequestsUser documentRequestsUser : entities) {
        preparedStatement.setBinaryStream(1, documentRequestsUser.getId());
        preparedStatement.setBinaryStream(2, documentRequestsUser.getRecipientGroupId());
        preparedStatement.setBinaryStream(3, documentRequestsUser.getRecipientActionTypeId());
        preparedStatement.setBinaryStream(4, documentRequestsUser.getUserId());
        preparedStatement.setTimestamp(5, documentRequestsUser.getActionedAt());
        preparedStatement.setTimestamp(6, documentRequestsUser.getCreatedAt());
        preparedStatement.setTimestamp(7, documentRequestsUser.getUpdatedAt());
        preparedStatement.setBinaryStream(8, documentRequestsUser.getDocumentRequestId());
        preparedStatement.execute();
        preparedStatement.clearParameters();
      }
    }
  }
}

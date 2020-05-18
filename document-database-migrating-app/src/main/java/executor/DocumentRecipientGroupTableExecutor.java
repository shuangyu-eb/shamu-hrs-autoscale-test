package executor;

import datasource.ConnectionManager;
import entity.DocumentRecipientGroup;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class DocumentRecipientGroupTableExecutor extends BaseExecutor<DocumentRecipientGroup> {

  private DocumentRecipientGroupTableExecutor(String companyId) {
    super(companyId);
    this.tableName = "document_recipient_groups";
    this.tableFullName = databaseName + "." + tableName;
  }

  public static DocumentRecipientGroupTableExecutor get(String companyId) {
    return new DocumentRecipientGroupTableExecutor(companyId);
  }

  @Override
  DocumentRecipientGroup buildEntityByResultSet(ResultSet resultSet) throws SQLException {
    DocumentRecipientGroup documentRecipientGroup = new DocumentRecipientGroup();
    buildBaseEntity(resultSet, documentRecipientGroup);
    return documentRecipientGroup;
  }

  @Override
  void insertDataToNewDatabase(List<DocumentRecipientGroup> entities) throws Exception {

    String sql = "INSERT INTO " + tableFullName + "( "
        + "id, created_at, updated_at "
        + ") VALUES (?, ?, ?)";

    Connection connection = ConnectionManager.getTenantConnection();
    try(PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      for(DocumentRecipientGroup documentRecipientGroup : entities) {
        preparedStatement.setBinaryStream(1, documentRecipientGroup.getId());
        preparedStatement.setTimestamp(2, documentRecipientGroup.getCreatedAt());
        preparedStatement.setTimestamp(3, documentRecipientGroup.getUpdatedAt());
        preparedStatement.execute();
      }
    }
  }
}

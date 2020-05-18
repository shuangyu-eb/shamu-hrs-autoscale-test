package executor;

import datasource.ConnectionManager;
import entity.Folder;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class FolderTableExecutor extends BaseExecutor<Folder> {

  private FolderTableExecutor(String companyId) {
    super(companyId);
    this.tableName = "folders";
    this.tableFullName = databaseName + "." + tableName;
  }

  public static FolderTableExecutor get(String companyId) {
    return new FolderTableExecutor(companyId);
  }

  public void execute() throws Exception {
    log.info("Start execute, tableName: {}, companyId: {} ", tableName, companyId);
    List<Folder> folders = getDataFromOldDatabase();
    insertDataToNewDatabase(folders);
    log.info("{} execute completed", tableFullName);
  }

  @Override
  void insertDataToNewDatabase(List<Folder> folders) throws Exception {
    String sql = "INSERT INTO " + tableFullName + " ("
        + "id, name, created_at, updated_at "
        + ") VALUES (?, ?, ?, ?)";
    Connection connection = ConnectionManager.getTenantConnection();
    try(PreparedStatement statement = connection.prepareStatement(sql)) {
      for(Folder folder : folders) {
        statement.setBinaryStream(1, folder.getId());
        statement.setString(2, folder.getName());
        statement.setTimestamp(3, folder.getCreatedAt());
        statement.setTimestamp(4, folder.getUpdatedAt());
        statement.execute();
      }
    }
  }

  @Override
  Folder buildEntityByResultSet(ResultSet resultSet) throws SQLException {
    Folder folder = new Folder();
    buildBaseEntity(resultSet, folder);
    folder.setName(resultSet.getString("name"));
    return folder;
  }
}

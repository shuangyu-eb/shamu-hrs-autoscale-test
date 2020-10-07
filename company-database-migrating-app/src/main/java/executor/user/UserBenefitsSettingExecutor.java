package executor.user;

import entity.user.UserBenefitsSetting;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserBenefitsSettingExecutor extends BaseExecutor<UserBenefitsSetting> {

  private UserBenefitsSettingExecutor(final String companyId) {
    super(companyId, "user_benefits_setting");
  }

  public static UserBenefitsSettingExecutor getInstance(final String companyId) {
    return new UserBenefitsSettingExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String sql =
        "SELECT ubs.* FROM %s ubs "
            + "LEFT JOIN users u ON u.id = ubs.user_id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(sql, tableName);
  }

  @Override
  protected UserBenefitsSetting buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final UserBenefitsSetting userBenefitsSetting = new UserBenefitsSetting();
    userBenefitsSetting.setUserId(resultSet.getBinaryStream("user_id"));
    userBenefitsSetting.setEffectYear(resultSet.getString("effect_year"));
    return userBenefitsSetting;
  }

  @Override
  protected String getInsertSql() {
    final String sql =
        "INSERT INTO %s (id, user_id, effect_year, "
            + "created_at, updated_at) "
            + "VALUES(?, ?, ?, ?, ?)";
    return String.format(sql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, UserBenefitsSetting entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getUserId());
    statement.setString(3, entity.getEffectYear());
    statement.setTimestamp(4, entity.getCreatedAt());
    statement.setTimestamp(5, entity.getUpdatedAt());
  }
}

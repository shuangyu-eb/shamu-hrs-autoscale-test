package manager;

import database.DataSource;
import database.DataSourceConfig;
import entity.OvertimePolicy;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class TenantManager {

  private TenantManager() {}


  public static void addNaOtPolicyifDoesNotExist(String companyId, OvertimePolicy overtimePolicy) throws Exception {
      final String schema = DataSourceConfig.getTenantPrefix() + companyId;
      final String sql = "INSERT INTO " + schema + ".overtime_policies " +
              "(id, policy_name, default_policy, active, created_at, updated_at)" +
              "SELECT UNHEX(?), ?, ?, ?, ?, ? " +
              "WHERE NOT EXISTS " +
              "(SELECT id FROM "+ schema  +".overtime_policies WHERE active = 1 AND policy_name = ?)";

      try (final Connection connection = DataSource.getTargetConnection();
           final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
          preparedStatement.setString(1, overtimePolicy.getId());
          preparedStatement.setString(2, overtimePolicy.getPolicyName());
          preparedStatement.setInt(3, overtimePolicy.getDefaultPolicy());
          preparedStatement.setInt(4, overtimePolicy.getActive());
          preparedStatement.setTimestamp(5, overtimePolicy.getCreatedAt());
          preparedStatement.setTimestamp(6, overtimePolicy.getUpdatedAt());
          preparedStatement.setString(7, overtimePolicy.getPolicyName());
          System.out.println(preparedStatement.toString());
          preparedStatement.execute();
      } catch (SQLException e) {
          log.warn("", e);
          throw e;

      }
  }

    public static List<String> getAllCompanyIds() throws Exception{
        final List<String> companies = new ArrayList<>();
        final String schema = DataSourceConfig.getDefaultSchema();
        final String sql = String.format("SELECT hex(company_id) AS id FROM " + schema+".tenants");
        try (final Connection connection = DataSource.getTargetConnection();
             final Statement statement = connection.createStatement()) {
            ResultSet rs  = statement.executeQuery(sql);
            while (rs.next()) {
                final String id = rs.getString("id");
                companies.add(id);
            }
            return companies;
        } catch (final SQLException e) {
            log.error(String.format("Get Address Failed.", schema), e);
            throw e;
        }
    }

}

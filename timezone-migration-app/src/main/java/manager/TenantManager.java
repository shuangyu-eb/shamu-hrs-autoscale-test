package manager;

import database.DataSource;
import database.DataSourceConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import entity.TimezoneAddress;
import formatter.TimezoneFormatter;

@Slf4j
public abstract class TenantManager {

  private TenantManager() {}

  public static void addTimezone(final String companyId, InputStream address_id, final String timezone) throws Exception {
      final String schema = DataSourceConfig.getTenantPrefix() + companyId;
      final String sql = "UPDATE " + schema + ".office_addresses SET time_zone_id= (" +
              "SELECT id FROM " + schema + ".static_timezones WHERE name=?) " +
              "WHERE id=?";
      try (final Connection connection = DataSource.getTargetConnection();
           final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
          preparedStatement.setString(1, timezone);
          preparedStatement.setBinaryStream(2, address_id);
          preparedStatement.execute();
      } catch (final SQLException e) {
          log.error(String.format("Update Failed.", schema), e);
      }
  }

  public static List<TimezoneAddress> getAddressesWithoutTimezones(String companyId ) throws Exception{
      final String schema = DataSourceConfig.getTenantPrefix() + companyId;
      final String sql = "SELECT * FROM " + schema + ".office_addresses " +
              "LEFT JOIN " + schema +  ".states_provinces ON " +
              "office_addresses.state_province_id = states_provinces.id " +
              "WHERE time_zone_id IS NULL";
      try (final Connection connection = DataSource.getTargetConnection();
           final Statement statement = connection.createStatement()) {
          ResultSet  rs  = statement.executeQuery(sql);
          return TimezoneFormatter.formatTimezones(rs);
      } catch (final SQLException e) {
          log.error(String.format("Get Address Failed.", schema), e);
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

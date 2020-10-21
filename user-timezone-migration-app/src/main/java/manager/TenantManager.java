package manager;

import database.DataSource;
import database.DataSourceConfig;
import entity.TimezoneAddress;
import formatter.TimezoneFormatter;
import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class TenantManager {

  private TenantManager() {}

  public static void addUserTimezone(
      final String companyId, final String timezoneId, final String userId) throws Exception {
    final String schema = DataSourceConfig.getTenantPrefix() + companyId;
    final String sql =
        "UPDATE " + schema + ".users SET time_zone_id= unhex(?) " + "WHERE id=unhex(?)";
    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, timezoneId);
      preparedStatement.setString(2, userId);
      preparedStatement.execute();
    } catch (final SQLException e) {
      log.error(String.format("Update Failed.", schema), e);
    }
  }

  public static List<String> getAllCompanyIds() throws Exception {
    final List<String> companies = new ArrayList<>();
    final String schema = DataSourceConfig.getDefaultSchema();
    final String sql = String.format("SELECT hex(company_id) AS id FROM " + schema + ".tenants");
    try (final Connection connection = DataSource.getTargetConnection();
        final Statement statement = connection.createStatement()) {
      final ResultSet rs = statement.executeQuery(sql);
      while (rs.next()) {
        final String id = rs.getString("id");
        companies.add(id);
      }
      return companies;
    } catch (final SQLException e) {
      log.error(String.format("Get Companies Failed.", schema), e);
      throw e;
    }
  }

  public static List<String> getAllUsersWithTimezoneUnset(final String companyId) throws Exception {
    final List<String> userIds = new ArrayList<>();
    final String schema = DataSourceConfig.getTenantPrefix() + companyId;
    final String sql =
        String.format(
            "SELECT hex(id) as id FROM "
                + schema
                + ".users WHERE time_zone_id IS NULL "
                + "and "
                + "verified_at is not null and deactivated_at is null");
    try (final Connection connection = DataSource.getTargetConnection();
        final Statement statement = connection.createStatement()) {
      final ResultSet rs = statement.executeQuery(sql);
      while (rs.next()) {
        final String id = rs.getString("id");
        userIds.add(id);
      }
      return userIds;
    } catch (final SQLException e) {
      log.error(String.format("Get Users With Timezone Unset Failed.", schema), e);
      throw e;
    }
  }

  public static String getUserOfficeAddressTimezoneId(final String companyId, final String userId)
      throws Exception {
    final String schema = DataSourceConfig.getTenantPrefix() + companyId;
    final String sql =
        String.format(
            "select hex(time_zone_id) as id from "
                + schema
                + ".office_addresses where id = (select office_address_id from "
                + schema
                + ".offices where id = (select office_id from "
                + schema
                + ".jobs_users where user_id = unhex(?)))");
    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, userId);
      final ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        return rs.getString("id");
      }
      return null;
    } catch (final SQLException e) {
      log.error(String.format("Get Office Timezone Failed.", schema), e);
      throw e;
    }
  }

  public static TimezoneAddress getHomeAddress(final String companyId, final String userId)
      throws Exception {
    final String schema = DataSourceConfig.getTenantPrefix() + companyId;
    final String sql =
        "SELECT * FROM "
            + schema
            + ".user_addresses "
            + "LEFT JOIN "
            + schema
            + ".states_provinces ON "
            + "user_addresses.state_province_id = states_provinces.id "
            + "WHERE user_id = unhex(?)";
    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, userId);
      final ResultSet rs = preparedStatement.executeQuery();
      final List<TimezoneAddress> timezoneAddresses = TimezoneFormatter.formatTimezones(rs);
      if (timezoneAddresses.size() == 0) {
        return null;
      } else {
        return timezoneAddresses.get(0);
      }
    } catch (final SQLException e) {
      log.error(String.format("Get Home Address Failed.", schema), e);
      throw e;
    }
  }

  public static String findOfficeAddressIdWithMostEmployees(final String companyId)
      throws Exception {
    final String schema = DataSourceConfig.getTenantPrefix() + companyId;
    final String sql =
        "select hex(time_zone_id) as id, count(*) as count from "
            + schema
            + ".office_addresses group by time_zone_id order by count desc limit 1";
    try (final Connection connection = DataSource.getTargetConnection();
        final Statement statement = connection.createStatement()) {
      final ResultSet rs = statement.executeQuery(sql);
      if (rs.next()) {
        return rs.getString("id");
      }
      return null;
    } catch (final SQLException e) {
      log.error(String.format("Get Office Address With Most Employees Failed.", schema), e);
      throw e;
    }
  }

  public static String findTimezoneIdByName(final String companyId, final String timezone)
      throws Exception {
    final String schema = DataSourceConfig.getTenantPrefix() + companyId;
    final String sql =
        String.format(
            "SELECT hex(id) as id FROM " + schema + ".static_timezones " + "WHERE name=?");
    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, timezone);
      final ResultSet rs = preparedStatement.executeQuery();
      if (rs.next()) {
        return rs.getString("id");
      }
      return null;
    } catch (final SQLException e) {
      log.error(String.format("Get Timezone By Name Failed.", schema), e);
      throw e;
    }
  }
}

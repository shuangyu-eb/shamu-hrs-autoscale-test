package util;

import database.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public interface CompanyIdUtils {

  static List<String> getAllCompanyIds() throws Exception {
    final String sql = "SELECT hex(id) AS id FROM companies";
    final List<String> companies = new ArrayList<>();
    try (final Connection companyConnection = DataSource.getOriginConnection()) {
      try (final PreparedStatement statement = companyConnection.prepareStatement(sql)) {
        try (final ResultSet resultSet = statement.executeQuery()) {
          while (resultSet.next()) {
            final String id = resultSet.getString("id");
            companies.add(id);
          }
        }
      }
    }
    return companies;
  }
}

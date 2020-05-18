package util;

import datasource.ConnectionManager;
import entity.Company;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public interface CompanyUtils {

  static List<Company> getCompaniesFromCompanyService() throws Exception {
    String sql = "SELECT hex(id) as id, name FROM companies";
    Connection companyConnection = ConnectionManager.getCompanyConnection();
    List<Company> companies = new ArrayList<>();
    try (PreparedStatement statement = companyConnection.prepareStatement(sql)) {
      try (ResultSet resultSet = statement.executeQuery()) {
        while (resultSet.next()) {
          String id = resultSet.getString("id");
          String name = resultSet.getString("name");
          Company company = new Company(id, name);
          companies.add(company);
        }
      }
    }
    return companies;
  }

  static List<String> getUserIdsByCompanyFromCompanyService(String companyId) throws Exception {
    String sql = "SELECT hex(id) AS id FROM users WHERE company_id = unhex(?)";
    Connection connection = ConnectionManager.getCompanyConnection();
    List<String> userIds = new ArrayList<>();
    try (PreparedStatement statement = connection.prepareStatement(sql)) {
      statement.setString(1, companyId);
      try (ResultSet resultSet = statement.executeQuery()){
        while (resultSet.next()) {
          userIds.add(resultSet.getString("id"));
        }
      }
    }
    return userIds;
  }

}

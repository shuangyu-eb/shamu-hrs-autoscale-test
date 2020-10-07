package executor.benefit;

import entity.benefit.BenefitPlanUserDependent;
import executor.BaseExecutor;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class BenefitPlanUserDependentExecutor extends BaseExecutor<BenefitPlanUserDependent> {

  private BenefitPlanUserDependentExecutor(final String companyId) {
    super(companyId, "user_dependents");
  }

  public static BenefitPlanUserDependentExecutor getInstance(final String companyId) {
    return new BenefitPlanUserDependentExecutor(companyId);
  }

  @Override
  protected String getQuerySql() {
    final String querySql =
        "SELECT ud.* FROM %s ud "
            + "LEFT JOIN users u ON ud.employee_id = u.id "
            + "WHERE u.company_id = unhex(?)";
    return String.format(querySql, tableName);
  }

  @Override
  protected BenefitPlanUserDependent buildEntityByResultSet(ResultSet resultSet) throws Exception {
    final BenefitPlanUserDependent benefitPlanUserDependent = new BenefitPlanUserDependent();
    benefitPlanUserDependent.setEmployeeId(resultSet.getBinaryStream("employee_id"));
    benefitPlanUserDependent.setFirstName(resultSet.getString("first_name"));
    benefitPlanUserDependent.setLastName(resultSet.getString("last_name"));
    benefitPlanUserDependent.setDependentRelationshipId(
        resultSet.getBinaryStream("dependent_relationship_id"));
    benefitPlanUserDependent.setPhoneHome(resultSet.getString("phone_home"));
    benefitPlanUserDependent.setPhoneWork(resultSet.getString("phone_work"));
    benefitPlanUserDependent.setPhoneMobile(resultSet.getString("phone_mobile"));
    benefitPlanUserDependent.setEmail(resultSet.getString("email"));
    benefitPlanUserDependent.setPostalCode(resultSet.getString("postal_code"));
    benefitPlanUserDependent.setCity(resultSet.getString("city"));
    benefitPlanUserDependent.setMiddleName(resultSet.getString("middle_name"));
    benefitPlanUserDependent.setStreet1(resultSet.getString("street_1"));
    benefitPlanUserDependent.setStreet2(resultSet.getString("street_2"));
    benefitPlanUserDependent.setBirthDate(resultSet.getTimestamp("birth_date"));
    benefitPlanUserDependent.setSsn(resultSet.getString("ssn"));
    benefitPlanUserDependent.setGenderId(resultSet.getBinaryStream("gender_id"));
    benefitPlanUserDependent.setStateId(resultSet.getBinaryStream("state_id"));
    return benefitPlanUserDependent;
  }

  @Override
  protected String getInsertSql() {
    final String insertSql =
        "INSERT INTO %s (id, employee_id, first_name, "
            + "last_name, dependent_relationship_id, phone_home, "
            + "phone_work, phone_mobile, email, "
            + "created_at, updated_at, postal_code, "
            + "city, middle_name, street_1, "
            + "street_2, birth_date, ssn, "
            + "gender_id, state_id) "
            + "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
    return String.format(insertSql, tableFullName);
  }

  @Override
  protected void buildInsertStatement(PreparedStatement statement, BenefitPlanUserDependent entity)
      throws Exception {
    statement.setBinaryStream(1, entity.getId());
    statement.setBinaryStream(2, entity.getEmployeeId());
    statement.setString(3, entity.getFirstName());
    statement.setString(4, entity.getLastName());
    statement.setBinaryStream(
        5,
        getRelatedForeignKeyByRefId(
            "benefit_plan_dependent_relationships", entity.getDependentRelationshipId()));
    statement.setString(6, entity.getPhoneHome());
    statement.setString(7, entity.getPhoneWork());
    statement.setString(8, entity.getPhoneMobile());
    statement.setString(9, entity.getEmail());
    statement.setTimestamp(10, entity.getCreatedAt());
    statement.setTimestamp(11, entity.getUpdatedAt());
    statement.setString(12, entity.getPostalCode());
    statement.setString(13, entity.getCity());
    statement.setString(14, entity.getMiddleName());
    statement.setString(15, entity.getStreet1());
    statement.setString(16, entity.getStreet2());
    statement.setTimestamp(17, entity.getBirthDate());
    statement.setString(18, entity.getSsn());
    statement.setBinaryStream(19, getRelatedForeignKeyByRefId("genders", entity.getGenderId()));
    statement.setBinaryStream(20, getRelatedForeignKeyByName("states_provinces", entity.getStateId()));
  }
}

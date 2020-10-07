package manager;

import database.DataSource;
import database.DataSourceConfig;
import executor.attendance.CompanyTaSettingExecutor;
import executor.attendance.CurrencyExecutor;
import executor.attendance.EmployeeTaSettingExecutor;
import executor.attendance.OvertimePolicyExecutor;
import executor.attendance.PayrollDetailExecutor;
import executor.attendance.PolicyDetailExecutor;
import executor.benefit.BenefitCoverageExecutor;
import executor.benefit.BenefitPlanCoverageExecutor;
import executor.benefit.BenefitPlanDependentExecutor;
import executor.benefit.BenefitPlanDocumentExecutor;
import executor.benefit.BenefitPlanExecutor;
import executor.benefit.BenefitPlanUserDependentExecutor;
import executor.benefit.BenefitPlanUserExecutor;
import executor.benefit.BenefitRequestExecutor;
import executor.benefit.BenefitSettingExecutor;
import executor.benefit.RetirementPlanTypeExecutor;
import executor.common.StateProvinceExecutor;
import executor.company.CompanyExecutor;
import executor.company.DepartmentExecutor;
import executor.company.OfficeAddressExecutor;
import executor.company.OfficeExecutor;
import executor.company.TenantExecutor;
import executor.email.EmailExecutor;
import executor.job.JobExecutor;
import executor.job.JobUserExecutor;
import executor.timeoff.PaidHolidayExecutor;
import executor.timeoff.PaidHolidayUserExecutor;
import executor.timeoff.TimeOffAdjustmentExecutor;
import executor.timeoff.TimeOffPolicyAccrualScheduleExecutor;
import executor.timeoff.TimeOffPolicyAccrualScheduleMilestoneExecutor;
import executor.timeoff.TimeOffPolicyExecutor;
import executor.timeoff.TimeOffPolicyUserExecutor;
import executor.timeoff.TimeOffRequestCommentExecutor;
import executor.timeoff.TimeOffRequestDateExecutor;
import executor.timeoff.TimeOffRequestExecutor;
import executor.timeoff.TimePeriodExecutor;
import executor.timeoff.TimesheetExecutor;
import executor.user.CompensationChangeReasonExecutor;
import executor.user.CompensationTypeExecutor;
import executor.user.DismissedAtExecutor;
import executor.user.EmployeeTimeEntryExecutor;
import executor.user.EmployeeTimeLogExecutor;
import executor.user.UserAccessLevelEventExecutor;
import executor.user.UserAddressExecutor;
import executor.user.UserBenefitsSettingExecutor;
import executor.user.UserCompensationExecutor;
import executor.user.UserContactInformationExecutor;
import executor.user.UserEducationExecutor;
import executor.user.UserEmergencyContactExecutor;
import executor.user.UserEmploymentStatusExecutor;
import executor.user.UserExecutor;
import executor.user.UserPersonalInformationExecutor;
import lombok.extern.slf4j.Slf4j;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

@Slf4j
public abstract class TenantManager {

  private TenantManager() {}

  public static void migrateDataByCompanyId(final String companyId) throws Exception {
    log.info("<<<<<<<<< Data migration start, companyId: {} >>>>>>>>>>", companyId);
    CompanyExecutor.getInstance(companyId).execute();
    CompanyTaSettingExecutor.getInstance(companyId).execute();
    StateProvinceExecutor.getInstance(companyId).execute();
    OvertimePolicyExecutor.getInstance(companyId).execute();
    // User module
    UserContactInformationExecutor.getInstance(companyId).execute();
    UserPersonalInformationExecutor.getInstance(companyId).execute();
    UserExecutor.getInstance(companyId).execute();
    CompensationTypeExecutor.getInstance(companyId).execute();
    CompensationChangeReasonExecutor.getInstance(companyId).execute();
    UserCompensationExecutor.getInstance(companyId).execute();
    UserEmergencyContactExecutor.getInstance(companyId).execute();
    DismissedAtExecutor.getInstance(companyId).execute();
    UserBenefitsSettingExecutor.getInstance(companyId).execute();
    UserAccessLevelEventExecutor.getInstance(companyId).execute();
    UserEducationExecutor.getInstance(companyId).execute();
    EmployeeTimeEntryExecutor.getInstance(companyId).execute();
    UserEmploymentStatusExecutor.getInstance(companyId).execute();
    UserAddressExecutor.getInstance(companyId).execute();
    EmployeeTimeLogExecutor.getInstance(companyId).execute();

    // Benefits module
    BenefitPlanExecutor.getInstance(companyId).execute();
    BenefitPlanDocumentExecutor.getInstance(companyId).execute();
    BenefitCoverageExecutor.getInstance(companyId).execute();
    RetirementPlanTypeExecutor.getInstance(companyId).execute();
    BenefitPlanCoverageExecutor.getInstance(companyId).execute();
    BenefitPlanUserExecutor.getInstance(companyId).execute();
    BenefitRequestExecutor.getInstance(companyId).execute();
    BenefitPlanUserDependentExecutor.getInstance(companyId).execute();
    BenefitPlanDependentExecutor.getInstance(companyId).execute();
    BenefitSettingExecutor.getInstance(companyId).execute();

    // Time Off module
    TimeOffPolicyExecutor.getInstance(companyId).execute();
    TimeOffPolicyUserExecutor.getInstance(companyId).execute();
    TimeOffPolicyAccrualScheduleExecutor.getInstance(companyId).execute();
    TimeOffPolicyAccrualScheduleMilestoneExecutor.getInstance(companyId).execute();
    TimeOffRequestExecutor.getInstance(companyId).execute();
    TimeOffRequestDateExecutor.getInstance(companyId).execute();
    TimeOffRequestCommentExecutor.getInstance(companyId).execute();
    TimeOffAdjustmentExecutor.getInstance(companyId).execute();
    TimePeriodExecutor.getInstance(companyId).execute();
    PaidHolidayUserExecutor.getInstance(companyId).execute();
    TimesheetExecutor.getInstance(companyId).execute();
    PaidHolidayExecutor.getInstance(companyId).execute();
    JobExecutor.getInstance(companyId).execute();

    // Company module
    DepartmentExecutor.getInstance(companyId).execute();
    OfficeAddressExecutor.getInstance(companyId).execute();
    OfficeExecutor.getInstance(companyId).execute();
    EmployeeTaSettingExecutor.getInstance(companyId).execute();
    JobUserExecutor.getInstance(companyId).execute();

    // Attendance module
    CurrencyExecutor.getInstance(companyId).execute();
    PayrollDetailExecutor.getInstance(companyId).execute();
    PolicyDetailExecutor.getInstance(companyId).execute();

    // Email module
    EmailExecutor.getInstance(companyId).execute();

    // tenant add
    TenantExecutor.getInstance(companyId).execute();
    log.info("<<<<<<<<< Data migration done, companyId: {} >>>>>>>>>>", companyId);
  }

  public static void dropTenantDatabase(final String companyId) throws Exception {
    final String sql = "DROP DATABASE ";
    final String schema = DataSourceConfig.getTenantPrefix() + companyId;
    try (final Connection connection = DataSource.getTargetConnection();
        final Statement statement = connection.createStatement()) {
      statement.execute(sql + schema);
      deleteCompanyRecordInCompanyDefaultSchemaTenantsTable(companyId);
    } catch (final SQLException e) {
      log.error(String.format("Database %s drop failed.", schema), e);
    }
  }

  private static void deleteCompanyRecordInCompanyDefaultSchemaTenantsTable(final String companyId)
      throws Exception {
    final String schema = DataSourceConfig.getDefaultSchema();
    final String sql = String.format("DELETE FROM %s WHERE id = unhex(?)", schema + ".tenants");

    try (final Connection connection = DataSource.getTargetConnection();
        final PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
      preparedStatement.setString(1, companyId);
      preparedStatement.execute();
    } catch (final SQLException e) {
      log.error(String.format("Database %s drop failed.", schema), e);
    }
  }
}

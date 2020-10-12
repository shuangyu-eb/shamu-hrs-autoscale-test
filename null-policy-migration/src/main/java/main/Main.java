package main;

import entity.OvertimePolicy;
import helpers.OvertimePolicyCreator;
import lombok.extern.slf4j.Slf4j;
import manager.TenantManager;
import java.util.List;

@Slf4j
public class Main {

  public static void main(final String[] args) throws Exception {
    final List<String> companyIds = TenantManager.getAllCompanyIds();
    int total_companies=0;
    log.info("=============== Start ===============");
    for (final String companyId : companyIds) {
      total_companies+=1;
      if (total_companies%10==0){
        log.info("Completed " + total_companies + "Companies");
      }
      log.info(String.valueOf(companyId));
      OvertimePolicy overtimePolicy = OvertimePolicyCreator.createOvertimePolicy();
      if(!TenantManager.checkPolicyExists(companyId,overtimePolicy)){
        TenantManager.addNaOtPolicy(companyId, overtimePolicy);
      }
    }
    log.info("============== COMPLETE ===============");
  }
}

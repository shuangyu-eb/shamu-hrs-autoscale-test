package main;

import helpers.GoogleMapsHelper;
import lombok.extern.slf4j.Slf4j;
import manager.TenantManager;
import java.util.List;
import entity.TimezoneAddress;
import util.PropertiesUtils;

@Slf4j
public class Main {

  public static void main(final String[] args) throws Exception {
    GoogleMapsHelper googleMapsHelper = new GoogleMapsHelper(PropertiesUtils.getProperty("googleGeoCodingApiKey"));
    final List<String> companyIds = TenantManager.getAllCompanyIds();
    log.info("=============== Start ===============");
    for (final String companyId : companyIds) {
      final List<TimezoneAddress> addressesWithoutTimezones = TenantManager.getAddressesWithoutTimezones(companyId);
      log.info(String.valueOf(addressesWithoutTimezones.size()));
      for(TimezoneAddress address : addressesWithoutTimezones){
        String timezone = googleMapsHelper.findTimezone(address);
        TenantManager.addTimezone(companyId,address.getId(),timezone);
      } 
    }
    log.info("============== COMPLETE ===============");
  }
}

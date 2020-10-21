package main;

import database.DataSource;
import helpers.GoogleMapsHelper;
import lombok.extern.slf4j.Slf4j;
import manager.TenantManager;
import util.PropertiesUtils;

import java.util.List;

@Slf4j
public class Main {

  public static void main(final String[] args) throws Exception {
    final GoogleMapsHelper googleMapsHelper =
        new GoogleMapsHelper(PropertiesUtils.getProperty("googleGeoCodingApiKey"));
    final List<String> companyIds = TenantManager.getAllCompanyIds();
    log.info("=============== Start ===============");

    for (final String companyId : companyIds) {
      final List<String> userIds = TenantManager.getAllUsersWithTimezoneUnset(companyId);
      log.info(String.valueOf(userIds.size()));
      for (final String userId : userIds) {
        final String timezoneId = googleMapsHelper.findUserTimeZone(companyId, userId);
        TenantManager.addUserTimezone(companyId, timezoneId, userId);
      }
    }
    DataSource.closeDataSource();
    log.info("============== COMPLETE ===============");
  }
}

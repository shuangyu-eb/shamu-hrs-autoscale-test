package formatter;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import entity.TimezoneAddress;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class TimezoneFormatter {

  private TimezoneFormatter() {}


  public static List<TimezoneAddress> formatTimezones(final ResultSet rs) throws Exception {
      List<TimezoneAddress> timezoneAddresses = new ArrayList<>();
      while (rs.next()) {
          TimezoneAddress timezoneAddress = new TimezoneAddress();
          timezoneAddress.setId(rs.getBinaryStream("id"));
          timezoneAddress.setStateProvinceCode(rs.getString("name"));
          timezoneAddress.setPostalCode(rs.getString("postal_code"));
          timezoneAddress.setCity(rs.getString("city"));
          timezoneAddresses.add(timezoneAddress);
      }
      return timezoneAddresses;
  }
}

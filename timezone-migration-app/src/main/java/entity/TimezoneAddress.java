package entity;

import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class TimezoneAddress {

  private InputStream id;

  private String stateProvinceCode;

  private String city;

  private String postalCode;

}

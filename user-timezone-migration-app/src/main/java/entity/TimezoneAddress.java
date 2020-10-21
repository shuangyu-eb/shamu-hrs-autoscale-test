package entity;

import lombok.Data;

import java.io.InputStream;

@Data
public class TimezoneAddress {

  private InputStream id;

  private String stateProvinceCode;

  private String city;

  private String postalCode;
}

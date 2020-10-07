package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class UserAddress extends BaseEntity {

  private InputStream userId;

  private String street1;

  private String street2;

  private InputStream stateProvinceId;

  private InputStream countryId;

  private String postalCode;

  private String city;
}

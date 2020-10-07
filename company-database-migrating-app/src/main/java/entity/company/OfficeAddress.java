package entity.company;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class OfficeAddress extends BaseEntity {

  private String street1;

  private String street2;

  private InputStream stateProvinceId;

  private String postalCode;

  private String city;
}

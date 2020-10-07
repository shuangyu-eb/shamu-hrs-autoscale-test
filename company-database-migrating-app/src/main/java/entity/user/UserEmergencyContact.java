package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class UserEmergencyContact extends BaseEntity {

  private InputStream userId;

  private String firstName;

  private String lastName;

  private String relationship;

  private String phone;

  private String email;

  private String street1;

  private String street2;

  private String city;

  private InputStream stateId;

  private String postalCode;

  private Boolean isPrimary;

  private InputStream countryId;
}

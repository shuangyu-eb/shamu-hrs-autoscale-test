package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class UserPersonalInformation extends BaseEntity {

  private String firstName;

  private String middleName;

  private String lastName;

  private String preferredName;

  private Timestamp birthDate;

  private String ssn;

  private InputStream genderId;

  private InputStream maritalStatusId;

  private InputStream ethnicityId;

  private InputStream citizenshipStatusId;
}

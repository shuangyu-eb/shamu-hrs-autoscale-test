package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class BenefitPlanUserDependent extends BaseEntity {

  private InputStream employeeId;

  private String firstName;

  private String lastName;

  private InputStream dependentRelationshipId;

  private String phoneHome;

  private String phoneWork;

  private String phoneMobile;

  private String email;

  private String postalCode;

  private String city;

  private String middleName;

  private String street1;

  private String street2;

  private Timestamp birthDate;

  private String ssn;

  private InputStream genderId;

  private InputStream stateId;
}

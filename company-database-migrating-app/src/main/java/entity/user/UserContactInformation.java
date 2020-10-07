package entity.user;

import entity.BaseEntity;
import lombok.Data;

@Data
public class UserContactInformation extends BaseEntity {

  private String phoneWork;

  private String phoneWorkExtension;

  private String phoneMobile;

  private String phoneHome;

  private String emailWork;

  private String emailHome;
}

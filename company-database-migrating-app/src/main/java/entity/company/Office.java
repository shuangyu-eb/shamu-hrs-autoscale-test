package entity.company;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class Office extends BaseEntity {

  private InputStream officeId;

  private String name;

  private String phone;

  private String email;

  private InputStream officeAddressId;
}

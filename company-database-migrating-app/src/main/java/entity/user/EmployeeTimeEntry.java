package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class EmployeeTimeEntry extends BaseEntity {

  private String comment;

  private InputStream employeeId;
}

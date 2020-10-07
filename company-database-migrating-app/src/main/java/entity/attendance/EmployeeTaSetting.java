package entity.attendance;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class EmployeeTaSetting extends BaseEntity {

  private InputStream employeeId;

  private Integer messagingOn;
}

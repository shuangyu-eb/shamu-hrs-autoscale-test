package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class Timesheet extends BaseEntity {

  private InputStream employeeId;

  private InputStream statusId;

  private InputStream approverEmployeeId;

  private Timestamp approvedTimestamp;

  private InputStream userCompensationId;

  private InputStream timePeriodId;

  private Timestamp removedAt;
}

package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class EmployeeTimeLog extends BaseEntity {

  private Timestamp start;

  private Integer durationMin;

  private InputStream timeTypeId;

  private InputStream entryId;
}

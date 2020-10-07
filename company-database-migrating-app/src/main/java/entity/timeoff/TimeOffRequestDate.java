package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class TimeOffRequestDate extends BaseEntity {

  private InputStream timeOffRequestId;

  private Timestamp date;

  private Integer hours;
}

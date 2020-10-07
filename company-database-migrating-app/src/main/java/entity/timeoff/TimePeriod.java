package entity.timeoff;

import entity.BaseEntity;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class TimePeriod extends BaseEntity {

  private Timestamp startDate;

  private Timestamp endDate;
}

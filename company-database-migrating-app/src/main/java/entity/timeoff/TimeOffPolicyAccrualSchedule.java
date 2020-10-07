package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class TimeOffPolicyAccrualSchedule extends BaseEntity {

  private InputStream timeOffPolicyId;

  private Integer accrualHours;

  private Integer maxBalance;

  private Integer daysBeforeAccrualStarts;

  private Integer carryoverLimit;

  private InputStream timeOffAccrualFrequencyId;

  private Timestamp expiredAt;
}

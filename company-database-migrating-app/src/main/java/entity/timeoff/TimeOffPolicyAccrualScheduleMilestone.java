package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class TimeOffPolicyAccrualScheduleMilestone extends BaseEntity {

  private Integer anniversaryYear;

  private InputStream timeOffPolicyAccrualScheduleId;

  private Integer accrualHours;

  private Integer accrualInterval;

  private Integer carryoverLimit;

  private Integer maxBalance;

  private String name;

  private Timestamp expiredAt;
}

package entity.attendance;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class CompanyTaSetting extends BaseEntity {

  private InputStream timeZoneId;

  private int approvalDaysBeforePayroll;

  private String startOfWeek;

  private Integer messagingOn;

  private Integer overtimeAlert;
}

package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class TimeOffAdjustment extends BaseEntity {

  private InputStream userId;

  private InputStream adjusterUserId;

  private Integer amount;

  private InputStream timeOffPolicyId;

  private String comment;
}

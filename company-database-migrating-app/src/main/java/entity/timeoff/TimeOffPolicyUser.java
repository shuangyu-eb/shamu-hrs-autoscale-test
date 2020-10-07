package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class TimeOffPolicyUser extends BaseEntity {

  private InputStream userId;

  private InputStream timeOffPolicyId;

  private Integer initialBalance;
}

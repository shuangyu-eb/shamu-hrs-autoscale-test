package entity.attendance;

import entity.BaseEntity;
import lombok.Data;

@Data
public class OvertimePolicy extends BaseEntity {
  private String policyName;

  private Boolean defaultPolicy;

  private Boolean active;
}

package entity.timeoff;

import entity.BaseEntity;
import lombok.Data;

@Data
public class TimeOffPolicy extends BaseEntity {

  private Boolean isLimited;

  private String name;

  private Boolean isAutoEnrollEnabled;
}

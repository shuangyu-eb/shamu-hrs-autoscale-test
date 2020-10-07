package entity.benefit;

import entity.BaseEntity;
import lombok.Data;
import java.sql.Timestamp;

@Data
public class BenefitSetting extends BaseEntity {
  private Boolean automaticRollover;

  private Timestamp periodStartDate;

  private Timestamp periodEndDate;
}

package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class RetirementPlanType extends BaseEntity {

  private InputStream benefitPlanId;

  private InputStream retirementTypeId;
}

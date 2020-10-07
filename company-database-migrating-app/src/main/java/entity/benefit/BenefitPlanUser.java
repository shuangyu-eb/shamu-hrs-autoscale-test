package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class BenefitPlanUser extends BaseEntity {

  private InputStream benefitPlanId;

  private InputStream userId;

  private Boolean enrolled;

  private InputStream coverageId;

  private InputStream dependentId;

  private Boolean confirmed;
}

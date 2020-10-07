package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class BenefitCoverage extends BaseEntity {

  private String name;

  private InputStream benefitPlanId;

  private Integer refId;
}

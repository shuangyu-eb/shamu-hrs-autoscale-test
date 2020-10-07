package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class BenefitPlanCoverage extends BaseEntity {

  private InputStream benefitPlanId;

  private BigDecimal employerCost;

  private BigDecimal employeeCost;

  private InputStream benefitCoverageId;

  private Integer refId;
}

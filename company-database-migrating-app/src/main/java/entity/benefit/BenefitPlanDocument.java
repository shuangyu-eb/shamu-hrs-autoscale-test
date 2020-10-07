package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class BenefitPlanDocument extends BaseEntity {

  private String title;

  private String url;

  private InputStream benefitPlanId;
}

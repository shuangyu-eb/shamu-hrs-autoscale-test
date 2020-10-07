package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class BenefitPlan extends BaseEntity {

  private String name;

  private String description;

  private String planId;

  private Timestamp startDate;

  private Timestamp endDate;

  private String website;

  private InputStream benefitPlanTypeId;

  private String imageUrl;
}

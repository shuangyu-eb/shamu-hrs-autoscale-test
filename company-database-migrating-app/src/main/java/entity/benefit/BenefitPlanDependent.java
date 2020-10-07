package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class BenefitPlanDependent extends BaseEntity {

  private InputStream benefitPlansUsersId;

  private InputStream userDependentsId;
}

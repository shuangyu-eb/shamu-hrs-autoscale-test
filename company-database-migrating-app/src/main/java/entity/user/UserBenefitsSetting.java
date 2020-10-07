package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class UserBenefitsSetting extends BaseEntity {

  private InputStream userId;

  private String effectYear;
}

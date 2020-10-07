package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class PaidHolidayUser extends BaseEntity {

  private InputStream userId;

  private Boolean isSelected;
}

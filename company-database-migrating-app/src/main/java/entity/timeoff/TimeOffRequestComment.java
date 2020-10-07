package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class TimeOffRequestComment extends BaseEntity {

  private InputStream timeOffRequestId;

  private InputStream userId;

  private String comment;
}

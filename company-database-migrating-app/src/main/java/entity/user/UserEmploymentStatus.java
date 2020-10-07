package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class UserEmploymentStatus extends BaseEntity {

  private Timestamp effectiveDate;

  private InputStream userEmploymentStatusTypeId;

  private String comment;

  private InputStream userId;
}

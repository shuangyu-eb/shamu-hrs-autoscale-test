package entity.attendance;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class PayrollDetail extends BaseEntity {
  private InputStream payFrequencyTypeId;

  private Timestamp lastPayrollPayday;
}

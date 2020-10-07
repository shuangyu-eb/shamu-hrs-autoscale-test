package entity.attendance;

import entity.BaseEntity;
import java.io.InputStream;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class PolicyDetail extends BaseEntity {
  private Integer start;

  private InputStream overtimeTypeId;

  private BigDecimal rate;

  private InputStream overtimePolicyId;
}

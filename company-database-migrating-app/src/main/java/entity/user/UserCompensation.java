package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class UserCompensation extends BaseEntity {

  private InputStream userId;

  private Integer wageCents;

  private InputStream compensationFrequencyId;

  private InputStream compensationTypeId;

  private Timestamp startDate;

  private Timestamp endDate;

  private InputStream overtimeStatusId;

  private InputStream compensationChangeReasonId;

  private String comment;

  private InputStream currencyId;

  private InputStream overtimePolicyId;
}

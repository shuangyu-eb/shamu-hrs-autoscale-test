package entity.benefit;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class BenefitRequest extends BaseEntity {

  private InputStream requestUserId;

  private InputStream approverUserId;

  private InputStream lifeEventTypeId;

  private Timestamp lifeEventDate;

  private InputStream prevEnrollmentId;

  private InputStream nextEnrollmentId;

  private Timestamp effectiveDate;

  private InputStream requestStatusId;
}

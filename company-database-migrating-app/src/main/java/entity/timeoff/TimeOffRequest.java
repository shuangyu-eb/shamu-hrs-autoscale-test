package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class TimeOffRequest extends BaseEntity {

  private InputStream requesterUserId;

  private InputStream approverUserId;

  private Timestamp approvedDate;

  private InputStream timeOffPolicyId;

  private Timestamp expiresAt;

  private InputStream timeOffRequestApprovalStatusId;

  private Integer balance;
}

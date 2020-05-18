package entity;

import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class DocumentRequestsUser extends BaseEntity {

  private InputStream recipientGroupId;

  private InputStream recipientActionTypeId;

  private InputStream userId;

  private Integer order;

  private Timestamp actionedAt;

  private InputStream documentRequestId;

  private String pactsafeSignerId;
}

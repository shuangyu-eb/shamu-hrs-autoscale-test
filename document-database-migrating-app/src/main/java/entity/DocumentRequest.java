package entity;

import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class DocumentRequest extends BaseEntity {

  private InputStream sendUserId;

  private Timestamp expiredAt;

  private String message;

  private InputStream documentId;

  private InputStream documentRequestTypeId;

  private InputStream pactsafeRequestId;

  private InputStream contractsId;
}

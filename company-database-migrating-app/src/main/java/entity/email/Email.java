package entity.email;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class Email extends BaseEntity {

  private InputStream userId;

  private String from;

  private String to;

  private String subject;

  private String content;

  private Timestamp sendDate;

  private Timestamp sentAt;

  private Integer retryCount;

  private String fromName;

  private String toName;

  private String messageId;

  private String status;
}

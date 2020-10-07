package entity;

import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public abstract class BaseEntity {

  private InputStream id;

  private Timestamp createdAt;

  private Timestamp updatedAt;
}

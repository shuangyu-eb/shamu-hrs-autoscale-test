package entity;

import java.io.InputStream;
import java.math.BigDecimal;
import lombok.Data;

@Data
public class Document extends BaseEntity {

  private String title;

  private String url;

  private InputStream userId;

  private InputStream folderId;

  private BigDecimal size;

  private String type;
}

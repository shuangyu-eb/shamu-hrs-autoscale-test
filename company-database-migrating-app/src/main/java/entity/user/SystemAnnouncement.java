package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class SystemAnnouncement extends BaseEntity {

  private InputStream userId;

  private String content;

  private Boolean isPastAnnouncement;
}

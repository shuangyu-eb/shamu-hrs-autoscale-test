package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class DismissedAt extends BaseEntity {

  private InputStream userId;

  private InputStream systemAnnouncementId;
}

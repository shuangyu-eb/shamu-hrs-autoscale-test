package entity.timeoff;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class PaidHoliday extends BaseEntity {

  private InputStream countryId;

  private String name;

  private Timestamp date;

  private String nameShow;

  private Boolean federal;

  private InputStream creatorId;
}

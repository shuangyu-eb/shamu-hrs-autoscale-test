package entity.company;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class Company extends BaseEntity {

  private String name;

  private String imageUrl;

  private String ein;

  private InputStream countryId;

  private Boolean isPaidHolidaysAutoEnroll;
}

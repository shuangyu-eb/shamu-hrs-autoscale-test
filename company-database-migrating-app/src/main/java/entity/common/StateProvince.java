package entity.common;

import entity.BaseEntity;
import java.io.InputStream;
import lombok.Data;

@Data
public class StateProvince extends BaseEntity {

  private InputStream countryId;

  private String name;
}

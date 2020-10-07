package entity.attendance;

import entity.BaseEntity;
import lombok.Data;

@Data
public class Currency extends BaseEntity {

  private String name;

  private String abbreviation;
}

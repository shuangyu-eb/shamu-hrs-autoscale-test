package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class UserEducation extends BaseEntity {

  private InputStream userId;

  private String school;

  private String degree;

  private String major;

  private String gpa;

  private Timestamp startDate;

  private Timestamp endDate;
}

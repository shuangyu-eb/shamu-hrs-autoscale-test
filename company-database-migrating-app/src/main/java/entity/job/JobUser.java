package entity.job;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class JobUser extends BaseEntity {

  private InputStream userId;

  private InputStream jobId;

  private InputStream employmentTypeId;

  private Timestamp startDate;

  private Timestamp endDate;

  private InputStream officeId;

  private InputStream userCompensationId;

  private InputStream employeeTypeId;

  private InputStream departmentId;
}

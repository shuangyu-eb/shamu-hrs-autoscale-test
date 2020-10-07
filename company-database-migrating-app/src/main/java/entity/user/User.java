package entity.user;

import entity.BaseEntity;
import java.io.InputStream;
import java.sql.Timestamp;
import lombok.Data;

@Data
public class User extends BaseEntity {

  // used to user compared with each other
  private String hexedId;

  private Timestamp latestLogin;

  private InputStream userStatusId;

  private String imageUrl;

  private InputStream managerUserId;

  // used to user compared with each other
  private String hexedManagerUserId;

  private InputStream userPersonalInformationId;

  private InputStream userContactInformationId;

  private String invitationEmailToken;

  private Timestamp invitedAt;

  private Timestamp resetPasswordSentAt;

  private String resetPasswordToken;

  private String verificationToken;

  private Timestamp verifiedAt;

  private InputStream deactivationReasonId;

  private Timestamp deactivatedAt;

  private String changeWorkEmail;

  private String changeWorkEmailToken;

  private Timestamp verifyChangeWorkEmailAt;

  private InputStream userRoleId;

  private String salt;

  private InputStream timeZoneId;

  private Timestamp invitationCapabilityFrozenAt;
}

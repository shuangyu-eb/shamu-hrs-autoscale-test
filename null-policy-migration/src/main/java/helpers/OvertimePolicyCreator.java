package helpers;

import entity.OvertimePolicy;

import java.sql.Timestamp;
import java.util.Date;

/**
 * @author mshumaker
 */

public class OvertimePolicyCreator {

    public static OvertimePolicy createOvertimePolicy(){
        Timestamp createTime = new Timestamp(new Date().getTime());
        return new OvertimePolicy()
                .builder()
                .id(UuidUtil.getUuidString())
                .createdAt(createTime)
                .updatedAt(createTime)
                .active(1)
                .defaultPolicy(0)
                .policyName("NOT_ELIGIBLE")
                .build();
    }
}

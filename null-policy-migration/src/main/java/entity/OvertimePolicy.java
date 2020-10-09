package entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * @author mshumaker
 */

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OvertimePolicy {

    private String id;

    private String policyName;

    private Integer defaultPolicy;

    private Integer active;

    private Timestamp createdAt;

    private Timestamp updatedAt;

}

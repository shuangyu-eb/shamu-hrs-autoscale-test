package helpers;

import java.util.UUID;

public interface UuidUtil {

    static String getUuidString() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}

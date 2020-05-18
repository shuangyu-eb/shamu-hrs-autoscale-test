package util;

import java.io.File;
import java.io.IOException;

public interface DictionaryUtils {

  static File createDirIfNotExists(String dirName) throws IOException {
    File dir = new File(dirName);
    if(!dir.exists()) {
      boolean result = dir.mkdir();

      if(!result) {
        throw new IOException("Temp dir create failed.");
      }
    }
    if(dir.isFile()) {
      throw new IOException("The file which name is " + dirName + " is exists and it is not a dictionary.");
    }
    return dir;
  }
}

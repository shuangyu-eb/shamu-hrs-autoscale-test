package main;

import datasource.ConnectionManager;
import util.ExecuteLogUtils;

public class Main {

  public static void main(String[] args) {
    while(!ExecuteLogUtils.isDone()) {
      try {
        TenantInitializer.get().start();
      } catch (Exception e) {
        e.printStackTrace();
        ConnectionManager.resetAllConnections();
      }
    }
    ConnectionManager.close();
  }
}

package indrocraft.utils.sqlUtils;

import java.util.logging.Logger;

public class SQLLogger {

    private Logger logger = null;

    public void setLogger(Logger logger) {
    }


    public SQLLogger() {
        this.logger = logger;
    }
    public void connError(){
        log(Severity.WARN, "Unable To Create SQLQuerry: No Connection To Database");
    }
    public void log(Severity Severity, String message) {
        if (this.logger != null) {
            switch (Severity) {
                case SEVERE:;
                    this.logger.severe(message);
                    break;
                case WARN:
                    this.logger.warning(message);
                    break;
                case INFO:
                    this.logger.info(message);
                    break;

            }
        } else {
            switch (Severity) {
                case SEVERE:
                    System.out.println("[SEVERE] " + message);
                    break;
                case  WARN:
                    System.out.println("[WARNING] " + message);
                    break;
                case  INFO:
                    System.out.println("[INFO] " + message);
                    break;

            }
        }
    }
    public enum Severity {
        SEVERE,
        WARN,
        INFO
    }
}

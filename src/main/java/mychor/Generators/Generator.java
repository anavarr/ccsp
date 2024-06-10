package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;

public interface Generator {
    String generateSend(Comm comm);
    String generateRcv(Comm comm);
    String generateSelect(Comm comm);
    String generateBranch(Comm comm);
    void generateClass(String service, String applicationPath) throws IOException;
}

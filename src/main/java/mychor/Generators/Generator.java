package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.util.Collection;

public interface Generator {
    String generateSend(Comm comm);
    String generateRcv(Comm comm);
    String generateSelect(Comm comm);
    String generateBranch(Comm comm);
    void generateClass(String service, String applicationPath) throws IOException;
    String closeSession();

    Collection<String> generateMainImports();
}

package mychor.Generators;

import mychor.Comm;

import java.io.IOException;
import java.util.ArrayList;

public interface Generator {
    String generateSend(Comm comm);
    String generateRcv(Comm comm);
    String generateSelect(Comm comm);
    String generateBranch(Comm comm);
    String generateVoid(Comm comm);
    ArrayList<String> generateClass(String service, String applicationPath) throws IOException;
    String closeSession();

    ArrayList<String> generateMainImports();
}

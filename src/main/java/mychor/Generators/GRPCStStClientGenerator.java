package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GRPCStStClientGenerator implements Generator {
    public GRPCStStClientGenerator(Session serviceSession) {
    }

    @Override
    public String generateSend(Comm comm) {
        return "";
    }

    @Override
    public String generateRcv(Comm comm) {
        return "";
    }

    @Override
    public String generateSelect(Comm comm) {
        return "";
    }

    @Override
    public String generateBranch(Comm comm) {
        return "";
    }

    @Override
    public ArrayList<String> generateClass(String service, String applicationPath) throws IOException {

        return null;
    }

    @Override
    public String closeSession() {
        return "";
    }

    @Override
    public ArrayList<String> generateMainImports() {
        return new ArrayList<>();
    }
}

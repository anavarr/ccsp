package mychor.Generators;

import mychor.Comm;
import mychor.Session;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GRPCStStServerGenerator extends GRPCStStGenerator {
    public GRPCStStServerGenerator(Session serviceSession) {
        super(serviceSession);
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
    public String generateVoid(Comm comm) {
        return "";
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

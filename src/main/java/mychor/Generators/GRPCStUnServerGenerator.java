package mychor.Generators;

import mychor.Comm;
import mychor.Session;

public class GRPCStUnServerGenerator extends GRPCUnUnGenerator {
    public GRPCStUnServerGenerator(Session serviceSession) {
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
}
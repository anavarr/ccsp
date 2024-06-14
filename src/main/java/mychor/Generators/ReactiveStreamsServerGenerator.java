package mychor.Generators;

import mychor.Comm;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

public class ReactiveStreamsServerGenerator implements Generator {
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
    public void generateClass(String service, String applicationPath) throws IOException {

    }

    @Override
    public String closeSession() {
        return "";
    }

    @Override
    public Collection<String> generateMainImports() {
        return List.of();
    }
}

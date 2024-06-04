package mychor.Generators;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;

public class GenerationConfig {
    static ObjectMapper om = new ObjectMapper();
    static String path = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/resources/messaging-patterns/patterns.json";
    public static List<FrameworkSetting> settingsList;
    public static HashMap<String, LocalizedFrameworkSetting> localizedFrameworkSettingList = new HashMap<>();



    public static class FrameworkSettingDeserializer extends JsonDeserializer<FrameworkSetting> {
        @Override
        public FrameworkSetting deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);

            String name = node.get("name").asText();
            String pattern = node.get("pattern").asText();
            boolean clientNeedsClass = node.get("client").get("needsClass").asBoolean();
            String clientPom = node.get("client").get("pom").asText();
            String clientClassText = node.get("client").get("class").asText();
            boolean serverNeedsClass = node.get("server").get("needsClass").asBoolean();
            String serverPom = node.get("server").get("pom").asText();
            String serverClassText = node.get("client").get("class").asText();

            return new FrameworkSetting(name, pattern, clientNeedsClass, clientPom,
                    serverNeedsClass, serverPom, clientClassText, serverClassText);
        }
    }

    @JsonDeserialize(using=FrameworkSettingDeserializer.class)
    public record FrameworkSetting(
            String name,
            String pattern,
            Boolean clientNeedsClass,
            String clientPom,
            Boolean serverNeedsClass,
            String serverPom, String clientClassText, String serverClassText) {}

    public record LocalizedFrameworkSetting(boolean needsClass, String pom, String classText) {}


    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            settingsList = objectMapper.readValue(Files.readString(Path.of(path)),
                    new TypeReference<>() {});
            for (FrameworkSetting frameworkSetting : settingsList) {
                localizedFrameworkSettingList.put(frameworkSetting.name+"_client",
                        new LocalizedFrameworkSetting(
                                frameworkSetting.clientNeedsClass,
                                frameworkSetting.clientPom(),
                                frameworkSetting.clientClassText()
                        ));
                localizedFrameworkSettingList.put(frameworkSetting.name+"_server",
                        new LocalizedFrameworkSetting(
                                frameworkSetting.serverNeedsClass,
                                frameworkSetting.serverPom(),
                                frameworkSetting.serverClassText()
                        ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GenerationConfig {
    static ObjectMapper om = new ObjectMapper();
    static String path = "/home/arnavarr/Documents/thesis/prog/antlr4/ccsp/src/main/resources/messaging-patterns/patterns.json";
    public static List<FrameworkSetting> settingsList;
    public static HashMap<String, LocalizedFrameworkSetting> localizedFrameworkSettingList = new HashMap<>();


    static List<String> json2StringListSafe(JsonNode nodes){
        var list = new ArrayList<String>();
        new ArrayList<>();
        if(nodes != null) {
            for (JsonNode jsonNode : nodes) {
                list.add(jsonNode.asText());
            }
        }
        return list;
    }
    public static class FrameworkSettingDeserializer extends JsonDeserializer<FrameworkSetting> {
        @Override
        public FrameworkSetting deserialize(JsonParser jp, DeserializationContext ctxt)
                throws IOException, JsonProcessingException {
            JsonNode node = jp.getCodec().readTree(jp);

            String name = node.get("name").asText();
            String pattern = node.get("pattern").asText();
            var pomNode= node.get("client").get("pom");
            boolean clientNeedsClass = node.get("client").get("needsClass").asBoolean();
            List<String> clientPomDependencies =  json2StringListSafe(pomNode.get("dependencies"));
            List<String> clientPomBuildExtensions =  json2StringListSafe(node.get("client").get("pom").get("build").get("extensions"));
            List<String> clientPomBuildPlugins =  json2StringListSafe(node.get("client").get("pom").get("build").get("plugins"));
            String clientClassText = node.get("client").get("class").asText();
            boolean serverNeedsClass = node.get("server").get("needsClass").asBoolean();
            List<String> serverPomDependencies = json2StringListSafe(node.get("server").get("pom").get("dependencies"));
            List<String> serverPomBuildExtensions =  json2StringListSafe(node.get("server").get("pom").get("build").get("extensions"));
            List<String> serverPomBuildPlugins =  json2StringListSafe(node.get("server").get("pom").get("build").get("plugins"));
            String serverClassText = node.get("client").get("class").asText();

            return new FrameworkSetting(name, pattern,
                    clientNeedsClass, clientPomDependencies, clientPomBuildExtensions, clientPomBuildPlugins,
                    serverNeedsClass, serverPomDependencies, serverPomBuildExtensions, serverPomBuildPlugins,
                    clientClassText, serverClassText);
        }
    }

    @JsonDeserialize(using=FrameworkSettingDeserializer.class)
    public record FrameworkSetting(
            String name,
            String pattern,
            Boolean clientNeedsClass,
            List<String> clientPomDependencies,
            List<String> clientPomBuildExtensions,
            List<String> clientPomBuildPlugins,
            Boolean serverNeedsClass,
            List<String> serverPomDependencies,
            List<String> serverPomBuildExtensions,
            List<String> serverPomBuildPlugins,
            String clientClassText,
            String serverClassText) {}

    public record LocalizedFrameworkSetting(boolean needsClass,
                                            List<String> pomDependencies,
                                            List<String> pomBuildExtensions,
                                            List<String> pomBuildPlugins,
                                            String classText) {}


    static {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            settingsList = objectMapper.readValue(Files.readString(Path.of(path)),
                    new TypeReference<>() {});
            for (FrameworkSetting frameworkSetting : settingsList) {
                localizedFrameworkSettingList.put(frameworkSetting.name+"_client",
                        new LocalizedFrameworkSetting(
                                frameworkSetting.clientNeedsClass,
                                frameworkSetting.clientPomDependencies(),
                                frameworkSetting.clientPomBuildExtensions(),
                                frameworkSetting.clientPomBuildPlugins(),
                                frameworkSetting.clientClassText()
                        ));
                localizedFrameworkSettingList.put(frameworkSetting.name+"_server",
                        new LocalizedFrameworkSetting(
                                frameworkSetting.serverNeedsClass,
                                frameworkSetting.serverPomDependencies(),
                                frameworkSetting.serverPomBuildExtensions(),
                                frameworkSetting.serverPomBuildPlugins(),
                                frameworkSetting.serverClassText()
                        ));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

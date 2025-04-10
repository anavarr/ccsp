package mychor.types;

import java.util.List;

public record PossibleMessages(String source, String destination, List<String> labels) {
}

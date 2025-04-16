package mychor.types;

import mychor.Message;

public record MessageTrace(String source, String destination, Message message) {
}

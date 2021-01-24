package app;

import java.util.function.Predicate;

@FunctionalInterface
public interface Sender {
    Runnable send(String name, String message, Predicate<Chat.ClientHandler> condition);
}

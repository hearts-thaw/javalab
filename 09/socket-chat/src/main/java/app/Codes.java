package app;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public enum Codes {
    CHANGE_NAME("changename");

    private static final Pattern systemSignalPattern = Pattern.compile("!\\(([a-z]+)\\):(([a-zA-Z0-9',.-=]*( [a-zA-Z',.-]*)*){2,30})");

    private final String code;

    Codes(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getServerCode() {
        return "!(" + getCode() + "):";
    }

    public static Codes parseServerCode(String messageCode) {
        Matcher matcher = systemSignalPattern.matcher(messageCode);
        if (matcher.matches()) {
            return Arrays.stream(values())
                    .filter(code -> code.getServerCode().equals(messageCode))
                    .findFirst().orElseThrow(IllegalArgumentException::new);
        } else {
            throw new IllegalArgumentException("No such code");
        }
    }

    public static List<String> getServerCodes() {
        return Arrays.stream(values()).map(Codes::getServerCode).collect(Collectors.toList());
    }
}

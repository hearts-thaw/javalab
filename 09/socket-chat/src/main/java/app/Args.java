package app;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(
            names = { "--port" },
            description = "app.Chat server port",
            required = true)
    int port;
}

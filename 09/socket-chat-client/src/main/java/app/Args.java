package app;

import com.beust.jcommander.Parameter;

public class Args {
    @Parameter(
            names = { "--server-ip" },
            description = "Chat server ip",
            required = true)
    String ip;

    @Parameter(
            names = { "--server-port" },
            description = "Chat server port",
            required = true)
    int port;
}

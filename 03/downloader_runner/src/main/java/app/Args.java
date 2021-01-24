package app;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(separators = ";")
public class Args {
    @Parameter(
            names = {"--files", "--files-url", "--url", "-u"},
            description = "URL of files to download (separated by ';')",
            required = true
    )
    public String files;

    @Parameter(
            names = {"--folder", "--directory", "--dir", "-d"},
            description = "Folder for downloading files to",
            required = true
    )
    public String folder;

    @Parameter(
            names = {"--names", "-n"},
            description = "Names for downloaded files"
    )
    public String names;
}

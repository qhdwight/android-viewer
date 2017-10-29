import java.io.*;

/**
 * Gets information about config and starts the {@link VideoReceiver}.
 */
public class Main {

    public static final boolean DEFAULT_DEBUGGING = false;
    public static boolean DEBUGGING = DEFAULT_DEBUGGING;

    private static final int DEFAULT_PORT = 1180;
    private static final String DEFAULT_IP = "localhost";
    private static final String DEFAULT_CONFIG = String.format("%s:%d\n%b", DEFAULT_IP, DEFAULT_PORT, DEFAULT_DEBUGGING);
    private static final String RELATIVE_CONFIG_PATH = "res/config.txt";

    public static void main(String[] args) {

        String config = DEFAULT_CONFIG;

        try {
            final FileReader fileReader = new FileReader(RELATIVE_CONFIG_PATH);
            final BufferedReader reader =new BufferedReader(fileReader);

            try {
                final String addressAndHost = reader.readLine();
                final String debugging = reader.readLine();
                config = String.format("%s\n%s", addressAndHost, debugging);
                reader.close();
            } catch (IOException e) {
                if (DEBUGGING) e.printStackTrace();
            }
        } catch (FileNotFoundException nfe) {
            if (DEBUGGING) nfe.printStackTrace();
            try {
                final FileWriter fileWriter = new FileWriter(RELATIVE_CONFIG_PATH);
                final BufferedWriter writer = new BufferedWriter(fileWriter);

                writer.write(DEFAULT_CONFIG);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                if (DEBUGGING) e.printStackTrace();
            }
        }

        // Split by lines
        final String[] lines = config.split("\\r?\\n");
        // Split first line by colon
        final String[] addressAndIp = lines[0].split(":");
        final String ip = addressAndIp[0];

        int port;
        DEBUGGING = Boolean.parseBoolean(lines[1]);
        try {
            port = Integer.parseInt(addressAndIp[1]);
        } catch (NumberFormatException e) {
            if (DEBUGGING) e.printStackTrace();
            port = DEFAULT_PORT;
        }

        new VideoReceiver(port, ip);
    }
}

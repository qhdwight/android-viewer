import java.io.*;

/**
 * Gets information about config and starts the {@link VideoReceiver}.
 */
public class Main {

    private static final int DEFAULT_PORT = 1180;
    private static final String DEFAULT_IP = "localhost";
    private static final String DEFAULT_CONFIG = String.format("%s:%d", DEFAULT_IP, DEFAULT_PORT);
    private static final String RELATIVE_CONFIG_PATH = "res/config.txt";

    public static final boolean debug = true;

    public static void main(String[] args) {

        String config = DEFAULT_CONFIG;

        try {
            final FileReader fileReader = new FileReader(RELATIVE_CONFIG_PATH);
            final BufferedReader reader =new BufferedReader(fileReader);

            try {
                config = reader.readLine();
                reader.close();
            } catch (IOException e) {
                if (debug) e.printStackTrace();
            }
        } catch (FileNotFoundException nfe) {
            if (debug) nfe.printStackTrace();
            try {
                final FileWriter fileWriter = new FileWriter(RELATIVE_CONFIG_PATH);
                final BufferedWriter writer = new BufferedWriter(fileWriter);

                writer.write(DEFAULT_CONFIG);
                writer.flush();
                writer.close();
            } catch (IOException e) {
                if (debug) e.printStackTrace();
            }
        }

        final String[] split = config.split("[:]");
        final String ip = split[0];

        int port;
        try {
            port = Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            if (debug) e.printStackTrace();
            port = DEFAULT_PORT;
        }

        new VideoReceiver(port, ip);
    }
}

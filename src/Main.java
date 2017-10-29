import java.io.*;

/**
 * Gets information about config and starts the {@link VideoReceiver}.
 */
public class Main {

    private static final int k_defaultPort = 1180;
    private static final String k_defaultIp = "localhost";
    private static final String k_defaultConfig = String.format("%s:%d", k_defaultIp, k_defaultPort);
    private static final String k_relativeConfigPath = "res/config.txt";

    public static final boolean debug = true;

    public static void main(String[] args) {

        String config = k_defaultConfig;

        try {
            final FileReader fileReader = new FileReader(k_relativeConfigPath);
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
                final FileWriter fileWriter = new FileWriter(k_relativeConfigPath);
                final BufferedWriter writer = new BufferedWriter(fileWriter);

                writer.write(k_defaultConfig);
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
            port = k_defaultPort;
        }

        new VideoReceiver(port, ip);
    }
}

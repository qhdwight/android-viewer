package viewer;

import com.google.gson.Gson;

import java.io.*;

/**
 * Gets information about config and starts the {@link VideoReceiver}.
 */
public class ViewerMain {

    private static final Gson GSON = new Gson();
    private static Config DEFAULT_CONFIG = new Config();
    private static final String RELATIVE_CONFIG_PATH = "./config/config.json";
    private static final File CONFIG_FILE = new File(RELATIVE_CONFIG_PATH);

    public static void main(String[] args) {
        try {
            System.out.println(CONFIG_FILE.getCanonicalPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Config config = DEFAULT_CONFIG;
        try {
            final FileReader fileReader = new FileReader(CONFIG_FILE);
            final BufferedReader reader = new BufferedReader(fileReader);
            try {
                String line;
                final StringBuilder fileText = new StringBuilder();
                while ((line = reader.readLine()) != null)
                    fileText.append(line);
                config = GSON.fromJson(fileText.toString(), Config.class);
                reader.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        } catch (final FileNotFoundException nfe) {
            nfe.printStackTrace();
            try {
                System.out.println(CONFIG_FILE.getCanonicalPath());
                if (!CONFIG_FILE.getParentFile().mkdirs())
                    System.err.println("Error creating parent directories!");
                final FileWriter fileWriter = new FileWriter(CONFIG_FILE);
                final BufferedWriter writer = new BufferedWriter(fileWriter);
                writer.write(GSON.toJson(DEFAULT_CONFIG));
                writer.flush();
                writer.close();
            } catch (final IOException e) {
                e.printStackTrace();
            }
        }
        System.out.println(String.format("Starting video receiver at %s:%d with scale %f and debugging: %b",
                config.address, config.port, config.previewScale, config.debugMode));
        new VideoReceiver(config.port, config.address, config.previewScale, config.debugMode);
    }
}

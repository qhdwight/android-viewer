package viewer;

import org.json.simple.parser.JSONParser;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.HashMap;

/**
 * Gets information about config and starts the {@link VideoReceiver}.
 */
public class ViewerMain {

    public static final boolean DEFAULT_DEBUGGING = false;

    private static final int DEFAULT_PORT = 1180;
    private static final String DEFAULT_IP = "localhost";
    private static HashMap<String, Object> DEFAULT_CONFIG_MAP = new HashMap<String, Object>() {{
        put("ip", DEFAULT_IP);
        put("port", DEFAULT_PORT);
        put("debugging", DEFAULT_DEBUGGING);
    }};
    private static final JSONObject DEFAULT_CONFIG = new JSONObject(DEFAULT_CONFIG_MAP);
    private static final String RELATIVE_CONFIG_PATH = "res/config.txt";

    public static void main(String[] args) {

        JSONObject config = DEFAULT_CONFIG;

        try {
            final FileReader fileReader = new FileReader(RELATIVE_CONFIG_PATH);
            final BufferedReader reader = new BufferedReader(fileReader);

            try {
                String line, fileText = "";
                while ((line = reader.readLine()) != null)
                    fileText += line;

                JSONParser parser = new JSONParser();
                try {
                    config = (JSONObject)parser.parse(fileText);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                reader.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException nfe) {
            nfe.printStackTrace();
            try {
                final FileWriter fileWriter = new FileWriter(RELATIVE_CONFIG_PATH);
                final BufferedWriter writer = new BufferedWriter(fileWriter);

                writer.write(DEFAULT_CONFIG.toJSONString());
                writer.flush();
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        final int port = ((Number)config.get("port")).intValue();
        final String ip = (String)config.get("ip");
        final boolean debugging = (boolean)config.get("debugging");

        new VideoReceiver(port, ip, debugging);
    }
}

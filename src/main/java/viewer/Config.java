package viewer;

public class Config {

    private static final boolean DEFAULT_DEBUGGING = false;
    private static final int DEFAULT_PORT = 1180;
    private static final String DEFAULT_IP = "localhost";
    private static final double DEFAULT_PREVIEW_SCALE = 2.25d;

    public final String address;
    public final int port;
    public final double previewScale;
    public final boolean debugMode;

    public Config() {
        address = DEFAULT_IP;
        port = DEFAULT_PORT;
        previewScale = DEFAULT_PREVIEW_SCALE;
        debugMode = DEFAULT_DEBUGGING;
    }

    public Config(final String address, final int port, final double previewScale, final boolean debugMode) {
        this.address = address;
        this.port = port;
        this.previewScale = previewScale;
        this.debugMode = debugMode;
    }
}

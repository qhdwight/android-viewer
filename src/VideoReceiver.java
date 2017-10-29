import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

/**
 * Sets up a socket to read video data from an android phone.
 * Also handles creating a {@link ViewerPanel} to display the video.
 */
public class VideoReceiver implements Runnable {

    private Socket m_socket = new Socket();

    private int m_port;
    private String m_ip;
    private boolean m_debugging;

    private boolean m_running;

    private static final long RETRY_WAIT = 500, UPDATE_TIME = 10;

    private final ViewerPanel k_viewerPanel;

    private enum VideoReceiverState {
        INIT, ATTEMPTING_CONNECTION, OPEN
    }

    private VideoReceiverState m_state = VideoReceiverState.INIT;

    public VideoReceiver(final int port, final String ip, final boolean debugging) {

        m_port = port;
        m_ip = ip;

        k_viewerPanel = new ViewerPanel();

        createFrame();

        start();
    }

    /**
     * Creates a {@link JFrame} and adds a {@link ViewerPanel} to it.
     */
    private void createFrame() {

        JFrame frame = new JFrame("Team 8 Android Viewer");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setResizable(false);

        try {
            final Image icon = ImageIO.read(new File("res/logo.png"));
            frame.setIconImage(icon);
        } catch (IOException e) {
            if (m_debugging) e.printStackTrace();
        }

        frame.add(k_viewerPanel);


        frame.pack();

        frame.repaint();

        frame.setVisible(true);
    }

    /**
     * Start the thread.
     */
    private void start() {

        m_running = true;

        m_state = VideoReceiverState.ATTEMPTING_CONNECTION;

        (new Thread(this)).run();
    }

    /**
     * Attempt to reconnect to the server socket.
     */
    private void retryConnection() {

        try {

            m_socket = new Socket(m_ip, m_port);

            if (m_debugging) System.out.println(String.format("Connected on port: %d", m_socket.getPort()));

            m_state = VideoReceiverState.OPEN;

        } catch (IOException e) {

            if (m_debugging) e.printStackTrace();

            try {
                Thread.sleep(RETRY_WAIT);
            } catch (InterruptedException ie) {
                if (m_debugging) ie.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        while (m_running) {

            switch (m_state) {
                case OPEN: {
                    tryReceiveAndDisplayVideo();
                    break;
                }
                case ATTEMPTING_CONNECTION: {
                    retryConnection();
                    break;
                }
            }
        }
    }

    /**
     * Try to read the byte array from the socket and send it to the {@link ViewerPanel} in order to display it in form of a {@link BufferedImage}.
     */
    private void tryReceiveAndDisplayVideo() {

        try {

            byte[] imageData = tryReceiveVideo();

            if (imageData != null) {

                BufferedImage image = getImageFromBytes(imageData);

                if (image != null) {

                    k_viewerPanel.setCurrentImage(image);
                }
            }

            Thread.sleep(UPDATE_TIME);

        } catch (InterruptedException ie) {

            if (m_debugging) ie.printStackTrace();
        }
    }


    /**
     * Try to receive an array of bytes representing an image from {@link #m_socket}.
     * If it fails, retry connection.
     *
     * @return Frame represented as byte array
     */
    private byte[] tryReceiveVideo() {

        try {

            final InputStream clientStream = m_socket.getInputStream();
            final DataInputStream clientDataStream = new DataInputStream(clientStream);

            final int length = clientDataStream.readInt();

            byte[] data = new byte[length];

            if (length > 0) {
                clientDataStream.readFully(data);
                return data;
            } else {
                return null;
            }

        } catch (IOException e) {

            if (m_debugging) e.printStackTrace();

            m_state = VideoReceiverState.ATTEMPTING_CONNECTION;
        }

        return null;
    }

    /**
     * Try to parse an array of bytes into a {@link BufferedImage}.
     *
     * @param data Array of bytes representing image
     * @return The buffered image parsed
     */
    private BufferedImage getImageFromBytes(byte[] data) {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

        try {

            return ImageIO.read(byteArrayInputStream);

        } catch (IOException e) {

            if (m_debugging) e.printStackTrace();
            return null;
        }
    }
}

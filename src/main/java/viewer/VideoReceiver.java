package viewer;

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

    private enum VideoReceiverState {
        INITIALIZATION, ATTEMPTING_CONNECTION, OPEN
    }

    private static final long RETRY_WAIT = 500;

    private final ViewerPanel fm_ViewerPanel;
    private Socket m_Socket = new Socket();
    private int m_Port;
    private String m_Ip;
    private boolean m_Debugging, m_Running;
    private VideoReceiverState m_State = VideoReceiverState.INITIALIZATION;

    public VideoReceiver(final int port, final String ip, final double scale, final boolean debugging) {
        m_Port = port;
        m_Ip = ip;
        fm_ViewerPanel = new ViewerPanel(scale);
        m_Debugging = debugging;
        createFrame();
        start();
    }

    /**
     * Creates a {@link JFrame} and adds a {@link ViewerPanel} to it.
     */
    private void createFrame() {
        JFrame frame = new JFrame("Team 8 Android Viewer [Hi Eric]");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);
        try {
            final Image icon = ImageIO.read(new File("res/logo.png"));
            frame.setIconImage(icon);
        } catch (IOException e) {
            if (m_Debugging) e.printStackTrace();
        }
        frame.add(fm_ViewerPanel);
        frame.pack();
        frame.repaint();
        frame.setVisible(true);
    }

    /**
     * Start the thread.
     */
    private void start() {
        m_Running = true;
        m_State = VideoReceiverState.ATTEMPTING_CONNECTION;
        (new Thread(this)).run();
    }

    /**
     * Attempt to reconnect to the server socket.
     */
    private void retryConnection() {
        try {
            m_Socket = new Socket(m_Ip, m_Port);
            if (m_Debugging) System.out.println(String.format("Connected on port: %d", m_Socket.getPort()));
            m_State = VideoReceiverState.OPEN;
        } catch (final IOException e) {
            if (m_Debugging) e.printStackTrace();
            try {
                Thread.sleep(RETRY_WAIT);
            } catch (InterruptedException ie) {
                if (m_Debugging) ie.printStackTrace();
            }
        }
    }

    public void run() {
        while (m_Running) {
            switch (m_State) {
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
        final byte[] imageData = tryReceiveVideo();
        if (imageData != null) {
            final BufferedImage image = getImageFromBytes(imageData);
            if (image != null) {
                fm_ViewerPanel.setCurrentImage(image);
            }
        }
    }


    /**
     * Try to receive an array of bytes representing an image from {@link #m_Socket}.
     * If it fails, retry connection.
     *
     * @return Frame represented as byte array
     */
    private byte[] tryReceiveVideo() {
        try {
            final DataInputStream inputStream = new DataInputStream(m_Socket.getInputStream());
            try {
                final int length = inputStream.readInt();
                final byte[] data = new byte[length];
                inputStream.readFully(data, 0, length);
                return data;
            } catch (final EOFException eofe) {
                eofe.printStackTrace();
            }
        } catch (final IOException ioe) {
            if (m_Debugging) ioe.printStackTrace();
            m_State = VideoReceiverState.ATTEMPTING_CONNECTION;
        }
        return null;
    }

    /**
     * Try to parse an array of bytes into a {@link BufferedImage}.
     *
     * @param data Array of bytes representing image
     * @return The buffered image parsed
     */
    private BufferedImage getImageFromBytes(final byte[] data) {
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
        try {
            return ImageIO.read(byteArrayInputStream);
        } catch (final IOException e) {
            if (m_Debugging) e.printStackTrace();
            return null;
        }
    }
}

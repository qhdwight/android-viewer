import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;

public class VideoReceiver implements Runnable {

    private Socket m_socket = new Socket();

    private int m_port;
    private String m_ip;

    private boolean m_running;

    private static final long retryWait = 500, updateTime = 10;

    private final ViewerPanel k_viewerPanel;

    private final boolean debug = false;

    public VideoReceiver(final int port, final String ip) {

        m_port = port;
        m_ip = ip;

        k_viewerPanel = new ViewerPanel();

        createFrame();

        start();
    }

    private void createFrame() {

        JFrame frame = new JFrame("Team 8 Android Viewer");

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        frame.setResizable(false);

        try {
            final Image icon = ImageIO.read(new File("res/logo.png"));
            frame.setIconImage(icon);
        } catch (IOException e) {
            if (debug) e.printStackTrace();
        }

        frame.add(k_viewerPanel);


        frame.pack();

        frame.repaint();

        frame.setVisible(true);
    }

    private void start() {

        m_running = true;

        (new Thread(this)).run();
    }

    private void retryConnection() {

        try {

            m_socket = new Socket(m_ip, m_port);

        } catch (IOException e) {

            if (debug) e.printStackTrace();

            try {
                Thread.sleep(retryWait);
            } catch (InterruptedException ie) {
                if (debug) ie.printStackTrace();
            }

            retryConnection();
        }
    }

    @Override
    public void run() {

        while (m_running) {

            try {

                byte[] imageData = tryReceiveVideo();

                if (imageData != null) {

                    BufferedImage image = getImageFromBytes(imageData);

                    if (image != null) {

                        k_viewerPanel.setCurrentImage(image);
                    }
                }

                Thread.sleep(updateTime);

            } catch (InterruptedException ie) {

                if (debug) ie.printStackTrace();
            }
        }
    }

    private byte[] tryReceiveVideo() {

        try {

            final InputStream clientStream = m_socket.getInputStream();
            final DataInputStream clientDataStream = new DataInputStream(clientStream);

            final int length = clientDataStream.readInt();

            byte[] data = new byte[length];

            if (length > 0)
                clientDataStream.readFully(data);
            else
                return null;

            return data;

        } catch (IOException e) {

            if (debug) e.printStackTrace();
            retryConnection();
        }

        return null;
    }

    private BufferedImage getImageFromBytes(byte[] data) {

        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);

        try {
            final BufferedImage image = ImageIO.read(byteArrayInputStream);
            return image;
        } catch (IOException e) {
            if (debug) e.printStackTrace();
            return null;
        }
    }
}

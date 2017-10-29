import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Displays a {@link BufferedImage} to a {@link JPanel}.
 */
public class ViewerPanel extends JPanel {

    private static final int IMAGE_WIDTH = 360, IMAGE_HEIGHT = 640;

    private BufferedImage currentImage;

    public ViewerPanel() {

    }

    @Override
    public void paint(Graphics g) {

        super.paint(g);

        Graphics2D g2 = (Graphics2D)g;

        if (currentImage != null)
            g2.drawImage(currentImage, 0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, null);
    }

    @Override
    public Dimension getPreferredSize() {

        return new Dimension(360, 640);
    }

    public void setCurrentImage(BufferedImage newImage) {

        currentImage = newImage;

        repaint();
    }
}

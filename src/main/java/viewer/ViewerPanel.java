package viewer;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Displays a {@link BufferedImage} to a {@link JPanel}.
 */
public class ViewerPanel extends JPanel {

    private static final int IMAGE_WIDTH = 300, IMAGE_HEIGHT = 400;

    private final double fm_Scale;
    private BufferedImage m_CurrentImage;

    public ViewerPanel(final double scale) {
        fm_Scale = scale;
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        final Graphics2D g2 = (Graphics2D)g;
        if (m_CurrentImage != null)
            g2.drawImage(m_CurrentImage, 0, 0, (int)(IMAGE_WIDTH * fm_Scale), (int)(IMAGE_HEIGHT * fm_Scale), null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((int)(IMAGE_WIDTH * fm_Scale), (int)(IMAGE_HEIGHT * fm_Scale));
    }

    public void setCurrentImage(final BufferedImage newImage) {
        m_CurrentImage = newImage;
        repaint();
    }
}

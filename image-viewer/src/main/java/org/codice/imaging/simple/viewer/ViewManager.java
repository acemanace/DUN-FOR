package org.codice.imaging.simple.viewer;

import java.awt.*;
import java.beans.PropertyVetoException;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ViewManager {
    private static final int MAX_CHIP_ZOOM = 1200;

    private static Set<ProgressMonitor> runningMonitors = new HashSet<>();

    @Autowired
    private JDesktopPane desktopPane;

    @Autowired
    private JFileChooser fileChooser;

    private JInternalFrame prepareNewFrame() {
        JInternalFrame internalFrame = new JInternalFrame();
        desktopPane.add(internalFrame);
        internalFrame.setVisible(true);

        try {
            internalFrame.setMaximum(true);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }

        return internalFrame;
    }

    public void openFile() {
        fileChooser.setDialogTitle("Open Image");
        int userSelection = fileChooser.showOpenDialog(desktopPane);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File path = fileChooser.getSelectedFile();
            ThreadLocal<JInternalFrame> nitfInternalFrame = new ThreadLocal<>();

            SwingWorker<Void, Void> worker = new SwingWorker() {
                @Override
                protected Object doInBackground ()throws Exception {
                    JInternalFrame jInternalFrame = prepareNewFrame();

                    Image image = ImageIO.read(path);
                    ImageIcon icon = new ImageIcon(image);
                    JLabel label = new JLabel();
                    label.setIcon(icon);
                    jInternalFrame.getContentPane().setLayout(new BorderLayout());
                    jInternalFrame.getContentPane().add(label, BorderLayout.CENTER);
                    return null;
                }
            };

            worker.execute();
        }

    }
}

package org.codice.imaging.nitf.viewer;

import java.awt.Insets;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.imageio.spi.IIORegistry;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.github.jaiimageio.jpeg2000.impl.J2KImageReaderSpi;

public class Viewer {
    public static void main(String... args) throws InterruptedException {
        UIManager.getDefaults()
                .put("TabbedPane.contentBorderInsets", new Insets(0, 0, 0, 0));
        UIManager.getDefaults()
                .put("TabbedPane.tabsOverlapBorder", true);

        try {
            UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        JFrame.setDefaultLookAndFeelDecorated(true);

        ApplicationContext context = new AnnotationConfigApplicationContext(ViewConfiguration.class);
        JFrame topFrame = context.getBean(JFrame.class, "topFrame");

        topFrame.setVisible(true);
        ViewManager ViewManager = context.getBean(ViewManager.class, "ViewManager");
        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize()
                .getWidth());
        int ySize = ((int) tk.getScreenSize()
                .getHeight());
        topFrame.setSize(xSize, ySize);

        IIORegistry.getDefaultInstance()
                .registerServiceProvider(new J2KImageReaderSpi());
        ViewManager.openFile();
    }
}

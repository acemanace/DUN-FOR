package org.codice.imaging.nitf.viewer;

import java.awt.Insets;
import java.awt.Toolkit;
import javax.swing.JFrame;
import javax.swing.UIManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Viewer {
    public static void main(String... args) throws InterruptedException {
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e) {}

        JFrame.setDefaultLookAndFeelDecorated(true);

        ApplicationContext context =
                new AnnotationConfigApplicationContext(GuiConfiguration.class);

        JFrame topFrame = context.getBean(JFrame.class, "topFrame");
        topFrame.setVisible(true);
        GuiManager guiManager = context.getBean(GuiManager.class, "guiManager");
        Toolkit tk = Toolkit.getDefaultToolkit();
        int xSize = ((int) tk.getScreenSize().getWidth());
        int ySize = ((int) tk.getScreenSize().getHeight());
        topFrame.setSize(xSize,ySize);
        guiManager.openFile();
    }
}

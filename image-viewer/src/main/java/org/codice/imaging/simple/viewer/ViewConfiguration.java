package org.codice.imaging.simple.viewer;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ViewConfiguration {

    @Bean
    public JDesktopPane desktopPane() throws IOException {
        JDesktopPane desktopPane = new JDesktopPane();

        desktopPane.setBackground(Color.LIGHT_GRAY);
        desktopPane.setBorder(titlePanel());
        return desktopPane;
    }

    @Bean
    public JFrame topFrame() throws PropertyVetoException, IOException {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(200, 50);
        frame.setSize(1000, 600);
        frame.setTitle(title());
        frame.setContentPane(desktopPane());
        frame.setJMenuBar(mainMenu());
        return frame;
    }

    @Bean
    public JTextArea logPanel() {
        JTextArea logPanel = new JTextArea();
        logPanel.setRows(4);
        logPanel.setAutoscrolls(true);
        logPanel.setEditable(false);
        return logPanel;
    }

    @Bean
    public JDesktopImage titlePanel() {
        URL iconUrl = ClassLoader.getSystemClassLoader()
                .getResource("images/background.png");

        BufferedImage img = null;

        try {
            img = ImageIO.read(iconUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new JDesktopImage(img);
    }

    @Bean
    public JTabbedPane mainPanel() {
        JTabbedPane mainPanel = new JTabbedPane();
        mainPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        return mainPanel;
    }

    @Bean
    public ViewManager nitfGuiManager() {
        return new ViewManager();
    }

    @Bean
    public JMenuBar mainMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu());
        return menuBar;
    }


    @Bean
    public JMenu fileMenu() {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_O);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        openItem.addActionListener(e -> nitfGuiManager().openFile());
        fileMenu.add(openItem);
        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));
        fileMenu.add(exitItem);
        return fileMenu;
    }

    @Bean
    public JFileChooser fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    @Bean
    public String title() {
        return "Codice Image Viewer";
    }

    @Bean
    public String copyright() {
        return "(c) Codice Foundation 2016";
    }
}

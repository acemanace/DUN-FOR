package org.codice.imaging.nitf.viewer;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.PropertyVetoException;
import java.io.IOException;
import javax.swing.JDesktopPane;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GuiConfiguration {

    @Bean
    public JDesktopPane desktopPane() throws IOException {
        JDesktopPane desktopPane = new JDesktopPane();

        desktopPane.setBackground(Color.GRAY);
        desktopPane.add(titlePanel());
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
    public JPanel titlePanel() {
        JPanel titlePanel = new JPanel(new GridLayout(2, 1));

        JLabel startupLabel1 = new JLabel();
        startupLabel1.setText(title() + " (DUN-FOR)");
        startupLabel1.setHorizontalAlignment(JLabel.CENTER);
        startupLabel1.setVerticalAlignment(JLabel.BOTTOM);
        Font defaultLabelFont = startupLabel1.getFont();
        int fontSize = defaultLabelFont.getSize();
        startupLabel1.setFont(new Font(defaultLabelFont.getName(), Font.PLAIN, fontSize));
        titlePanel.add(startupLabel1);

        JLabel startupLabel2 = new JLabel();
        startupLabel2.setText(copyright());
        startupLabel2.setHorizontalAlignment(JLabel.CENTER);
        startupLabel2.setVerticalAlignment(JLabel.TOP);
        startupLabel2.setFont(new Font(defaultLabelFont.getName(), Font.PLAIN, fontSize - 2));
        startupLabel2.setForeground(Color.BLUE);
        titlePanel.add(startupLabel2);

        return titlePanel;
    }

    @Bean
    public JTabbedPane mainPanel() {
        JTabbedPane mainPanel = new JTabbedPane();
        mainPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        return mainPanel;
    }

    @Bean
    public TabPanelFactory tabPanelFactory() {
        return new TabPanelFactory();
    }

    @Bean
    public GuiManager nitfGuiManager() {
        return new GuiManager();
    }

    @Bean
    public JMenuBar mainMenu() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileMenu());
        menuBar.add(imageMenu());
        return menuBar;
    }

    @Bean
    public JMenu imageMenu() {
        JMenu tabMenu = new JMenu("Image");
        tabMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem closeTabItem = new JMenuItem("Close", KeyEvent.VK_C);
        closeTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        closeTabItem.addActionListener(e -> nitfGuiManager().closeTab(mainPanel().getSelectedIndex()));
        tabMenu.add(closeTabItem);

        JMenuItem saveImageItem = new JMenuItem("Save Image", KeyEvent.VK_S);
        saveImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        saveImageItem.addActionListener(e -> nitfGuiManager().saveCurrentTabImage());
        tabMenu.add(saveImageItem);

        JMenuItem thumbnailItem = new JMenuItem("Create Thumbnail", KeyEvent.VK_T);
        thumbnailItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
        thumbnailItem.addActionListener(e -> nitfGuiManager().createThumbnail());
        tabMenu.add(thumbnailItem);

        JMenuItem chipItem = new JMenuItem("Create Chip", KeyEvent.VK_H);
        chipItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        chipItem.addActionListener(e -> nitfGuiManager().createChip("Chip"));
        tabMenu.add(chipItem);

        tabMenu.add(zoomMenu());

        return tabMenu;
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

    private JMenu zoomMenu() {
        JMenu zoomMenu = new JMenu("Zoom");

        JMenuItem oneHundredPercent = new JMenuItem("100%");
        oneHundredPercent.addActionListener(e -> nitfGuiManager().getActivePaintSurface().setScale(1.0));
        zoomMenu.add(oneHundredPercent);

        JMenuItem seventyFivePercent = new JMenuItem("75%");
        seventyFivePercent.addActionListener(e -> nitfGuiManager().getActivePaintSurface().setScale(0.5));
        zoomMenu.add(seventyFivePercent);

        JMenuItem fiftyPercent = new JMenuItem("50%");
        fiftyPercent.addActionListener(e -> nitfGuiManager().getActivePaintSurface().setScale(0.5));
        zoomMenu.add(fiftyPercent);

        JMenuItem twentyFivePercent = new JMenuItem("25%");
        twentyFivePercent.addActionListener(e -> nitfGuiManager().getActivePaintSurface().setScale(0.25));
        zoomMenu.add(twentyFivePercent);

        JMenuItem tenPercent = new JMenuItem("10%");
        tenPercent.addActionListener(e -> nitfGuiManager().getActivePaintSurface().setScale(0.10));
        zoomMenu.add(tenPercent);

        return zoomMenu;
    }

    @Bean
    public JFileChooser fileChooser() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        return fileChooser;
    }

    @Bean
    public String title() {
        return "Don's Ultimate NITF-File Optical Renderer";
    }

    @Bean
    public String copyright() {
        return "(c) Codice Foundation 2016";
    }
}

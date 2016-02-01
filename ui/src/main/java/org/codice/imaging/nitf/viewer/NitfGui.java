package org.codice.imaging.nitf.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.codice.imaging.nitf.core.NitfFileHeader;
import org.codice.imaging.nitf.core.image.NitfImageSegmentHeader;
import org.codice.imaging.nitf.render.NitfRenderer;
import org.codice.imaging.nitf.render.flow.NitfParserInputFlow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.coobird.thumbnailator.Thumbnails;

public class NitfGui {
    private static final Logger USER_LOGGER = LoggerFactory.getLogger(JTextArea.class);

    private static final Map<String, NitfFileHeader> FILE_HEADER_MAP = new HashMap<>();

    private final static JFileChooser FILE_CHOOSER = new JFileChooser();

    private static JTabbedPane mainPanel = new JTabbedPane();

    private static JPanel titlePanel;

    private static JFrame topFrame = new JFrame();

    private static JTextArea logPanel = new JTextArea();

    public static void main(String... args) throws Exception {
        UIManager.getDefaults().put("TabbedPane.contentBorderInsets", new Insets(0,0,0,0));
        UIManager.getDefaults().put("TabbedPane.tabsOverlapBorder", true);
        FILE_CHOOSER.setFileSelectionMode(JFileChooser.FILES_ONLY);
        topFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        topFrame.setLocation(200, 50);
        topFrame.setSize(1000, 600);
        topFrame.setLayout(new BorderLayout());
        topFrame.setTitle(NitfGuiComponentFactory.TITLE);

        titlePanel = NitfGuiComponentFactory.createTitlePanel();
        logPanel.setRows(5);
        logPanel.setEditable(false);

        mainPanel.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        topFrame.getContentPane()
                .add(titlePanel, BorderLayout.CENTER);
        topFrame.add(logPanel, BorderLayout.SOUTH);
        JMenuBar menuBar = new JMenuBar();
        createFileMenu(topFrame, menuBar);
        createTabMenu(topFrame, menuBar);

        topFrame.setJMenuBar(menuBar);
        topFrame.setVisible(true);
    }

    private static void createTabMenu(JFrame jframe, JMenuBar menuBar) {
        JMenu tabMenu = new JMenu("Image");
        tabMenu.setMnemonic(KeyEvent.VK_T);

        JMenuItem closeTabItem = new JMenuItem("Close", KeyEvent.VK_C);
        closeTabItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.ALT_MASK));
        closeTabItem.addActionListener(e -> { mainPanel.remove(mainPanel.getSelectedComponent());
           System.gc(); } );
        tabMenu.add(closeTabItem);

        JMenuItem saveImageItem = new JMenuItem("Save Image", KeyEvent.VK_S);
        saveImageItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, ActionEvent.ALT_MASK));
        saveImageItem.addActionListener(e -> saveCurrentTabImage());
        tabMenu.add(saveImageItem);

        JMenuItem thumbnailItem = new JMenuItem("Create Thumbnail", KeyEvent.VK_T);
        thumbnailItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T, ActionEvent.ALT_MASK));
        thumbnailItem.addActionListener(e -> createThumbnail());
        tabMenu.add(thumbnailItem);

        JMenuItem chipItem = new JMenuItem("Create Chip", KeyEvent.VK_H);
        chipItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_H, ActionEvent.ALT_MASK));
        chipItem.addActionListener(e -> createChip());
        tabMenu.add(chipItem);

        menuBar.add(tabMenu);
    }

    private static void createChip() {
        BufferedImage bi = NitfGuiComponentFactory.getSelectedAreaOfImage(mainPanel);
        JDialog dialog = new JDialog();
        JLabel label = new JLabel(new ImageIcon(bi));
        label.setOpaque(false);
        
        dialog.getContentPane().add(label);
        dialog.setSize(new Dimension(bi.getWidth(), bi.getHeight()));
        dialog.setVisible(true);
    }

    private static void createFileMenu(JFrame jframe, JMenuBar menuBar) {
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        menuBar.add(fileMenu);
        JMenuItem openItem = new JMenuItem("Open", KeyEvent.VK_O);
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        openItem.addActionListener(e -> {
            FILE_CHOOSER.setDialogTitle("Open NITF");
            FileFilter fileFilter = new FileNameExtensionFilter("NITF File", "ntf", "nsf", "NTF", "NSF");
            FILE_CHOOSER.setFileFilter(fileFilter);
            int userSelection = FILE_CHOOSER.showOpenDialog(jframe);

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File path = FILE_CHOOSER.getSelectedFile();

                if (verifyFileExists(path)) {
                    topFrame.getContentPane()
                            .remove(titlePanel);
                    topFrame.getContentPane()
                            .add(mainPanel, BorderLayout.CENTER);

                    SwingWorker<Void, Void> worker = new SwingWorker() {
                        @Override
                        protected Object doInBackground() throws Exception {
                            render(path, (img, header) -> addImageToFrame(path, img, header));
                            System.gc();
                            return null;
                        }
                    };

                    worker.execute();
                }
            }

            FILE_CHOOSER.removeChoosableFileFilter(fileFilter);
        });

        fileMenu.add(openItem);

        JMenuItem exitItem = new JMenuItem("Exit", KeyEvent.VK_X);
        exitItem.addActionListener(e -> System.exit(0));

        fileMenu.add(exitItem);
    }

    private static void saveCurrentTabImage() {
        FILE_CHOOSER.setDialogTitle("Save image (.png)");
        FileFilter fileFilter = new FileNameExtensionFilter("PNG File", "png", "PNG");
        FILE_CHOOSER.setFileFilter(fileFilter);
        int c = FILE_CHOOSER.showSaveDialog(topFrame);

        if (c == JFileChooser.APPROVE_OPTION) {
            File outputFile = FILE_CHOOSER.getSelectedFile();

            ProgressMonitor progressMonitor = new ProgressMonitor(topFrame,
                    "Saving File... ",
                    "",
                    0,
                    100);
            BufferedImage bi = NitfGuiComponentFactory.getDisplayedImage(mainPanel);
            NitfGuiComponentFactory.startProgressMonitor(progressMonitor, 1, 25);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws IOException {
                    ImageIO.write(bi, "png", outputFile);
                    NitfGuiComponentFactory.haltProgressMonitor(progressMonitor);
                    return null;
                }
            };

            worker.execute();
        }

        FILE_CHOOSER.removeChoosableFileFilter(fileFilter);
    }

    private static void createThumbnail() {
        FILE_CHOOSER.setDialogTitle("Create thumbnail (.jpg)");
        FileFilter fileFilter = new FileNameExtensionFilter("JPEG File", "jpg", "JPG", "jpeg", "JPEG");
        FILE_CHOOSER.setFileFilter(fileFilter);
        int c = FILE_CHOOSER.showSaveDialog(topFrame);

        if (c == JFileChooser.APPROVE_OPTION) {
            ProgressMonitor progressMonitor = new ProgressMonitor(topFrame,
                    "Creating thumbnail...",
                    "",
                    0,
                    250);

            BufferedImage bi = NitfGuiComponentFactory.getDisplayedImage(mainPanel);

            SwingWorker<Void, Void> worker = new SwingWorker() {
                protected Object doInBackground() {
                    try {
                        NitfGuiComponentFactory.startProgressMonitor(progressMonitor, 1, 3);
                        File tempFile =
                                File.createTempFile(mainPanel.getTitleAt(mainPanel.getSelectedIndex()),
                                        ".tmp");
                        ImageIO.write(bi, "jpg", tempFile);

                        Thumbnails.of(tempFile)
                                .size(640, 480)
                                .outputFormat("jpg")
                                .toFile(FILE_CHOOSER.getSelectedFile());

                        tempFile.delete();
                        NitfGuiComponentFactory.haltProgressMonitor(progressMonitor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            worker.execute();
        }

        FILE_CHOOSER.removeChoosableFileFilter(fileFilter);
    }

    private static void render(File nitfRgbFile,
            BiConsumer<BufferedImage, NitfImageSegmentHeader> consumer) {
        ProgressMonitor progressMonitor = new ProgressMonitor(topFrame,
                "Loading File: " + nitfRgbFile.getName(),
                "",
                0,
                100);

        NitfGuiComponentFactory.startProgressMonitor(progressMonitor, 0, 5);

        try {
            new NitfParserInputFlow().file(nitfRgbFile)
                    .imageData()
                    .fileHeader(header -> FILE_HEADER_MAP.put(nitfRgbFile.getName(), header))
                    .forEachImage((segment, bytes) -> {
                        NitfRenderer renderer = new NitfRenderer();

                        try {
                            BufferedImage img = renderer.render(segment, bytes);
                            consumer.accept(img, segment);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });

            NitfGuiComponentFactory.haltProgressMonitor(progressMonitor);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static void addImageToFrame(File imagePath, BufferedImage bufferedImage,
            NitfImageSegmentHeader imageSegmentHeader) {
        String tabName = imagePath.getName();
        tabName = tabName.substring(0, tabName.length() - 4);

        NitfFileHeader fileHeader = FILE_HEADER_MAP.get(imagePath.getName());
        JSplitPane outer = NitfGuiComponentFactory.getjSplitPane(bufferedImage,
                fileHeader,
                imageSegmentHeader);

        NitfGuiComponentFactory.createTab(mainPanel, tabName, outer);
    }

    private static boolean verifyFileExists(File file) {

        if (!file.exists()) {
            System.out.println(String.format("Can't locate input file: %s.", file.getName()));
            return false;
        }

        return true;
    }
}

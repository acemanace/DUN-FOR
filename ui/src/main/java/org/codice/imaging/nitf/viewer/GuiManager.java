package org.codice.imaging.nitf.viewer;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;
import javax.imageio.ImageIO;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ProgressMonitor;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.codice.imaging.nitf.core.NitfFileHeader;
import org.codice.imaging.nitf.core.image.NitfImageSegmentHeader;
import org.codice.imaging.nitf.render.NitfRenderer;
import org.codice.imaging.nitf.render.flow.NitfParserInputFlow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import net.coobird.thumbnailator.Thumbnails;

@Component
public class GuiManager {
    @Autowired
    private JTabbedPane mainPanel;

    @Autowired
    private JFileChooser fileChooser;

    @Autowired
    private JFrame topFrame;

    @Autowired
    private TabPanelFactory tabPanelFactory;

    @Autowired
    private JPanel titlePanel;


    private static final Map<String, NitfFileHeader> FILE_HEADER_MAP = new HashMap<>();

    private static Set<ProgressMonitor> runningMonitors = new HashSet<>();

    public PaintSurface getActivePaintSurface() {
        PropertiesImageTab propertiesImageTab = (PropertiesImageTab) mainPanel.getSelectedComponent();
        return propertiesImageTab.getPaintSurface();
    }

    static void startProgressMonitor(ProgressMonitor progressMonitor, int initialProgress, int maxIncrement) {
        runningMonitors.add(progressMonitor);
        Random random = new Random();

        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                ThreadLocal<Integer> progress = new ThreadLocal<>();
                progress.set(initialProgress);

                while (!progressMonitor.isCanceled() &&
                        runningMonitors.contains(progressMonitor)) {
                    progressMonitor.setProgress(progress.get());
                    Thread.sleep(500);
                    progress.set(progress.get() + random.nextInt(maxIncrement) + 1);
                }

                if (runningMonitors.contains(progressMonitor)) {
                    runningMonitors.remove(progressMonitor);
                }

                return null;
            }
        };

        worker.execute();
    }

    static void haltProgressMonitor(ProgressMonitor progressMonitor) {
        if (runningMonitors.contains(progressMonitor)) {
            runningMonitors.remove(progressMonitor);
        }

        progressMonitor.setProgress(progressMonitor.getMaximum());
    }

    public void createChip(String clipName) {
        BufferedImage bi = getActivePaintSurface().getSelectedAreaImage();
        JDialog dialog = new JDialog();
        ImagePanel imagePanel = new ImagePanel(bi, 800);
        imagePanel.setOpaque(false);

        dialog.setTitle(clipName);
        dialog.getContentPane().setLayout(new BorderLayout());
        dialog.getContentPane().add(imagePanel, BorderLayout.CENTER);
        dialog.setSize(new Dimension(bi.getWidth(), bi.getHeight() + 25));
        dialog.setLocation(220, 120);
        dialog.setVisible(true);
        imagePanel.repaint();
    }

    public void saveCurrentTabImage() {
        fileChooser.setDialogTitle("Save image (.png)");
        FileFilter fileFilter = new FileNameExtensionFilter("PNG File", "png", "PNG");
        fileChooser.setFileFilter(fileFilter);
        int c = fileChooser.showSaveDialog(topFrame);

        if (c == JFileChooser.APPROVE_OPTION) {
            File outputFile = fileChooser.getSelectedFile();

            ProgressMonitor progressMonitor = new ProgressMonitor(topFrame,
                    "Saving File... ",
                    "",
                    0,
                    100);
            BufferedImage bi = getActivePaintSurface().getBackgroundImage();
            startProgressMonitor(progressMonitor, 1, 25);

            SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
                @Override
                protected Void doInBackground() throws IOException {
                    ImageIO.write(bi, "png", outputFile);
                    haltProgressMonitor(progressMonitor);
                    return null;
                }
            };

            worker.execute();
        }

        fileChooser.removeChoosableFileFilter(fileFilter);
    }

    public void createThumbnail() {
        fileChooser.setDialogTitle("Create thumbnail (.jpg)");
        FileFilter fileFilter = new FileNameExtensionFilter("JPEG File", "jpg", "JPG", "jpeg", "JPEG");
        fileChooser.setFileFilter(fileFilter);
        int c = fileChooser.showSaveDialog(topFrame);

        if (c == JFileChooser.APPROVE_OPTION) {
            ProgressMonitor progressMonitor = new ProgressMonitor(topFrame,
                    "Creating thumbnail...",
                    "",
                    0,
                    250);

            BufferedImage bi = getActivePaintSurface().getBackgroundImage();

            SwingWorker<Void, Void> worker = new SwingWorker() {
                protected Object doInBackground() {
                    try {
                        startProgressMonitor(progressMonitor, 1, 3);
                        File tempFile =
                                File.createTempFile(mainPanel.getTitleAt(mainPanel.getSelectedIndex()),
                                        ".tmp");
                        ImageIO.write(bi, "jpg", tempFile);

                        Thumbnails.of(tempFile)
                                .size(200, 200)
                                .outputFormat("jpg")
                                .toFile(fileChooser.getSelectedFile());

                        tempFile.delete();
                        haltProgressMonitor(progressMonitor);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                }
            };

            worker.execute();
        }

        fileChooser.removeChoosableFileFilter(fileFilter);
    }

    private void addImageToFrame(File imagePath, BufferedImage bufferedImage,
            NitfImageSegmentHeader imageSegmentHeader) {
        String tabName = imagePath.getName();
        tabName = tabName.substring(0, tabName.length() - 4);

        NitfFileHeader fileHeader = FILE_HEADER_MAP.get(imagePath.getName());
        PropertiesImageTab propertiesImageTab = tabPanelFactory.createNitfImagePanel(bufferedImage,
                fileHeader,
                imageSegmentHeader);

        JPanel tab = tabPanelFactory.createTab(tabName);
        mainPanel.addTab(tabName, propertiesImageTab);
        mainPanel.setSelectedComponent(propertiesImageTab);
        mainPanel.setTabComponentAt(mainPanel.getSelectedIndex(), tab);
    }

    private void render(File nitfRgbFile,
            BiConsumer<BufferedImage, NitfImageSegmentHeader> consumer) {
        ProgressMonitor progressMonitor = new ProgressMonitor(topFrame,
                "Loading File: " + nitfRgbFile.getName(),
                "",
                0,
                100);

        startProgressMonitor(progressMonitor, 0, 5);

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

            haltProgressMonitor(progressMonitor);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void openFile() {
        fileChooser.setDialogTitle("Open NITF");
        FileFilter fileFilter = new FileNameExtensionFilter("NITF File", "ntf", "nitf", "nsf", "NTF", "NITF", "NSF");
        fileChooser.setFileFilter(fileFilter);
        int userSelection = fileChooser.showOpenDialog(topFrame);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File path = fileChooser.getSelectedFile();

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

        fileChooser.removeChoosableFileFilter(fileFilter);
    }

    public void closeTab(String tabName) {
        int index = mainPanel.indexOfTab(tabName);
        closeTab(index);
    }

    public void closeTab(int index) {
        mainPanel.removeTabAt(index);
        System.gc();

        if (mainPanel.getSelectedIndex() == -1) {
            topFrame.getContentPane().remove(mainPanel);
            topFrame.getContentPane()
                    .add(titlePanel, BorderLayout.CENTER);
            topFrame.repaint();
        }
    }
}

package org.codice.imaging.nitf.viewer;

import java.awt.image.BufferedImage;
import java.util.function.Supplier;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import org.codice.imaging.nitf.core.NitfFileHeader;
import org.codice.imaging.nitf.core.image.ImageCoordinatePair;
import org.codice.imaging.nitf.core.image.ImageCoordinates;
import org.codice.imaging.nitf.core.image.NitfImageSegmentHeader;
import org.codice.imaging.nitf.core.security.FileSecurityMetadata;

public class PropertiesImageTab extends JSplitPane {

    private ImagePanel imagePanel;

    public PropertiesImageTab(final BufferedImage bufferedImage,
            final NitfFileHeader fileHeader,
            final NitfImageSegmentHeader header) {
        super(JSplitPane.HORIZONTAL_SPLIT);

        this.imagePanel = new ImagePanel(bufferedImage, 400);
        JTable imageProperties = getImagePropertyTable(header);
        JScrollPane imagePropertiesScrollPane = new JScrollPane(imageProperties);
        JTabbedPane imagePropertiesTab = new JTabbedPane();
        imagePropertiesTab.addTab("Image Properties", imagePropertiesScrollPane);

        JTable fileProperties = getFilePropertyTable(fileHeader);

        JScrollPane filePropertiesScrollPane = new JScrollPane(fileProperties);
        JTabbedPane filePropertiesTab = new JTabbedPane();
        filePropertiesTab.addTab("File Properties", filePropertiesScrollPane);

        JSplitPane propertiesPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        propertiesPane.setDividerLocation(260);
        propertiesPane.setTopComponent(imagePropertiesTab);
        propertiesPane.setBottomComponent(filePropertiesTab);

        this.setLeftComponent(imagePanel);
        this.setRightComponent(propertiesPane);

        SwingUtilities.invokeLater(() -> propertiesPane.setDividerLocation(0.7d));
    }

    private static JTable getFilePropertyTable(NitfFileHeader header) {
        FileSecurityMetadata securityMetadata = header.getFileSecurityMetadata();

        String[][] data = {{"Title:", header.getFileTitle()}, {"ID:", header.getIdentifier()},
            {"Classification:", securityMetadata.getSecurityClassification().name()},
            {"Origin ID:", header.getOriginatingStationId()},
            {"Origin Phone:", header.getOriginatorsPhoneNumber()},
            {"Standard Type:", header.getStandardType()},
            {"Complexity Level:", "" + header.getComplexityLevel()},
            {"Date:", header.getFileDateTime().getSourceString()}, {"Image Count:",
            "" + header.getImageSegmentSubHeaderLengths()
                    .size()},
            {"Class Authority:", securityMetadata.getClassificationAuthority()},
            {"Class Auth Type:", securityMetadata.getClassificationAuthorityType()},
            {"Class Text:", securityMetadata.getClassificationText()},
            {"Class Reason:", securityMetadata.getClassificationReason()} };

        String[] headers = {"Property", "Value"};
        JTable fileProperties = new JTable(data, headers);
        fileProperties.setShowGrid(false);
        return fileProperties;
    }

    private JTable getImagePropertyTable(NitfImageSegmentHeader header) {
        ImageCoordinates imageCoordinates = header.getImageCoordinates();

        String[][] data = {{"Identifier: ", header.getIdentifier()},
            {"Source: ", header.getImageSource()},
            {"Horizontal Pixels/Block",
            "" + header.getNumberOfPixelsPerBlockHorizontal()},
            {"Vertical Pixels/Block", "" + header.getNumberOfPixelsPerBlockVertical()},
            {"Block Size:", "" + header.getNumberOfPixelsPerBlockHorizontal()
                    * header.getNumberOfPixelsPerBlockVertical()},
            {"Blocks/Row: ", "" + header.getNumberOfBlocksPerRow()},
            {"Blocks/Column: ", "" + header.getNumberOfBlocksPerColumn()},
            {"Rows: ", "" + header.getNumberOfRows()},
            {"Columns: ", "" + header.getNumberOfColumns()},
            {"Image Compression: ", header.getImageCompression().name()},
            {"Image Mode: ", "" + header.getImageMode()},
            {"Image Representation: ", header.getImageRepresentation().name()},
            {"Image Category: ", header.getImageCategory().name()},
            {"bpp/Band:", "" + header.getActualBitsPerPixelPerBand()},
            {"Bands:", "" + header.getNumBands()},
            {"Pixel Value Type:", header.getPixelValueType().name()},
            {"Pixel Justification:", header.getPixelJustification().name()},
            {"Image Coordinates:", ""},
            {"    Coord 0,0:", getImageCoordinate(imageCoordinates, () -> imageCoordinates.getCoordinate00())},
            {"    Coord 0,max:", getImageCoordinate(imageCoordinates, () -> imageCoordinates.getCoordinate0MaxCol())},
            {"    Coord max,max:", getImageCoordinate(imageCoordinates, () -> imageCoordinates.getCoordinateMaxRowMaxCol())},
            {"    Coord max,0:", getImageCoordinate(imageCoordinates, () -> imageCoordinates.getCoordinateMaxRow0())},
        };

        String[] headers = {"Property", "Value"};
        JTable imageProperties = new JTable(data, headers);
        imageProperties.setShowGrid(false);
        return imageProperties;
    }

    private String getImageCoordinate(ImageCoordinates imageCoordinates, Supplier<ImageCoordinatePair> supplier) {
        if (imageCoordinates != null) {
            ImageCoordinatePair icp = supplier.get();
            return String.format("%5f, %5f", icp.getLatitude(), icp.getLongitude());
        }

        return "";
    }

    public PaintSurface getPaintSurface() {
        return imagePanel.getPaintSurface();
    }
}

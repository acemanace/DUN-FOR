package org.codice.imaging.nitf.viewer.nitfbuilder;

import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.common.FileType;
import org.codice.imaging.nitf.core.image.*;
import org.codice.imaging.nitf.core.security.FileSecurityMetadata;

import javax.imageio.stream.ImageInputStream;
import java.util.function.Supplier;

public class ImageSegmentCreationFlow implements Supplier<ImageSegment> {
    private ImageSegment imageSegment;

    private Supplier<FileSecurityMetadata> securityMetadataSupplier;

    public ImageSegmentCreationFlow() {
        this.imageSegment = new ImageSegmentImpl();
    }

    public ImageSegmentCreationFlow dateTime(DateTime imageDateTime) {
        imageSegment.setImageDateTime(imageDateTime);
        return this;
    }

    public ImageSegmentCreationFlow targetId(TargetId imageTargetId) {
        imageSegment.setImageTargetId(imageTargetId);
        return this;
    }

    public ImageSegmentCreationFlow identifier2(String imageIdentifier) {
        imageSegment.setImageIdentifier2(imageIdentifier);
        return this;
    }

    public ImageSegmentCreationFlow source(String imageSource) {
        imageSegment.setImageSource(imageSource);
        return this;
    }

    public ImageSegmentCreationFlow rows(long numberOfRows) {
        imageSegment.setNumberOfRows(numberOfRows);
        return this;
    }

    public ImageSegmentCreationFlow columns(long numberOfColumns) {
        imageSegment.setNumberOfColumns(numberOfColumns);
        return this;
    }

    public ImageSegmentCreationFlow pixelValueType(PixelValueType pixelValueType) {
        imageSegment.setPixelValueType(pixelValueType);
        return this;
    }

    public ImageSegmentCreationFlow representation(ImageRepresentation imageRepresentation) {
        imageSegment.setImageRepresentation(imageRepresentation);
        return this;
    }

    public ImageSegmentCreationFlow category(ImageCategory imageCategory) {
        imageSegment.setImageCategory(imageCategory);
        return this;
    }

    public ImageSegmentCreationFlow actualBitsPerPixelPerBand(int actualBitsPerPixelPerBand) {
        imageSegment.setActualBitsPerPixelPerBand(actualBitsPerPixelPerBand);
        return this;
    }

    public ImageSegmentCreationFlow pixelJustification(PixelJustification pixelJustification) {
        imageSegment.setPixelJustification(pixelJustification);
        return this;
    }

    public ImageSegmentCreationFlow coordinateRepresentation(ImageCoordinatesRepresentation imageCoordinatesRepresentation) {
        imageSegment.setImageCoordinatesRepresentation(imageCoordinatesRepresentation);
        return this;
    }

    public ImageSegmentCreationFlow comment(String imageComment) {
        imageSegment.addImageComment(imageComment);
        return this;
    }

    public ImageSegmentCreationFlow compression(ImageCompression imageCompression) {
        imageSegment.setImageCompression(imageCompression);
        return this;
    }

    public ImageSegmentCreationFlow compressionRate(String compressionRate) {
        imageSegment.setCompressionRate(compressionRate);
        return this;
    }

    public ImageSegmentCreationFlow band(ImageBand imageBand) {
        imageSegment.addImageBand(imageBand);
        return this;
    }

    public ImageSegmentCreationFlow mode(ImageMode imageMode) {
        imageSegment.setImageMode(imageMode);
        return this;
    }

    public ImageSegmentCreationFlow blocksPerRow(int numberOfBlocksPerRow) {
        imageSegment.setNumberOfBlocksPerRow(numberOfBlocksPerRow);
        return this;
    }

    public ImageSegmentCreationFlow blocksPerColumn(int numberOfBlocksPerColumn) {
        imageSegment.setNumberOfBlocksPerColumn(numberOfBlocksPerColumn);
        return this;
    }

    public ImageSegmentCreationFlow pixelsPerBlockHorizontal(int numberOfPixelsPerBlockHorizontal) {
        imageSegment.setNumberOfPixelsPerBlockHorizontalRaw(numberOfPixelsPerBlockHorizontal);
        return this;
    }

    public ImageSegmentCreationFlow pixelsPerBlockVertical(int numberOfPixelsPerBlockVertical) {
        imageSegment.setNumberOfPixelsPerBlockVerticalRaw(numberOfPixelsPerBlockVertical);
        return this;
    }

    public ImageSegmentCreationFlow bitsPerPixelPerBand(int numberOfBitsPerPixelPerBand) {
        imageSegment.setNumberOfBitsPerPixelPerBand(numberOfBitsPerPixelPerBand);
        return this;
    }

    public ImageSegmentCreationFlow displayLevel(int imageDisplayLevel) {
        imageSegment.setImageDisplayLevel(imageDisplayLevel);
        return this;
    }

    public ImageSegmentCreationFlow locationRow(int imageLocationRow) {
        imageSegment.setImageLocationRow(imageLocationRow);
        return this;
    }

    public ImageSegmentCreationFlow locationColumn(int imageLocationColumn) {
        imageSegment.setImageLocationColumn(imageLocationColumn);
        return this;
    }

    public ImageSegmentCreationFlow magnification(String imageMagnification) {
        imageSegment.setImageMagnification(imageMagnification);
        return this;
    }

    public ImageSegmentCreationFlow coordinates(ImageCoordinates imageCoordinates) {
        imageSegment.setImageCoordinates(imageCoordinates);
        return this;
    }

    public ImageSegmentCreationFlow userDefinedHeaderOverflow(int userDefinedHeaderOverflow) {
        imageSegment.setUserDefinedHeaderOverflow(userDefinedHeaderOverflow);
        return this;
    }

    public ImageSegmentCreationFlow imageData(ImageInputStream imageInputStream) {
        imageSegment.setData(imageInputStream);
        return this;
    }

    public ImageSegmentCreationFlow imageDataLength(int imageDataLength) {
        imageSegment.setDataLength(imageDataLength);
        return this;
    }

    public ImageSegmentCreationFlow extendedHeaderDataOverflow(int extendedHeaderDataOverflow) {
        imageSegment.setExtendedHeaderDataOverflow(extendedHeaderDataOverflow);
        return this;
    }

    public ImageSegmentCreationFlow attachmentLevel(int attachmentLevel) {
        imageSegment.setAttachmentLevel(attachmentLevel);
        return this;
    }

    public ImageSegmentCreationFlow identifier(String identifier) {
        imageSegment.setIdentifier(identifier);
        return this;
    }

    public ImageSegmentCreationFlow securityMetadata(Supplier<FileSecurityMetadata> securityMetadataSupplier) {
        this.securityMetadataSupplier = securityMetadataSupplier;
        return this;
    }

    public ImageSegmentCreationFlow fileType(FileType fileType) {
        imageSegment.setFileType(fileType);
        return this;
    }

    @Override
    public ImageSegment get() {
        imageSegment.setSecurityMetadata(securityMetadataSupplier.get());
        return this.imageSegment;
    }
}

/**
 * Copyright (c) Connexta, LLC
 * <p/>
 * This is free software: you can redistribute it and/or modify it under the terms of the GNU Lesser
 * General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or any later version.
 * <p/>
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details. A copy of the GNU Lesser General Public License
 * is distributed along with this program and can be found at
 * <http://www.gnu.org/licenses/lgpl.html>.
 */
package org.codice.imaging.nitf.nitfpeek;

import java.io.Serializable;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.function.Function;

import org.codice.imaging.nitf.core.common.DateTime;
import org.codice.imaging.nitf.core.image.ImageSegment;


/**
 * NitfAttributes to represent the properties of a ImageSegment.
 */
enum ImageAttribute {

    FILE_PART_TYPE("filePartType", "IM", header -> "IM"),
    IMAGE_IDENTIFIER_1("imageIdentifier1", "IID1", ImageSegment::getIdentifier),
    IMAGE_DATE_AND_TIME("imageDateAndTime", "IDATIM",
            header -> convertNitfDate(header.getImageDateTime())),
    TARGET_IDENTIFIER("targetIdentifier", "TGTID",
            header -> header.getImageTargetId().toString().trim()),
    IMAGE_IDENTIFIER_2("imageIdentifier2", "IID2", ImageSegment::getImageIdentifier2),
    IMAGE_SECURITY_CLASSIFICATION("imageSecurityClassification", "ISCLAS",
            header -> header.getSecurityMetadata().getSecurityClassification().name()),
    IMAGE_CLASSIFICATION_SECURITY_SYSTEM("imageClassificationSecuritySystem", "ISCLSY",
            header -> header.getSecurityMetadata().getSecurityClassificationSystem()),
    IMAGE_CODEWORDS("imageCodewords", "ISCODE",
            header -> header.getSecurityMetadata().getCodewords()),
    IMAGE_CONTROL_AND_HANDLING("imageControlandHandling", "ISCTLH",
            header -> header.getSecurityMetadata().getControlAndHandling()),
    IMAGE_RELEASING_INSTRUCTIONS("imageReleasingInstructions", "ISREL",
            header -> header.getSecurityMetadata().getReleaseInstructions()),
    IMAGE_DECLASSIFICATION_TYPE("imageDeclassificationType", "ISDCTP",
            header -> header.getSecurityMetadata().getDeclassificationType()),
    IMAGE_DECLASSIFICATION_DATE("imageDeclassificationDate", "ISDCDT",
            header -> header.getSecurityMetadata().getDeclassificationDate()),
    IMAGE_DECLASSIFICATION_EXEMPTION("imageDeclassificationExemption", "ISDCXM",
            header -> header.getSecurityMetadata().getDeclassificationExemption()),
    IMAGE_DOWNGRADE("imageDowngrade", "ISDG",
            header -> header.getSecurityMetadata().getDowngrade()),
    IMAGE_DOWNGRADE_DATE("imageDowngradeDate", "ISDGDT",
            header -> header.getSecurityMetadata().getDowngradeDate()),
    IMAGE_CLASSIFICATION_TEXT("imageClassificationText", "ISCLTX",
            header -> header.getSecurityMetadata().getClassificationText()),
    IMAGE_CLASSIFICATION_AUTHORITY_TYPE("imageClassificationAuthorityType", "ISCATP",
            header -> header.getSecurityMetadata().getClassificationAuthorityType()),
    IMAGE_CLASSIFICATION_AUTHORITY("imageClassificationAuthority", "ISCAUT",
            header -> header.getSecurityMetadata().getClassificationAuthority()),
    IMAGE_CLASSIFICATION_REASON("imageClassificationReason", "ISCRSN",
            header -> header.getSecurityMetadata().getClassificationReason()),
    IMAGE_SECURITY_SOURCE_DATE("imageSecuritySourceDate", "ISSRDT",
            header -> header.getSecurityMetadata().getSecuritySourceDate()),
    IMAGE_SECURITY_CONTROL_NUMBER("imageSecurityControlNumber", "ISCTLN",
            header -> header.getSecurityMetadata().getSecurityControlNumber()),
    IMAGE_SOURCE("imageSource", "ISORCE", ImageSegment::getImageSource),
    NUMBER_OF_SIGNIFICANT_ROWS_IN_IMAGE("numberOfSignificantRowsInImage", "NROWS",
            ImageSegment::getNumberOfRows),
    NUMBER_OF_SIGNIFICANT_COLUMNS_IN_IMAGE("numberOfSignificantColumnsInImage", "NCOLS",
            ImageSegment::getNumberOfColumns),
    PIXEL_VALUE_TYPE("pixelValueType", "PVTYPE", header -> header.getPixelValueType().name()),
    IMAGE_REPRESENTATION("imageRepresentation", "IREP",
            header -> header.getImageRepresentation().name()),
    IMAGE_CATEGORY("imageCategory", "ICAT", header -> header.getImageCategory().name()),
    ACTUAL_BITS_PER_PIXEL_PER_BAND("actualBitsPerPixelPerBand", "ABPP",
            ImageSegment::getActualBitsPerPixelPerBand),
    PIXEL_JUSTIFICATION("pixelJustification", "PJUST",
            header -> header.getPixelJustification().name()),
    IMAGE_COORDINATE_REPRESENTATION("imageCoordinateRepresentation", "ICORDS",
            header -> header.getImageCoordinatesRepresentation().name()),
    NUMBER_OF_IMAGE_COMMENTS("numberOfImageComments", "NICOM",
            header -> header.getImageComments().size()),
    IMAGE_COMMENT_1("imageComment1", "ICOM1",
            header -> header.getImageComments().size() > 0 ? header.getImageComments().get(0) : ""),
    IMAGE_COMMENT_2("imageComment2", "ICOM2",
            header -> header.getImageComments().size() > 1 ? header.getImageComments().get(1) : ""),
    IMAGE_COMMENT_3("imageComment3", "ICOM3",
            header -> header.getImageComments().size() > 2 ? header.getImageComments().get(2) : ""),
    IMAGE_COMPRESSION("imageCompression", "IC", header -> header.getImageCompression().name()),
    NUMBER_OF_BANDS("numberOfBands", "NBANDS", ImageSegment::getNumBands),
    IMAGE_MODE("imageMode", "IMODE", header -> header.getImageMode().name()),
    NUMBER_OF_BLOCKS_PER_ROW("numberOfBlocksperRow", "NBPR",
            ImageSegment::getNumberOfBlocksPerRow),
    NUMBER_OF_BLOCKS_PER_COLUMN("numberOfBlocksperColumn", "NBPC",
            ImageSegment::getNumberOfBlocksPerColumn),
    NUMBER_OF_PIXELS_PER_BLOCK_HORIZONTAL("numberOfPixelsPerBlockHorizontal", "NPPBH",
            ImageSegment::getNumberOfPixelsPerBlockHorizontal),
    NUMBER_OF_PIXELS_PER_BLOCK_VERTICAL("numberOfPixelsPerBlockVertical", "NPPBV",
            ImageSegment::getNumberOfPixelsPerBlockVertical),
    NUMBER_OF_BITS_PER_PIXEL("numberOfBitsPerPixel", "NBPP",
            ImageSegment::getNumberOfBitsPerPixelPerBand),
    IMAGE_DISPLAY_LEVEL("imageDisplayLevel", "IDLVL", ImageSegment::getImageDisplayLevel),
    IMAGE_ATTACHMENT_LEVEL("imageAttachmentLevel", "IALVL",
            ImageSegment::getAttachmentLevel),
    IMAGE_LOCATION("imageLocation", "ILOC",
            header -> header.getImageCoordinates().getCoordinate00().getLatitude() + ", " + header.getImageCoordinates().getCoordinate00().getLongitude() + ":"
                    + header.getImageCoordinates().getCoordinate0MaxCol().getLatitude() + ", " + header.getImageCoordinates().getCoordinate0MaxCol().getLongitude()
                    + header.getImageCoordinates().getCoordinateMaxRowMaxCol().getLatitude() + ", " + header.getImageCoordinates().getCoordinateMaxRowMaxCol().getLongitude()
                    + header.getImageCoordinates().getCoordinateMaxRow0().getLatitude() + ", " + header.getImageCoordinates().getCoordinateMaxRow0().getLongitude()),
    IMAGE_MAGNIFICATION("imageMagnification", "IMAG",
            header -> header.getImageMagnification().trim());

    public static final String ATTRIBUTE_NAME_PREFIX = "nitf.image.";

    private String shortName;

    private String longName;

    private Function<ImageSegment, Serializable> accessorFunction;

    private ImageAttribute(final String lName, final String sName,
            final Function<ImageSegment, Serializable> accessor) {
        this.accessorFunction = accessor;
        this.shortName = sName;
        this.longName = lName;
    }

    private static Date convertNitfDate(DateTime nitfDateTime) {
        if (nitfDateTime == null || nitfDateTime.getZonedDateTime() == null) {
            return null;
        }

        ZonedDateTime zonedDateTime = nitfDateTime.getZonedDateTime();
        Instant instant = zonedDateTime.toInstant();

        if (instant != null) {
            return Date.from(instant);
        }

        return null;
    }

    public final String getShortName() {
        return this.shortName;
    }

    public final String getLongName() {
        return this.longName;
    }

    public Function<ImageSegment, Serializable> getAccessorFunction() {
        return accessorFunction;
    }

    public String toString() {
        return getLongName();
    }
}

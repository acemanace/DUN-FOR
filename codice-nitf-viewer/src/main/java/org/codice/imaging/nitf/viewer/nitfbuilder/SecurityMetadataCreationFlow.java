package org.codice.imaging.nitf.viewer.nitfbuilder;

import org.codice.imaging.nitf.core.security.SecurityClassification;
import org.codice.imaging.nitf.core.security.SecurityMetadata;

import java.util.function.Supplier;

public class SecurityMetadataCreationFlow implements Supplier<SecurityMetadata> {
    private SecurityMetadataImpl securityMetadata = new SecurityMetadataImpl();

    public SecurityMetadataCreationFlow classification(SecurityClassification securityClassification) {
        securityMetadata.setSecurityClassification(securityClassification);
        return this;
    }

    public SecurityMetadataCreationFlow classificationSystem(String securityClassificationSystem) {
        securityMetadata.setNitfSecurityClassificationSystem(securityClassificationSystem);
        return this;
    }

    public SecurityMetadataCreationFlow codewords(String codewords) {
        securityMetadata.setNitfCodewords(codewords);
        return this;
    }

    public SecurityMetadataCreationFlow controlAndHandling(String controlAndHandling) {
        securityMetadata.setNitfControlAndHandling(controlAndHandling);
        return this;
    }

    public SecurityMetadataCreationFlow releaseInstructions(String releaseInstructions) {
        securityMetadata.setNitfReleaseInstructions(releaseInstructions);
        return this;
    }

    public SecurityMetadataCreationFlow declassificationType(String declassificationType) {
        securityMetadata.setNitfDeclassificationType(declassificationType);
        return this;
    }

    public SecurityMetadataCreationFlow declassificationDate(String declassificationDate) {
        securityMetadata.setNitfDeclassificationDate(declassificationDate);
        return this;
    }

    public SecurityMetadataCreationFlow declassificationExemption(String declassificationExemption) {
        securityMetadata.setNitfDeclassificationExemption(declassificationExemption);
        return this;
    }

    public SecurityMetadataCreationFlow downgrade(String downgrade) {
        securityMetadata.setNitfDowngrade(downgrade);
        return this;
    }

    public SecurityMetadataCreationFlow downgradeDate(String downgradeDate) {
        securityMetadata.setNitfDowngradeDate(downgradeDate);
        return this;
    }

    public SecurityMetadataCreationFlow classificationText(String classificationText) {
        securityMetadata.setNitfClassificationText(classificationText);
        return this;
    }

    public SecurityMetadataCreationFlow classificationAuthorityType(String classificationAuthorityType) {
        securityMetadata.setNitfClassificationAuthorityType(classificationAuthorityType);
        return this;
    }

    public SecurityMetadataCreationFlow classificationAuthority(String classificationAuthority) {
        securityMetadata.setNitfClassificationAuthority(classificationAuthority);
        return this;
    }

    public SecurityMetadataCreationFlow classificationReason(String classificationReason) {
        securityMetadata.setNitfClassificationReason(classificationReason);
        return this;
    }

    public SecurityMetadataCreationFlow sourceDate(String sourceDate) {
        securityMetadata.setNitfSecuritySourceDate(sourceDate);
        return this;
    }

    public SecurityMetadataCreationFlow controlNumber(String controlNumber) {
        securityMetadata.setNitfSecurityControlNumber(controlNumber);
        return this;
    }

    public SecurityMetadataCreationFlow downgradeDateOrSpecialCase(String downgradeDateOrSpecialCase) {
        securityMetadata.setDowngradeDateOrSpecialCase(downgradeDateOrSpecialCase);
        return this;
    }

    @Override
    public SecurityMetadata get() {
        return null;
    }
}

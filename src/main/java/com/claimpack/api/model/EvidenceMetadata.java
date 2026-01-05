package com.claimpack.api.model;

import lombok.Data;
import java.util.List;

@Data
public class EvidenceMetadata {
    private String caseName;
    private String disputeCategory;
    private String summary;
    private List<ExhibitDetail> exhibits;
}
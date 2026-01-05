package com.claimpack.api.model;

import lombok.Data;

@Data
public class ExhibitDetail {
    private int fileIndex;
    private String title;
    private String description;
    private String dateOfEvent;
}
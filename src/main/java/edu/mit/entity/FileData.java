package edu.mit.entity;

import lombok.*;

@Data
public class FileData {
    private String name;
    private long size;
    private String lastmoddatetime;
    private String nicesize;
    private String status;
    private String setmoddatetimestatus;
}

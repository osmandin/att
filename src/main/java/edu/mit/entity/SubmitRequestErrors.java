package edu.mit.entity;

import lombok.*;

@Data
public class SubmitRequestErrors {
    private boolean duplicateuser = false;
    private boolean duplicateusername = false;
    private boolean duplicatedepartment = false;
    private boolean fullDuplicates = false;
    private boolean departmentalreadyassignedtothisuser = false;
    private boolean useremailfailed = false;
    private boolean wrongnumberofusermatches = false;
    private int ssaid = -1;
}

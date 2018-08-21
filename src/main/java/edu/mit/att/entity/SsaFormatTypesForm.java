package edu.mit.att.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ssaFormatTypes")
public class SsaFormatTypesForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String formattype;
    private int idx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ssaid")
    private SubmissionAgreement submissionAgreement;

}

package edu.mit.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ssaAccessRestrictions")
public class SsaAccessRestrictionsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String restriction;

    private int idx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ssaid")
    private SsasForm ssasForm;
}

package edu.mit.entity;

import javax.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "ssaCopyrights")
public class SsaCopyrightsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String copyright;

    private int idx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ssaid")
    private SsasForm ssasForm;
}

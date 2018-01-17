package edu.mit.entity;

import lombok.*;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString(exclude = {"ssasForm"})
@Entity
@Table(name = "ssaContacts")
public class SsaContactsForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name = "";
    private String phone = "";
    private String address = "";
    private String email = "";
    private int idx;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ssaid")
    private SsasForm ssasForm;
}

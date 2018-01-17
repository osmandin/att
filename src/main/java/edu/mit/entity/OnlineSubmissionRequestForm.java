package edu.mit.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "onlineSubmissionRequest")
public class OnlineSubmissionRequestForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private int ssaid;
    private String department;
    private String address;
    private String name;
    private String email;
    private String phone;
    private String departmenthead;
    private String signature;
    private String date;

    @Transient
    private String nicedate;

}

package edu.mit.entity;

import lombok.*;
import lombok.ToString;

import javax.persistence.*;

@Data
@ToString(exclude = {"rsasForm"})
@Entity
@Table(name = "rsaFileData")
public class RsaFileDataForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String name;
    private long size;
    private String nicesize;
    private String lastmoddatetime;
    private String status;
    private int idx;

    public String getLastmoddatetime() {
        // 2014-10-08 10:28:32.0
        if (lastmoddatetime != null && lastmoddatetime.length() > 19) {
            return lastmoddatetime.substring(0, 19);
        }
        return lastmoddatetime;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "rsaid")
    private RsasForm rsasForm;
}

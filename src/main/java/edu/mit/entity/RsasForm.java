package edu.mit.entity;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;

import lombok.*;

@Entity
@Table(name = "rsas")
@Data
@EqualsAndHashCode(exclude = {"ssasForm", "rsaFileDataForms"})
public class RsasForm {

    @Transient
    private int numfiles = 0;

    @Id
    @GeneratedValue
    private Integer id;

    private String startyear;
    private String endyear;
    private String description;
    private long extent = 0;
    private String extentstr;
    private String transferdate;
    private String accessionnumber;
    private String createdby;
    private boolean approved;
    private boolean deleted = false;
    private int idx;
    private String path; // Osm added path to file

    public void setTransferdate(String date) {
        if (date.equals("")) {
            this.transferdate = null;
        } else {
            this.transferdate = date;
        }
    }

    public String getStartyear() {
        if (startyear != null && startyear.length() > 4) {
            return startyear.substring(0, 4);
        }
        return startyear;
    }

    public String getEndyear() {
        if (endyear != null && endyear.length() > 4) {
            return endyear.substring(0, 4);
        }
        return endyear;
    }


    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ssaid")
    @NotFound(action = NotFoundAction.IGNORE)
    private SsasForm ssasForm;

    public int hashCode(SsasForm ssa) {
        return ssa.getId();
    }


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "rsasForm", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderColumn(name = "idx")
    @Setter(AccessLevel.NONE)
    private List<RsaFileDataForm> rsaFileDataForms;

    public void setRsaFileDataForms(List<RsaFileDataForm> fds) {
        if (fds != null) {
            int i = 0;
            for (RsaFileDataForm fd : fds) {
                fd.setIdx(i++);
            }
        }
        rsaFileDataForms = fds;
    }

    public int hashCode(RsaFileDataForm fd) {
        return fd.getId();
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}

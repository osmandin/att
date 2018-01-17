package edu.mit.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Table(name = "approvedRsas")
public class ApprovedRsasForm {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;

    private String datetime;
    private String accession;
    private int rsaid;
    private String departmentname;
    private String sendername;
    private String senderemail;
    private String approvername;
    private String approveremail;
    private String description;

    public String getDatetime() {
        //2015-11-25 23:28:26.0
        if (datetime != null && datetime.length() > 19) {
            return datetime.substring(0, 19);
        }
        return datetime;
    }

    // probably not needed
    public void setDatetime(String datetime) {
        //2015-11-25 23:28:26.0
        if (datetime != null && datetime.length() > 19) {
            this.datetime = datetime.substring(0, 19);
        } else {
            this.datetime = datetime;
        }
    }
}

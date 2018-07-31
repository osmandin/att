package edu.mit.entity;

import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;
import java.util.List;
import java.util.logging.Logger;

import lombok.*;

@Data
@Entity
@Table(name = "ssas")
@EqualsAndHashCode(exclude = {"ssaContactsForms", "ssaCopyrightsForms", "ssaAccessRestrictionsForms", "ssaFormatTypesForms"})
public class SsasForm {
    private final static Logger LOGGER = Logger.getLogger(SsasForm.class.getCanonicalName());

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String departmenthead;
    private String recordid;
    private String creationdate;
    private String recordstitle;
    private String effectivedate;
    private String expirationdate;
    private String retentionalertdate;
    private String retentionschedule;
    private String otherformattypes;
    private String recorddescription;
    private String retentionperiod;
    private String descriptionstandards;
    private String createdby;
    private String editdate;

    private boolean enabled = false;
    private boolean approved = false;
    private boolean deleted = false;

    private String IP;

    private boolean onlinesubmission = false;

    @Transient
    private List<DepartmentsForm> dropdownDepartmentsForms;


    public void setCreationdate(String date) {
        if (date.equals("")) {
            this.creationdate = null;
        } else {
            this.creationdate = date;
        }
    }

    public void setEffectivedate(String date) {
        if (date.equals("")) {
            this.effectivedate = null;
        } else {
            this.effectivedate = date;
        }
    }

    public void setExpirationdate(String date) {
        if (date.equals("")) {
            this.expirationdate = null;
        } else {
            this.expirationdate = date;
        }
    }

    public void setRetentionalertdate(String date) {
        if (date.equals("")) {
            this.retentionalertdate = null;
        } else {
            this.retentionalertdate = date;
        }
    }


    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "departmentid")
    @NotFound(action = NotFoundAction.IGNORE)
    private DepartmentsForm departmentForm;

    public int hashCode(DepartmentsForm df) {
        return df.getId();
    }


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ssasForm", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderColumn(name = "idx")
    @Setter(AccessLevel.NONE)
    private List<SsaContactsForm> ssaContactsForms;

    public void setSsaContactsForms(List<SsaContactsForm> cs) {
        if (cs != null) {
            int i = 0;
            for (SsaContactsForm c : cs) {
                c.setIdx(i++);
            }
        }
        ssaContactsForms = cs;
    }

    public int hashCode(SsaContactsForm cf) {
        return cf.getId();
    }


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ssasForm", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderColumn(name = "idx")
    @Setter(AccessLevel.NONE)
    private List<SsaCopyrightsForm> ssaCopyrightsForms;

    public void setSsaCopyrightsForms(List<SsaCopyrightsForm> crs) {
        if (crs != null) {
            int i = 0;
            for (SsaCopyrightsForm cr : crs) {
                cr.setIdx(i++);
            }
        }
        ssaCopyrightsForms = crs;
    }

    public int hashCode(SsaCopyrightsForm crf) {
        return crf.getId();
    }


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ssasForm", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderColumn(name = "idx")
    @Setter(AccessLevel.NONE)
    private List<SsaAccessRestrictionsForm> ssaAccessRestrictionsForms;

    public void setSsaAccessRestrictionsForms(List<SsaAccessRestrictionsForm> ars) {
        if (ars != null) {
            int i = 0;
            for (SsaAccessRestrictionsForm ar : ars) {
                ar.setIdx(i++);
            }
        }
        ssaAccessRestrictionsForms = ars;
    }

    public int hashCode(SsaAccessRestrictionsForm ar) {
        return ar.getId();
    }


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ssasForm", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderColumn(name = "idx")
    @Setter(AccessLevel.NONE)
    private List<SsaFormatTypesForm> ssaFormatTypesForms;

    public void setSsaFormatTypesForms(List<SsaFormatTypesForm> fts) {
        if (fts != null) {
            int i = 0;
            for (SsaFormatTypesForm ft : fts) {
                ft.setIdx(i++);
            }
        }
        ssaFormatTypesForms = fts;
    }

    public int hashCode(SsaFormatTypesForm ft) {
        return ft.getId();
    }


    @OneToMany(fetch = FetchType.EAGER, mappedBy = "ssasForm", cascade = {CascadeType.REMOVE, CascadeType.MERGE})
    @OrderColumn(name = "idx")
    @Setter(AccessLevel.NONE)
    private List<RsasForm> rsasForms;

    public void setRsasForms(List<RsasForm> rsas) {
        if (rsas != null) {
            int i = 0;
            for (RsasForm rsa : rsas) {
                rsa.setIdx(i++);
            }
        }
        rsasForms = rsas;
    }

    public int hashCode(RsasForm rsa) {
        return rsa.getId();
    }

    // Done due to: https://stackoverflow.com/questions/23973347/jpa-java-lang-stackoverflowerror-on-adding-tostring-method-in-entity-classes
    @Override
    public String toString() {
        return "SsasForm{" +
                "id=" + id +
                ", departmenthead='" + departmenthead + '\'' +
                ", recordid='" + recordid + '\'' +
                ", creationdate='" + creationdate + '\'' +
                ", recordstitle='" + recordstitle + '\'' +
                ", effectivedate='" + effectivedate + '\'' +
                ", expirationdate='" + expirationdate + '\'' +
                ", retentionalertdate='" + retentionalertdate + '\'' +
                ", retentionschedule='" + retentionschedule + '\'' +
                ", otherformattypes='" + otherformattypes + '\'' +
                ", recorddescription='" + recorddescription + '\'' +
                ", retentionperiod='" + retentionperiod + '\'' +
                ", descriptionstandards='" + descriptionstandards + '\'' +
                ", createdby='" + createdby + '\'' +
                ", editdate='" + editdate + '\'' +
                ", enabled=" + enabled +
                ", approved=" + approved +
                ", deleted=" + deleted +
                ", IP='" + IP + '\'' +
                ", onlinesubmission=" + onlinesubmission +
                '}';
    }
}

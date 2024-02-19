package tr.gov.icisleri.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "SEC_MUNICIPALITY", schema = "E_BELEDIYE")
public class Municipality extends BaseEntity {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "municipalityoid", nullable = false, precision = 12, scale = 0)
    private Long id;

    @Column(name = "active", nullable = false)
    private boolean active;

//    @NotBlank
//    @Size(min = 8, max = 11)
//    @Pattern(regexp = "\\d{2}(\\.\\d{2}){2,3}", message = "Geçersiz Kurumsal Kod")
    @Column(name = "agencycode", nullable = false, length = 15)
    private String agencyCode;


    @Column(name = "detsisno")
    private Integer detsisNo;

    @Column(name = "edevletkodu")
    private String edevletCode;

    @Column(name = "email", length = 30)
    private String email;

    @Column(name = "fax", length = 10)
    private BigDecimal fax;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "municipality")
//    @OrderBy(clause = "year asc")
    private Set<FinancialPeriod> financialPeriods = new HashSet<>(0);


    @Column(name = "ldapunitkey", length = 32)
    private String ldapUnitKey;


    @Version
    @Column(name = "version", nullable = false)
    private int version;

    public Municipality() {
        super();
    }

    public Municipality(Long municipalityId) {
        super();
        this.id = municipalityId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    public Integer getDetsisNo() {
        return detsisNo;
    }

    public void setDetsisNo(Integer detsisNo) {
        this.detsisNo = detsisNo;
    }

    public String getEdevletCode() {
        return edevletCode;
    }

    public void setEdevletCode(String edevletCode) {
        this.edevletCode = edevletCode;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getFax() {
        return fax;
    }

    public void setFax(BigDecimal fax) {
        this.fax = fax;
    }

    public Set<FinancialPeriod> getFinancialPeriods() {
        return financialPeriods;
    }

    public void setFinancialPeriods(Set<FinancialPeriod> financialPeriods) {
        this.financialPeriods = financialPeriods;
    }

    public String getLdapUnitKey() {
        return ldapUnitKey;
    }

    public void setLdapUnitKey(String ldapUnitKey) {
        this.ldapUnitKey = ldapUnitKey;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}

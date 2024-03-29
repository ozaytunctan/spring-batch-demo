package tr.otunctan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;


@Entity
@Table(name = "GEN_FINANCIAL_PERIOD", schema = "E_BELEDIYE", uniqueConstraints = @UniqueConstraint(columnNames = {"year", "municipalityoid"}))
public class FinancialPeriod extends BaseEntity {


    @Id
    @Column(name = "financialperiodoid", nullable = false, precision = 12)
    private Long id;

    @Version
    @Column(name = "version", nullable = false)
    private int version;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "municipalityoid", nullable = false)
    private Municipality municipality;

    @Column(name = "year", nullable = false, precision = 4)
    private short year;
    @Column(name = "agencycode")
    private String  agencyCode;


    public FinancialPeriod() {
    }

    public FinancialPeriod(short year, Long municipalityId) {
        this.year = year;
        this.municipality = new Municipality(municipalityId);
        this.id=getNewId();
    }

    public static FinancialPeriod of(short year, Long municipalityId) {
        return new FinancialPeriod(year, municipalityId);
    }


    @Transient
    public Long getNewId() {
        if (municipality == null) {
            return null;
        }

        return Long.parseLong(String.valueOf(year) + municipality.getId());
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public Municipality getMunicipality() {
        return municipality;
    }

    public void setMunicipality(Municipality municipality) {
        this.municipality = municipality;
    }

    public short getYear() {
        return year;
    }

    public void setYear(short year) {
        this.year = year;
    }

    public String getAgencyCode() {
        return agencyCode;
    }

    public void setAgencyCode(String agencyCode) {
        this.agencyCode = agencyCode;
    }

    @Override
    public String toString() {
        return "FinancialPeriod{" +
                "id=" + id +
                ", version=" + version +
                ", municipality=" + municipality +
                ", year=" + year +
                ", agencyCode='" + agencyCode + '\'' +
                '}';
    }
}

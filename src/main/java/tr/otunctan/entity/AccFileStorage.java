package tr.otunctan.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;



/**
 * @author ozay.tunctan
 */
@Entity
@Table(name = "acc_file_storage", schema = "E_BELEDIYE")
//        uniqueConstraints = @UniqueConstraint(columnNames = { "municipality_id","financial_period_id","path","type"}))
@SequenceGenerator(name = "file_storage_id_gen", schema = "E_BELEDIYE", allocationSize = 1, sequenceName = "acc_file_storage_seq")
public class AccFileStorage extends Storage  {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "file_storage_id_gen")
    private Long id;


    /**
     * Mongo storage id
     */
    @Column(name = "content_doc_id", unique = true)
    private String contentDocId;

    /**
     * File ek tipi
     */
    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private FileStorageType type = FileStorageType.SLIP_DETAIL_ADDENDA;

    /**
     *Dosyayı kaydeden kullanıcı
     */
    @Column(name = "user_id")
    private Long userId;

    /**
     * Finansal Dönem
     */
    @ManyToOne
    @JoinColumn(name = "financial_period_id")
    private FinancialPeriod financialPeriod;



    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setContentDocId(String contentDocId) {
        this.contentDocId = contentDocId;
    }

    public void setType(FileStorageType type) {
        this.type = type;
    }


    public void setFinancialPeriod(FinancialPeriod financialPeriod) {
        this.financialPeriod = financialPeriod;
    }

    public String getContentDocId() {
        return contentDocId;
    }

    public FileStorageType getType() {
        return type;
    }

    public FinancialPeriod getFinancialPeriod() {
        return financialPeriod;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "AccFileStorage{" +
                "id=" + id +
                ", contentDocId='" + contentDocId + '\'' +
                ", type=" + type +
                ", financialPeriod=" + financialPeriod +
                '}';
    }
}
package tr.otunctan.mongo.entity;

import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection="dosyalar")
public class FileStorageData implements Serializable {

    @Id
    @Field(name = "_id",targetType = FieldType.OBJECT_ID)
    private String id;

    private Long belediyeId;

    private String modul;

    private String dataSinifi;

    private LocalDateTime islemZamani=LocalDateTime.now();
    private String islemYapanTc;
    private String mimeType;
    private String realPath;
    private String dosyaAdi;
    @Field(name = "icerik",targetType = FieldType.BINARY)
    private Binary icerik;
    private String objeIcerik;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getBelediyeId() {
        return belediyeId;
    }

    public void setBelediyeId(Long belediyeId) {
        this.belediyeId = belediyeId;
    }

    public String getModul() {
        return modul;
    }

    public void setModul(String modul) {
        this.modul = modul;
    }

    public String getDataSinifi() {
        return dataSinifi;
    }

    public void setDataSinifi(String dataSinifi) {
        this.dataSinifi = dataSinifi;
    }

    public LocalDateTime getIslemZamani() {
        return islemZamani;
    }

    public void setIslemZamani(LocalDateTime islemZamani) {
        this.islemZamani = islemZamani;
    }

    public String getIslemYapanTc() {
        return islemYapanTc;
    }

    public void setIslemYapanTc(String islemYapanTc) {
        this.islemYapanTc = islemYapanTc;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getRealPath() {
        return realPath;
    }

    public void setRealPath(String realPath) {
        this.realPath = realPath;
    }

    public String getDosyaAdi() {
        return dosyaAdi;
    }

    public void setDosyaAdi(String dosyaAdi) {
        this.dosyaAdi = dosyaAdi;
    }

    public Binary getIcerik() {
        return icerik;
    }

    public void setIcerik(Binary icerik) {
        this.icerik = icerik;
    }

    public String getObjeIcerik() {
        return objeIcerik;
    }

    public void setObjeIcerik(String objeIcerik) {
        this.objeIcerik = objeIcerik;
    }
}

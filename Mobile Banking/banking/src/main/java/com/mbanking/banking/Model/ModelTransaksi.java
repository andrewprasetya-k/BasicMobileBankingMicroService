package com.mbanking.banking.Model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public class ModelTransaksi {
    @Id
    private String idMongo;
    private Integer id;
    private String nomorReferensi;
    private BigDecimal nominal;
    private String jenisTransaksi; // contoh: Transfer, TopUp, Pembayaran
    private String keterangan;
    private String waktuTransaksi;
    private String metode; //bi-fast,qris
    private BigDecimal biayaAdmin;
    
    public String getIdMongo() {
        return idMongo;
    }
    public String getNomorReferensi() {
        return nomorReferensi;
    }
    
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public void setNomorReferensi(String nomorReferensi) {
        this.nomorReferensi = nomorReferensi;
    }
    public BigDecimal getNominal() {
        return nominal;
    }
    public void setNominal(BigDecimal nominal) {
        this.nominal = nominal;
    }
    public String getJenisTransaksi() {
        return jenisTransaksi;
    }
    public void setJenisTransaksi(String jenisTransaksi) {
        this.jenisTransaksi = jenisTransaksi;
    }
    public String getKeterangan() {
        return keterangan;
    }
    public void setKeterangan(String keterangan) {
        this.keterangan = keterangan;
    }
    public String getWaktuTransaksi() {
        return waktuTransaksi;
    }
    public void setWaktuTransaksi(String waktuTransaksi) {
        this.waktuTransaksi = waktuTransaksi;
    }
    public String getMetode() {
        return metode;
    }
    public void setMetode(String metode) {
        this.metode = metode;
    }
    public BigDecimal getBiayaAdmin() {
        return biayaAdmin;
    }
    public void setBiayaAdmin(BigDecimal biayaAdmin) {
        this.biayaAdmin = biayaAdmin;
    }
    
    
}

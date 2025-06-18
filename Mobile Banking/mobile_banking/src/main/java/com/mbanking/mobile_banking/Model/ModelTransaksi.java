package com.mbanking.mobile_banking.Model;

import java.math.BigDecimal;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class ModelTransaksi {
    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Integer id;
    private String nomorReferensi;
    private BigDecimal nominal;
    private String jenisTransaksi; // contoh: Transfer, TopUp, Pembayaran
    private String keterangan;
    private String waktuTransaksi;
    private String metode;
    private BigDecimal biayaAdmin;

    
    public Integer getId(){
        return id;
    }
    public void setId(Integer id){
        this.id = id;
    }
    public String getNomorReferensi() {
        return nomorReferensi;
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

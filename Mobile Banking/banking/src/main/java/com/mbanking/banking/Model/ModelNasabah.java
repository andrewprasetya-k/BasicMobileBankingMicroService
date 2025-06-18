package com.mbanking.banking.Model;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;

public class ModelNasabah {
    @Id
    private String idMongo;
    private Integer id;
    private String noRekening;
    private String nama;
    private String alamat;
    private String jenisKelamin;
    private BigDecimal saldo;

    public String getIdMongo() {
        return idMongo;
    }
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }
    public String getNoRekening() {
        return noRekening;
    }
    public void setNoRekening(String noRekening) {
        this.noRekening = noRekening;
    }
    public String getNama() {
        return nama;
    }
    public void setNama(String nama) {
        this.nama = nama;
    }
    public String getAlamat() {
        return alamat;
    }
    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }
    public String getJenisKelamin() {
        return jenisKelamin;
    }
    public void setJenisKelamin(String jenisKelamin) {
        this.jenisKelamin = jenisKelamin;
    }
    public BigDecimal getSaldo() {
        return saldo;
    }
    public void setSaldo(BigDecimal saldo) {
        this.saldo = saldo;
    }

}

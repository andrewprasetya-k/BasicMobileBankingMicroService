package com.mbankingpostgre.mbankingpostgre.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mbankingpostgre.mbankingpostgre.Model.ModelNasabah;

public interface RepositoryNasabah extends CrudRepository<ModelNasabah, Integer> {
    //query udh ada bawaan, ga usah dibuat di sini

    //get by nama
    @Query("select n from ModelNasabah n where n.nama LIKE %?1%")
    public List<ModelNasabah> findByName(String nama);

    //lihat rata-rata saldo
    @Query("select avg(saldo) from ModelNasabah")
    public BigDecimal findAvgSaldo();

    //mencari nasabah dengan saldo tertinggi
    @Query("select n from ModelNasabah n where n.saldo >= (select avg(n.saldo) from ModelNasabah n) order by n.saldo desc")
    public List<ModelNasabah> findNasabahTajir();
    
    //mencari nasabah dengan saldo dalam range max < x < min
    @Query("select n from ModelNasabah n where (n.saldo >= :min and n.saldo <= :max)order by n.saldo desc")
    public List<ModelNasabah> findSaldoNasabahRange(BigDecimal min, BigDecimal max);

}

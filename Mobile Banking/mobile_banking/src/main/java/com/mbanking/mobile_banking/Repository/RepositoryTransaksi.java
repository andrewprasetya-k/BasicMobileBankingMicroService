package com.mbanking.mobile_banking.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.mbanking.mobile_banking.Model.ModelTransaksi;

public interface RepositoryTransaksi extends CrudRepository<ModelTransaksi, Integer> {
    //query udh ada bawaan, ga usah dibuat di sini

    //get by nomor referensi
    @Query("select n from ModelTransaksi n where n.nomorReferensi = ?1")
    public List<ModelTransaksi> findByNoReferensi(String nomorReferensi);
    
    //mengurutkan transfer dari nominal yg paling tinggi
    @Query("select n from ModelTransaksi n order by n.nominal desc")
    public List<ModelTransaksi> sortTransaksiDesc();
    
    //mencari transaksi dengan nominal dalam range max < x < min
    @Query("select n from ModelTransaksi n where (n.nominal >= :min and n.nominal <= :max)order by n.nominal desc")
    public List<ModelTransaksi> findSaldoNasabahRange(BigDecimal min, BigDecimal max);
    
    //mencari transaksi dengan metode transaksi tertentu
    @Query("select n from ModelTransaksi n where n.metode LIKE %?1%")
    public List<ModelTransaksi> findByMetode(String nama);

}

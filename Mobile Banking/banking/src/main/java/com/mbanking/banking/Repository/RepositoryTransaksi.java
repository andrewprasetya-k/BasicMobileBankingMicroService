package com.mbanking.banking.Repository;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mbanking.banking.Model.ModelTransaksi;

@Repository
public interface RepositoryTransaksi extends MongoRepository<ModelTransaksi, String> {
    List<ModelTransaksi> getById(Integer id);
    boolean existsById(Integer id);
    void deleteById(Integer id);
    // Find by nomor referensi using regex for partial matching
    @Query("{'nomorReferensi': {$regex: ?0, $options: 'i'}}")
    List<ModelTransaksi> findByNoReferensi(String nama);

    // Sort transactions by nominal descending
    @Query(value = "{}", sort = "{'nominal': -1}")
    List<ModelTransaksi> sortTransaksiDesc();

    // Find transactions within amount range
    @Query("{'nominal': {$gte: ?0, $lte: ?1}}")
    List<ModelTransaksi> findSaldoNasabahRange(BigDecimal min, BigDecimal max);

    // Find by payment method using regex for partial matching
    @Query("{'metode': {$regex: ?0, $options: 'i'}}")
    List<ModelTransaksi> findByMetodeIgnoreCase(String nama);

    // Find by transaction type
    @Query("{'jenisTransaksi': {$regex: ?0, $options: 'i'}}")
    List<ModelTransaksi> findByJenisTransaksi(String jenisTransaksi);

    // Find transactions within date range
    @Query("{'waktuTransaksi': {$gte: ?0, $lte: ?1}}")
    List<ModelTransaksi> findByWaktuTransaksiBetween(java.time.LocalDateTime start, java.time.LocalDateTime end);
}
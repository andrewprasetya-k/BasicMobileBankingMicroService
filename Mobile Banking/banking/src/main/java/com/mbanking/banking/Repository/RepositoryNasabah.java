package com.mbanking.banking.Repository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import com.mbanking.banking.Model.ModelNasabah;

@Repository
public interface RepositoryNasabah extends MongoRepository<ModelNasabah, String> {
    // Basic CRUD operations - fixed ID type consistency
    List<ModelNasabah> getById(Integer id);
    boolean existsById(Integer id);
    void deleteById(Integer id);
    // Remove Boolean findById(Integer id) - this causes the serialization error

    // Lihat rata-rata saldo menggunakan aggregation
    @Aggregation(pipeline = {
        "{ '$group': { '_id': null, 'avgSaldo': { '$avg': '$saldo' } } }",
        "{ '$project': { '_id': 0, 'avgSaldo': 1 } }"
    })
    List<AvgSaldoResult> findAvgSaldoAggregation();
    
    // Alternative method untuk rata-rata saldo - fixed deprecated ROUND_HALF_UP
    default BigDecimal findAvgSaldo() {
        List<ModelNasabah> allNasabah = findAll();
        if (allNasabah.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal total = allNasabah.stream()
            .map(ModelNasabah::getSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        return total.divide(BigDecimal.valueOf(allNasabah.size()), 2, RoundingMode.HALF_UP);
    }
    
    // Mencari nasabah dengan saldo >= rata-rata, diurutkan desc
    @Query(value = "{ 'saldo': { $gte: ?0 } }", sort = "{ 'saldo': -1 }")
    List<ModelNasabah> findNasabahWithSaldoAboveAverage(BigDecimal avgSaldo);
    
    // Method untuk mencari nasabah tajir (kombinasi query)
    default List<ModelNasabah> findNasabahTajir() {
        BigDecimal avgSaldo = findAvgSaldo();
        return findNasabahWithSaldoAboveAverage(avgSaldo);
    }
    
    // Alternative implementation using streams (more flexible)
    default List<ModelNasabah> findNasabahTajirStream() {
        BigDecimal avgSaldo = findAvgSaldo();
        return findAll().stream()
            .filter(nasabah -> nasabah.getSaldo().compareTo(avgSaldo) > 0)
            .sorted((n1, n2) -> n2.getSaldo().compareTo(n1.getSaldo())) // Sort desc by saldo
            .collect(Collectors.toList());
    }
    
    // Mencari nasabah dengan saldo dalam range min <= saldo <= max
    @Query("{'saldo': {$gte: ?0, $lte: ?1}}")
    List<ModelNasabah> findSaldoNasabahRange(BigDecimal min, BigDecimal max);
    
    // Method tambahan untuk compatibility dengan Spring Data MongoDB
    List<ModelNasabah> findByNamaContainingIgnoreCase(String nama);
    List<ModelNasabah> findBySaldoBetween(BigDecimal min, BigDecimal max);
    List<ModelNasabah> findBySaldoGreaterThanEqual(BigDecimal saldo);
    List<ModelNasabah> findBySaldoGreaterThan(BigDecimal saldo);
    List<ModelNasabah> findAllByOrderBySaldoDesc();
    
    // Additional useful queries for nasabah analysis
    List<ModelNasabah> findByJenisKelamin(String jenisKelamin);
    List<ModelNasabah> findByAlamatContainingIgnoreCase(String alamat);
    
    // Count methods
    long countBySaldoGreaterThan(BigDecimal saldo);
    long countBySaldoLessThan(BigDecimal saldo);
    
    // Top N richest customers
    default List<ModelNasabah> findTopNRichestNasabah(int limit) {
        return findAllByOrderBySaldoDesc().stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    // Nasabah with lowest saldo
    default List<ModelNasabah> findPoorestNasabah(int limit) {
        return findAll().stream()
            .sorted((n1, n2) -> n1.getSaldo().compareTo(n2.getSaldo()))
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    // Statistical methods
    default BigDecimal getTotalSaldo() {
        return findAll().stream()
            .map(ModelNasabah::getSaldo)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    
    default long countNasabahAboveAverage() {
        BigDecimal avgSaldo = findAvgSaldo();
        return findAll().stream()
            .filter(nasabah -> nasabah.getSaldo().compareTo(avgSaldo) > 0)
            .count();
    }
    
    default long countNasabahBelowAverage() {
        BigDecimal avgSaldo = findAvgSaldo();
        return findAll().stream()
            .filter(nasabah -> nasabah.getSaldo().compareTo(avgSaldo) < 0)
            .count();
    }
    
    // Inner class untuk aggregation result
    public static interface AvgSaldoResult {
        BigDecimal getAvgSaldo();
    }
}
package com.mbanking.mobile_banking.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mbanking.mobile_banking.Model.ModelNasabah;
import com.mbanking.mobile_banking.Repository.RepositoryNasabah;

@Controller
@RequestMapping(path = "/nasabah")
public class ControllerNasabah {
    @Autowired
    private RepositoryNasabah rmb;
    
    @GetMapping
    @ResponseBody
    public Map<String, Object> getAllNasabah() {
        Map<String, Object> response = new HashMap<>();
        try {
            List<ModelNasabah> nasabahList = new ArrayList<>();
            rmb.findAll().forEach(nasabahList::add);

            response.put("status", true);
            response.put("message", "Sukses mengambil data nasabah");
            response.put("data", nasabahList);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Error mengambil data nasabah" + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    // lewat body
    @PostMapping
    public @ResponseBody Map<String, Object> addNasabah(@RequestBody ModelNasabah mn){
        Map<String, Object> response = new HashMap<>();
        try {
            // Validasi: saldo tidak boleh negatif
            if (mn.getSaldo() != null && mn.getSaldo().compareTo(BigDecimal.ZERO) < 0) {
                response.put("status", false);
                response.put("message", "Saldo tidak boleh negatif");
                return response;
            }
            // Validasi: nama tidak boleh kosong
            if (mn.getNama() == null || mn.getNama().trim().isEmpty()) {
                response.put("status", false);
                response.put("message", "Nama tidak boleh kosong");
                return response;
            }
            // Validasi: no rekening tidak boleh kosong
            if (mn.getNoRekening() == null || mn.getNoRekening().trim().isEmpty()) {
                response.put("status", false);
                response.put("message", "No rekening tidak boleh kosong");
                return response;
            }
            rmb.save(mn);
            response.put("status", true);
            response.put("message", "Nasabah berhasil ditambahkan");
            response.put("data", mn);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    // Delete nasabah by ID with detailed response
    @DeleteMapping("/delete")
    public @ResponseBody Map<String, Object> deleteById(@RequestParam int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (rmb.existsById(id)) {
                rmb.deleteById(id);
                response.put("status", true);
                response.put("message", "Nasabah dengan ID " + id + " berhasil dihapus");
            } else {
                response.put("status", false);
                response.put("message", "Nasabah dengan ID " + id + " tidak ditemukan");
            }
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }
        return response;
    }

    //UPDATE MENGGUNAKAN PATH VARIABEL DAN REQUEST BODY
    @PutMapping("/update/{id}")
    public @ResponseBody boolean update(@PathVariable("id") int id, @RequestBody ModelNasabah mk){
        try {
            if(rmb.existsById(id)){
                @SuppressWarnings("null")
                ModelNasabah mb2 = rmb.findById(id).get();
                // Update all fields from the request body
                mb2.setNama(mk.getNama());
                mb2.setNoRekening(mk.getNoRekening());
                mb2.setAlamat(mk.getAlamat());
                mb2.setJenisKelamin(mk.getJenisKelamin());
                mb2.setSaldo(mk.getSaldo());
                rmb.save(mb2);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    // Get nama nasabah
    @GetMapping("/getbynama")
    public @ResponseBody Map<String, Object> getByNama(@RequestParam String nama) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (nama == null || nama.trim().isEmpty()) {
            response.put("status", false);
            response.put("message", "Nama tidak boleh kosong");
            response.put("data", null);
            return response;
            }
            // Case-insensitive search
            List<ModelNasabah> result = rmb.findByNameIgnoreCase(nama.trim());
            if (result == null || result.isEmpty()) {
            response.put("status", false);
            response.put("message", "Nasabah dengan nama tersebut tidak ditemukan");
            response.put("data", null);
            } else {
            response.put("status", true);
            response.put("message", "Sukses mengambil data nasabah");
            response.put("data", result);
            }
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    //cari rata-rata saldo
    @GetMapping("/avgsaldo")
    public @ResponseBody Map<String, Object> findAvgSaldo() {
        Map<String, Object> response = new HashMap<>();
        try {
            BigDecimal avgSaldo = rmb.findAvgSaldo();
            response.put("status", true);
            response.put("message", "Berhasil mengambil rata-rata saldo");
            response.put("Saldo", avgSaldo);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }
    
    //cari nasabah tajir
    @GetMapping("/saldobesar")
    public @ResponseBody Object findNasabahTajir() {
        Map<String, Object> obj = new HashMap<>();
        try {
            Iterable<ModelNasabah> nasabahIterable = rmb.findAll();
            
            List<ModelNasabah> nasabahList = new ArrayList<>();
            nasabahIterable.forEach(nasabahList::add);

            obj.put("data", nasabahList);
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error retrieving data: " + e.getMessage());
            obj.put("data", null);
        }
        return obj;
    }
    
    //cari nasabah dengan range saldo m < x < n
    @GetMapping("/saldorange")
    public @ResponseBody Map<String, Object> findSaldoNasabahRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validasi: min dan max tidak boleh negatif
            if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
                response.put("status", false);
                response.put("message", "Nilai saldo tidak boleh negatif");
                response.put("data", null);
                return response;
            }
            // Validasi: min harus lebih kecil dari max
            if (min.compareTo(max) >= 0) {
                response.put("status", false);
                response.put("message", "Nilai minimum harus lebih kecil dari maksimum");
                response.put("data", null);
                return response;
            }
            Iterable<ModelNasabah> nasabahIterable = rmb.findSaldoNasabahRange(min, max);
            List<ModelNasabah> nasabahList = new ArrayList<>();
            nasabahIterable.forEach(nasabahList::add);

            response.put("status", true);
            response.put("message", "Berhasil mengambil data nasabah dalam range saldo");
            response.put("data", nasabahList);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Error retrieving data: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

} 
package com.mbankingpostgre.mbankingpostgre.Controller;

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

import com.mbankingpostgre.mbankingpostgre.Model.ModelNasabah;
import com.mbankingpostgre.mbankingpostgre.Repository.RepositoryNasabah;

@Controller
@RequestMapping(path = "/nasabah")
public class ControllerNasabah {
    @Autowired
    private RepositoryNasabah rmb;
    
    @GetMapping
    @ResponseBody
    public Object getAllNasabah(){
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

    //lewat body
    @PostMapping
    public @ResponseBody Map<String, Object> addNasabah(@RequestBody ModelNasabah mn){
        Map<String, Object> response = new HashMap<>();
        try {
            if (mn.getNama() == null || mn.getNama().trim().isEmpty() ||
                mn.getNoRekening() == null || mn.getNoRekening().trim().isEmpty() ||
                mn.getAlamat() == null || mn.getAlamat().trim().isEmpty() ||
                mn.getJenisKelamin() == null || mn.getJenisKelamin().trim().isEmpty() ||
                mn.getSaldo() == null) {
                response.put("status", false);
                response.put("message", "Semua field wajib diisi dan tidak boleh kosong.");
                return response;
            }
            rmb.save(mn);
            response.put("status", true);
            response.put("message", "Nasabah berhasil ditambahkan.");
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }
        return response;
    }

    //delete
    @DeleteMapping("/delete")
    public @ResponseBody Map<String, Object> deleteById(@RequestParam int id){
        Map<String, Object> response = new HashMap<>();
        try {
            if(rmb.existsById(id)){
                rmb.deleteById(id);
                response.put("status", true);
                response.put("message", "Nasabah dengan id " + id + " berhasil dihapus.");
            } else {
                response.put("status", false);
                response.put("message", "Nasabah dengan id " + id + " tidak ditemukan.");
            }
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }
        return response;
    }

    //UPDATE MENGGUNAKAN PATH VARIABEL DAN REQUEST BODY
    @PutMapping("/update/{id}")
    public @ResponseBody Map<String, Object> update(@PathVariable("id") int id, @RequestBody ModelNasabah mk){
        Map<String, Object> response = new HashMap<>();
        try {
            if (!rmb.existsById(id)) {
                response.put("status", false);
                response.put("message", "Nasabah dengan id " + id + " tidak ditemukan.");
                return response;
            }
            if (mk.getNama() == null || mk.getNama().trim().isEmpty() ||
                mk.getNoRekening() == null || mk.getNoRekening().trim().isEmpty() ||
                mk.getAlamat() == null || mk.getAlamat().trim().isEmpty() ||
                mk.getJenisKelamin() == null || mk.getJenisKelamin().trim().isEmpty() ||
                mk.getSaldo() == null) {
                response.put("status", false);
                response.put("message", "Semua field wajib diisi dan tidak boleh kosong.");
                return response;
            }
            ModelNasabah mb2 = rmb.findById(id).get();
            mb2.setNama(mk.getNama());
            mb2.setNoRekening(mk.getNoRekening());
            mb2.setAlamat(mk.getAlamat());
            mb2.setJenisKelamin(mk.getJenisKelamin());
            mb2.setSaldo(mk.getSaldo());
            rmb.save(mb2);
            response.put("status", true);
            response.put("message", "Nasabah berhasil diupdate.");
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }
        return response;
    }

    //Get by Nama--URL
    @GetMapping("/getbynama")
    public @ResponseBody Object getByNama(@RequestParam String nama){
        Map<String, Object> response = new HashMap<>();
        try {
            Iterable<ModelNasabah> result = rmb.findByName(nama);
            List<ModelNasabah> nasabahList = new ArrayList<>();
            result.forEach(nasabahList::add);

            if (nasabahList.isEmpty()) {
            response.put("status", false);
            response.put("message", "Nasabah dengan nama '" + nama + "' tidak ditemukan.");
            response.put("data", null);
            } else {
            response.put("status", true);
            response.put("data", nasabahList);
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
    public @ResponseBody Object findAvgSaldo() {
        Map<String, Object> response = new HashMap<>();
        try {
            BigDecimal avgSaldo = rmb.findAvgSaldo();
            response.put("status", true);
            response.put("avgSaldo", avgSaldo);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
            response.put("avgSaldo", null);
        }
        return response;
    }
    
    //cari nasabah tajir
    @GetMapping("/saldobesar")
    public @ResponseBody Object findNasabahTajir() {
        Map<String, Object> obj = new HashMap<>();
        try {
            Iterable<ModelNasabah> nasabahIterable = rmb.findNasabahTajir();

            List<ModelNasabah> nasabahList = new ArrayList<>();
            nasabahIterable.forEach(nasabahList::add);

            if (nasabahList.isEmpty()) {
            obj.put("status", false);
            obj.put("message", "Tidak ada nasabah dengan saldo di atas saldo rata-rata.");
            obj.put("data", null);
            } else {
            obj.put("status", true);
            obj.put("data", nasabahList);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error retrieving data: " + e.getMessage());
            obj.put("data", null);
        }
        return obj;
    }
    
    //cari nasabah dengan range saldo m < x < n
    @GetMapping("/saldorange")
    public @ResponseBody Object findSaldoNasabahRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Map<String, Object> obj = new HashMap<>();
        try {
            if (min == null || max == null) {
            obj.put("status", false);
            obj.put("message", "Parameter saldo minimum dan maksimum tidak boleh kosong.");
            obj.put("data", null);
            return obj;
            }
            if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
            obj.put("status", false);
            obj.put("message", "Nilai saldo minimum dan maksimum tidak boleh kurang dari 0.");
            obj.put("data", null);
            return obj;
            }
            Iterable<ModelNasabah> nasabahIterable = rmb.findSaldoNasabahRange(min, max);

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

}
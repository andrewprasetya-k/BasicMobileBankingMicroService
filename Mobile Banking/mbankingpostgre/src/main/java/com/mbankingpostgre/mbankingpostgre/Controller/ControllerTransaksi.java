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

import com.mbankingpostgre.mbankingpostgre.Model.ModelTransaksi;
import com.mbankingpostgre.mbankingpostgre.Repository.RepositoryTransaksi;

@Controller
@RequestMapping(path = "/transaksi")
public class ControllerTransaksi {
    @Autowired
    private RepositoryTransaksi rpt;
    
    @GetMapping
    @ResponseBody
    public Object getAllTransaksi(){
        Map<String, Object> obj = new HashMap<>();
        try {
            Iterable<ModelTransaksi> transaksiIterable = rpt.findAll();
            
            List<ModelTransaksi> transaksiList = new ArrayList<>();
            transaksiIterable.forEach(transaksiList::add);

            obj.put("data", transaksiList);
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error retrieving data: " + e.getMessage());
            obj.put("data", null);
        }
        return obj;
    }

    //lewat body
    @PostMapping
    public @ResponseBody boolean addTransaksi(@RequestBody ModelTransaksi mt){
        // Validasi field kosong dan saldo negatif
        if (mt.getNomorReferensi() == null || mt.getNomorReferensi().trim().isEmpty() ||
            mt.getJenisTransaksi() == null || mt.getJenisTransaksi().trim().isEmpty() ||
            mt.getKeterangan() == null || mt.getKeterangan().trim().isEmpty() ||
            mt.getWaktuTransaksi() == null ||
            mt.getMetode() == null || mt.getMetode().trim().isEmpty() ||
            mt.getNominal() == null || mt.getBiayaAdmin() == null) {
            return false;
        }
        if (mt.getNominal().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        rpt.save(mt);
        return true;
    }

    //delete
    @DeleteMapping("/delete")
    public @ResponseBody Map<String, Object> deleteById(@RequestParam int id){
        Map<String, Object> response = new HashMap<>();
        try {
            if(rpt.existsById(id)){
                rpt.deleteById(id);
                response.put("status", true);
                response.put("message", "Transaksi berhasil dihapus.");
            } else {
                response.put("status", false);
                response.put("message", "ID transaksi tidak ditemukan.");
            }
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan: " + e.getMessage());
        }
        return response;
    }

    //UPDATE MENGGUNAKAN PATH VARIABEL DAN REQUEST BODY
    @PutMapping("/update/{id}")
    public @ResponseBody boolean update(@PathVariable("id") int id, @RequestBody ModelTransaksi mt) {
        // Validasi field kosong dan saldo negatif
        if (mt.getNomorReferensi() == null || mt.getNomorReferensi().trim().isEmpty() ||
            mt.getJenisTransaksi() == null || mt.getJenisTransaksi().trim().isEmpty() ||
            mt.getKeterangan() == null || mt.getKeterangan().trim().isEmpty() ||
            mt.getWaktuTransaksi() == null ||
            mt.getMetode() == null || mt.getMetode().trim().isEmpty() ||
            mt.getNominal() == null || mt.getBiayaAdmin() == null) {
            return false;
        }
        if (mt.getNominal().compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        try {
            if (rpt.existsById(id)) {
                ModelTransaksi mt1 = rpt.findById(id).get();
                mt1.setNomorReferensi(mt.getNomorReferensi());
                mt1.setNominal(mt.getNominal());
                mt1.setJenisTransaksi(mt.getJenisTransaksi());
                mt1.setKeterangan(mt.getKeterangan());
                mt1.setWaktuTransaksi(mt.getWaktuTransaksi());
                mt1.setMetode(mt.getMetode());
                mt1.setBiayaAdmin(mt.getBiayaAdmin());
                
                rpt.save(mt1);
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

    @GetMapping("/search/reference")
    @ResponseBody
    public Object findByNoReferensi(@RequestParam String nama) {
        Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelTransaksi> transaksiList = rpt.findByNoReferensi(nama);
            
            if (!transaksiList.isEmpty()) {
                obj.put("status", true);
                obj.put("message", "Transactions found");
                obj.put("data", transaksiList);
            } else {
                obj.put("status", false);
                obj.put("message", "No transactions found with reference number containing: " + nama);
                obj.put("data", new ArrayList<>());
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error searching transactions: " + e.getMessage());
            obj.put("data", null);
        }
        return obj;
    }
    
    @GetMapping("/sort/nominal")
    @ResponseBody
    public Object getSortedTransaksi() {
        Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelTransaksi> transaksiList = rpt.sortTransaksiDesc();
            
            obj.put("status", true);
            obj.put("message", "Transactions sorted by amount (highest first)");
            obj.put("data", transaksiList);
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error sorting transactions: " + e.getMessage());
            obj.put("data", null);
        }
        return obj;
    }

    // Validasi agar min dan max tidak boleh negatif
    @GetMapping("/search/rangenominal")
    @ResponseBody
    public Object findByAmountRangeValidated(@RequestParam BigDecimal min,@RequestParam BigDecimal max) {
        Map<String, Object> obj = new HashMap<>();
        if (min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
            obj.put("status", false);
            obj.put("message", "Nilai minimum dan maksimum tidak boleh negatif.");
            obj.put("data", new ArrayList<>());
            return obj;
        }
        try {
            List<ModelTransaksi> transaksiList = rpt.findSaldoNasabahRange(min, max);

            if (!transaksiList.isEmpty()) {
                obj.put("status", true);
                obj.put("message", "Transactions found in amount range");
                obj.put("data", transaksiList);
            } else {
                obj.put("status", false);
                obj.put("message", "No transactions found in the amount range");
                obj.put("data", new ArrayList<>());
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error searching transactions by amount range: " + e.getMessage());
            obj.put("data", null);
        }
        return obj;
    }
    @GetMapping("/search/method")
    @ResponseBody
    public Object findByMetode(@RequestParam String nama) {
        Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelTransaksi> transaksiList = rpt.findByMetode(nama);
            
            if (!transaksiList.isEmpty()) {
                obj.put("status", true);
                obj.put("message", "Transactions found with payment method");
                obj.put("data", transaksiList);
            } else {
                obj.put("status", false);
                obj.put("message", "No transactions found with payment method containing: " + nama);
                obj.put("data", new ArrayList<>());
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error searching transactions by payment method: " + e.getMessage());
            obj.put("data", null);
        }
        return obj;
    }

}

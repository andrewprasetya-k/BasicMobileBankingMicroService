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

import com.mbanking.mobile_banking.Model.ModelTransaksi;
import com.mbanking.mobile_banking.Repository.RepositoryTransaksi;

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

    // Tambah transaksi dengan validasi dan penanganan error
    @PostMapping
    @ResponseBody
    public Object addTransaksi(@RequestBody ModelTransaksi mt) {
        Map<String, Object> response = new HashMap<>();
        try {
            // Validasi: nominal tidak boleh negatif atau null
            if (mt.getNominal() == null || mt.getNominal().compareTo(BigDecimal.ZERO) < 0) {
                response.put("status", false);
                response.put("message", "Nominal tidak boleh negatif atau kosong.");
                return response;
            }
            // Validasi: keterangan tidak boleh kosong
            if (mt.getKeterangan() == null || mt.getKeterangan().trim().isEmpty()) {
                response.put("status", false);
                response.put("message", "Keterangan tidak boleh kosong.");
                return response;
            }
            // Validasi: metode tidak boleh kosong
            if (mt.getMetode() == null || mt.getMetode().trim().isEmpty()) {
                response.put("status", false);
                response.put("message", "Metode tidak boleh kosong.");
                return response;
            }
            // Validasi: nomor referensi tidak boleh kosong
            if (mt.getNomorReferensi() == null || mt.getNomorReferensi().trim().isEmpty()) {
                response.put("status", false);
                response.put("message", "Nomor Referensi tidak boleh kosong.");
                return response;
            }
            // Simpan ke database
            rpt.save(mt);
            response.put("status", true);
            response.put("message", "Transaksi berhasil ditambahkan.");
            response.put("data", mt);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan saat menambah transaksi: " + e.getMessage());
            response.put("data", null);
        }
        return response;
    }

    //delete
    @DeleteMapping("/delete")
    @ResponseBody
    public Object deleteById(@RequestParam int id) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (rpt.existsById(id)) {
                rpt.deleteById(id);
                response.put("status", true);
                response.put("message", "Transaksi berhasil dihapus.");
            } else {
                response.put("status", false);
                response.put("message", "ID transaksi tidak ditemukan.");
            }
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan saat menghapus transaksi: " + e.getMessage());
        }
        return response;
    }

    //UPDATE MENGGUNAKAN PATH VARIABEL DAN REQUEST BODY
    @PutMapping("/update/{id}")
    @ResponseBody
    public Object update(@PathVariable("id") int id, @RequestBody ModelTransaksi mt) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!rpt.existsById(id)) {
                response.put("status", false);
                response.put("message", "ID transaksi tidak ditemukan.");
                return response;
            }
            ModelTransaksi mt1 = rpt.findById(id).orElse(null);
            if (mt1 == null) {
                response.put("status", false);
                response.put("message", "Data transaksi tidak ditemukan.");
                return response;
            }
            mt1.setNomorReferensi(mt.getNomorReferensi());
            mt1.setNominal(mt.getNominal());
            mt1.setJenisTransaksi(mt.getJenisTransaksi());
            mt1.setKeterangan(mt.getKeterangan());
            mt1.setWaktuTransaksi(mt.getWaktuTransaksi());
            mt1.setMetode(mt.getMetode());
            mt1.setBiayaAdmin(mt.getBiayaAdmin());

            rpt.save(mt1);
            response.put("status", true);
            response.put("message", "Transaksi berhasil diupdate.");
            response.put("data", mt1);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Terjadi kesalahan saat update transaksi: " + e.getMessage());
            response.put("data", null);
        }
        return response;
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

    @GetMapping("/search/rangenominal")
    @ResponseBody
    public Object findByAmountRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Map<String, Object> obj = new HashMap<>();
        // Validasi: range nominal tidak boleh negatif
        if (min == null || max == null || min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
            obj.put("status", false);
            obj.put("message", "Range nominal tidak boleh negatif atau kosong.");
            obj.put("data", null);
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
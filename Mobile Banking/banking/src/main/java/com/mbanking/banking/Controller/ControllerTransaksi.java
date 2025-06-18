package com.mbanking.banking.Controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

import com.mbanking.banking.Model.ModelTransaksi;
import com.mbanking.banking.Repository.RepositoryTransaksi;

@Controller
@RequestMapping("/transaksi")
public class ControllerTransaksi {
    
    @Autowired
    private RepositoryTransaksi rpt;
    
    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllTransaksi(){
        Map<String, Object> response = new HashMap<>();
        try {
            Iterable<ModelTransaksi> nasabahIterable = rpt.findAll();
            List<ModelTransaksi> nasabahList = new ArrayList<>();
            nasabahIterable.forEach(nasabahList::add);
            response.put("data", nasabahList);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", false);
            response.put("message", "Error retrieving data: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @PostMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> addTransaksi(@RequestBody ModelTransaksi mt){
        Map<String, Object> obj = new HashMap<>();
        // Proteksi: cek field text wajib tidak kosong/null
        if (mt.getNomorReferensi() == null || mt.getNomorReferensi().trim().isEmpty() ||
            mt.getJenisTransaksi() == null || mt.getJenisTransaksi().trim().isEmpty() ||
            mt.getKeterangan() == null || mt.getKeterangan().trim().isEmpty() ||
            mt.getMetode() == null || mt.getMetode().trim().isEmpty()) {
            obj.put("status", false);
            obj.put("message", "Field nomorReferensi, jenisTransaksi, keterangan, dan metode tidak boleh kosong");
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(obj);
        }
        // Proteksi: nominal tidak boleh negatif/null
        if (mt.getNominal() == null || mt.getNominal().compareTo(BigDecimal.ZERO) < 0) {
            obj.put("status", false);
            obj.put("message", "Nominal/saldo tidak boleh negatif atau kosong");
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(obj);
        }
        try {
            ModelTransaksi savedTransaksi = rpt.save(mt);
            obj.put("status", true);
            obj.put("message", "Transaction added successfully");
            obj.put("data", savedTransaksi);
            return ResponseEntity.status(HttpStatus.CREATED).body(obj);
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error adding transaction: " + e.getMessage());
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    // Alternative delete method using query parameter (keeping your original approach)
    // @DeleteMapping("/delete")
    // @ResponseBody 
    // public ResponseEntity<Map<String, Object>> deleteByIdParam(@RequestParam String id){
    //     return deleteById(id);
    // }
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteById(@RequestParam int id) {
        Map<String, Object> obj = new HashMap<>();
        List<ModelTransaksi> transaksiList = rpt.getById(id);
        try {
            if (!transaksiList.isEmpty()) {
                rpt.deleteById(id);
                obj.put("status", true);
                obj.put("message", "Nasabah deleted successfully");
                return ResponseEntity.ok(obj);
            } else {
                obj.put("status", false);
                obj.put("message", "Nasabah not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error deleting nasabah: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    @PutMapping("/update/{id}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> update(@PathVariable Integer id, @RequestBody ModelTransaksi mt) {
        Map<String, Object> obj = new HashMap<>();
        List<ModelTransaksi> transaksiList = rpt.getById(id);

        // Proteksi: cek field text wajib tidak kosong/null
        if (mt.getNomorReferensi() == null || mt.getNomorReferensi().trim().isEmpty() ||
            mt.getJenisTransaksi() == null || mt.getJenisTransaksi().trim().isEmpty() ||
            mt.getKeterangan() == null || mt.getKeterangan().trim().isEmpty() ||
            mt.getMetode() == null || mt.getMetode().trim().isEmpty()) {
            obj.put("status", false);
            obj.put("message", "Field nomorReferensi, jenisTransaksi, keterangan, dan metode tidak boleh kosong");
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(obj);
        }
        // Proteksi: nominal tidak boleh negatif/null
        if (mt.getNominal() == null || mt.getNominal().compareTo(BigDecimal.ZERO) < 0) {
            obj.put("status", false);
            obj.put("message", "Nominal/saldo tidak boleh negatif atau kosong");
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(obj);
        }

        try {
            if (!transaksiList.isEmpty()) {
                ModelTransaksi mt2 = transaksiList.getFirst();
                // Update fields
                mt2.setNomorReferensi(mt.getNomorReferensi());
                mt2.setNominal(mt.getNominal());
                mt2.setJenisTransaksi(mt.getJenisTransaksi());
                mt2.setKeterangan(mt.getKeterangan());
                mt2.setWaktuTransaksi(mt.getWaktuTransaksi());
                mt2.setMetode(mt.getMetode());
                mt2.setBiayaAdmin(mt.getBiayaAdmin());
                rpt.save(mt2);
                obj.put("status", true);
                obj.put("message", "Nasabah updated successfully");
                obj.put("data", mt2);
                return ResponseEntity.ok(obj);
            } else {
                obj.put("status", false);
                obj.put("message", "Nasabah not found with id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error updating nasabah: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    @GetMapping("/search/reference")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findByNoReferensi(@RequestParam String nama) {
        Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelTransaksi> transaksiList = rpt.findByNoReferensi(nama);
            if (!transaksiList.isEmpty()) {
                obj.put("status", true);
                obj.put("message", "Transactions found");
                obj.put("data", transaksiList);
                obj.put("count", transaksiList.size());
                
                return ResponseEntity.ok(obj);
            } else {
                obj.put("status", false);
                obj.put("message", "No transactions found with reference number containing: " + nama);
                obj.put("data", new ArrayList<>());
                obj.put("count", 0);
                
                return ResponseEntity.ok(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error searching transactions: " + e.getMessage());
            obj.put("data", null);
            obj.put("count", 0);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }
    
    @GetMapping("/sort/nominal")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getSortedTransaksi() {
        Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelTransaksi> transaksiList = rpt.sortTransaksiDesc();
            
            obj.put("status", true);
            obj.put("message", "Transactions sorted by amount (highest first)");
            obj.put("data", transaksiList);
            obj.put("count", transaksiList.size());
            
            return ResponseEntity.ok(obj);
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error sorting transactions: " + e.getMessage());
            obj.put("data", null);
            obj.put("count", 0);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    @GetMapping("/search/rangenominal")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findByAmountRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Map<String, Object> obj = new HashMap<>();
        // Proteksi: min dan max tidak boleh negatif
        try {
            List<ModelTransaksi> transaksiList = rpt.findSaldoNasabahRange(min, max);
            System.out.println("Searching transactions in range: " + min + " to " + max);
            if (min == null || max == null || min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
                obj.put("status", false);
                obj.put("message", "Nilai min dan max tidak boleh negatif atau kosong");
                obj.put("data", null);
                obj.put("count", 0);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(obj);
            }
            else{
                if (!transaksiList.isEmpty()) {
                    obj.put("status", true);
                    obj.put("message", "Transactions found in amount range");
                    obj.put("data", transaksiList);
                    obj.put("count", transaksiList.size());
                    
                    return ResponseEntity.ok(obj);
                } else {
                    obj.put("status", false);
                    obj.put("message", "No transactions found in the amount range");
                    obj.put("data", new ArrayList<>());
                    obj.put("count", 0);
                    
                    return ResponseEntity.ok(obj);
                }
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error searching transactions by amount range: " + e.getMessage());
            obj.put("data", null);
            obj.put("count", 0);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    @GetMapping("/search/method")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> findByMetode(@RequestParam String nama) {
        Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelTransaksi> transaksiList = rpt.findByMetodeIgnoreCase(nama);
            
            if (!transaksiList.isEmpty()) {
                obj.put("status", true);
                obj.put("message", "Transactions found with payment method");
                obj.put("data", transaksiList);
                obj.put("count", transaksiList.size());
                
                return ResponseEntity.ok(obj);
            } else {
                obj.put("status", false);
                obj.put("message", "No transactions found with payment method containing: " + nama);
                obj.put("data", new ArrayList<>());
                obj.put("count", 0);
                
                return ResponseEntity.ok(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error searching transactions by payment method: " + e.getMessage());
            obj.put("data", null);
            obj.put("count", 0);
            
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    // Additional endpoint for finding by transaction type
//     @GetMapping("/search/type")
//     @ResponseBody
//     public ResponseEntity<Map<String, Object>> findByJenisTransaksi(@RequestParam String type) {
//         Map<String, Object> obj = new HashMap<>();
//         try {
//             List<ModelTransaksi> transaksiList = rpt.findByJenisTransaksi(type);
            
//             if (!transaksiList.isEmpty()) {
//                 obj.put("status", true);
//                 obj.put("message", "Transactions found with transaction type");
//                 obj.put("data", transaksiList);
//                 obj.put("count", transaksiList.size());
                
//                 return ResponseEntity.ok(obj);
//             } else {
//                 obj.put("status", false);
//                 obj.put("message", "No transactions found with transaction type containing: " + type);
//                 obj.put("data", new ArrayList<>());
//                 obj.put("count", 0);
                
//                 return ResponseEntity.ok(obj);
//             }
//         } catch (Exception e) {
//             obj.put("status", false);
//             obj.put("message", "Error searching transactions by type: " + e.getMessage());
//             obj.put("data", null);
//             obj.put("count", 0);
            
//             return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
//         }
//     }
}
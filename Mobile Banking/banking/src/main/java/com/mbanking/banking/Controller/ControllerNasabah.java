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

import com.mbanking.banking.Model.ModelNasabah;
import com.mbanking.banking.Repository.RepositoryNasabah;

@Controller
@RequestMapping("/nasabah")
public class ControllerNasabah {

    @Autowired
    private RepositoryNasabah rpn;

    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getNasabah(){
        Map<String, Object> response = new HashMap<>();
        try {
            Iterable<ModelNasabah> nasabahIterable = rpn.findAll();
            List<ModelNasabah> nasabahList = new ArrayList<>();
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
    public ResponseEntity<Map<String, Object>> addNasabah(@RequestBody ModelNasabah mb){
        Map<String, Object> response = new HashMap<>();
        try {
            rpn.insert(mb);
            response.put("status", true);
            response.put("message", "Nasabah berhasil ditambahkan");
            response.put("data", mb);
            return ResponseEntity.ok(response);
        } 
        catch (Exception e) {
            response.put("status", false);
            response.put("message", "Gagal menambahkan nasabah: " + e.getMessage());
            response.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        } 
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteById(@RequestParam int id) {
        Map<String, Object> obj = new HashMap<>();
        List<ModelNasabah> nasabahList = rpn.getById(id);
        try {
            if (!nasabahList.isEmpty()) {
                rpn.deleteById(id);
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
    public ResponseEntity<Map<String,Object>> updateById(@PathVariable Integer id, @RequestBody ModelNasabah mn){
        Map<String,Object> obj = new HashMap<>();
        List<ModelNasabah> nasabahList = rpn.getById(id);
        try {            
            if(!nasabahList.isEmpty()){
                ModelNasabah mn2 = rpn.getById(id).getFirst();
                mn2.setNama(mn.getNama());
                mn2.setNoRekening(mn.getNoRekening());
                mn2.setAlamat(mn.getAlamat());
                mn2.setJenisKelamin(mn.getJenisKelamin());
                mn2.setSaldo(mn.getSaldo());
                rpn.save(mn2);
                obj.put("status", true);
                obj.put("message", "Nasabah updated successfully");
                obj.put("data", mn2);
                return ResponseEntity.ok(obj);
            }
            else{
                obj.put("status", false);
                obj.put("message", "Nasabah tidak ditemukan dengan id: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error update nasabah: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    @GetMapping("/getbynama")
    public ResponseEntity<Map<String, Object>> getByNama(@RequestParam String nama) {
        Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelNasabah> nasabahList = rpn.findByNamaContainingIgnoreCase(nama);
            if (!nasabahList.isEmpty()) {
            obj.put("status", true);
            obj.put("message", "Data retrieved successfully");
            obj.put("data", nasabahList);
            return ResponseEntity.ok(obj);
            } else {
            obj.put("status", false);
            obj.put("message", "Nasabah dengan nama '" + nama + "' tidak ditemukan");
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error retrieving data: " + e.getMessage());
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }


    @GetMapping("/avgsaldo")
    public ResponseEntity<Map<String, Object>> findAvgSaldo() {
        Map<String, Object> obj = new HashMap<>();
        try {
            BigDecimal avgSaldo = rpn.findAvgSaldo();
            obj.put("status", true);
            obj.put("message", "Average saldo calculated successfully");
            obj.put("avgSaldo", avgSaldo);
            return ResponseEntity.ok(obj);
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error calculating average saldo: " + e.getMessage());
            obj.put("avgSaldo", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    @GetMapping("/saldobesar")
    public ResponseEntity<Map<String, Object>> findNasabahTajir() {
    Map<String, Object> obj = new HashMap<>();
        try {
            List<ModelNasabah> nasabahTajir = rpn.findNasabahTajir();
            if (!nasabahTajir.isEmpty()) {
            obj.put("status", true);
            obj.put("message", "Nasabah tajir retrieved successfully");
            obj.put("data", nasabahTajir);
            return ResponseEntity.ok(obj);
            } else {
            obj.put("status", false);
            obj.put("message", "Tidak ada nasabah dengan saldo di atas rata-rata");
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Error retrieving nasabah tajir: " + e.getMessage());
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }

    @GetMapping("/saldorange")
    public ResponseEntity<Map<String, Object>> findSaldoNasabahRange(@RequestParam BigDecimal min, @RequestParam BigDecimal max) {
        Map<String, Object> obj = new HashMap<>();
        try {
            // Proteksi: min dan max tidak boleh null, dan tidak boleh negatif
            List<ModelNasabah> nasabahList = rpn.findBySaldoBetween(min, max);
            if (min == null || max == null || min.compareTo(BigDecimal.ZERO) < 0 || max.compareTo(BigDecimal.ZERO) < 0) {
                obj.put("status", false);
                obj.put("message", "Field min dan max tidak boleh kosong atau negatif");
                obj.put("data", null);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(obj);
            }
            if (!nasabahList.isEmpty()) {
                obj.put("status", true);
                obj.put("message", "Data nasabah dengan saldo antara " + min + " dan " + max + " berhasil diambil");
                obj.put("data", nasabahList);
                return ResponseEntity.ok(obj);
            } else {
                obj.put("status", false);
                obj.put("message", "Tidak ditemukan nasabah dengan saldo antara " + min + " dan " + max);
                obj.put("data", null);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(obj);
            }
        } catch (Exception e) {
            obj.put("status", false);
            obj.put("message", "Terjadi kesalahan saat mengambil data: " + e.getMessage());
            obj.put("data", null);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(obj);
        }
    }
}

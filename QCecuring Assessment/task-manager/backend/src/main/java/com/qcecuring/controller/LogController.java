package com.qcecuring.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.qcecuring.repository.LogRepository;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api")
public class LogController {
  @Autowired LogRepository logRepo;
  @GetMapping("/logs")
  public ResponseEntity<?> logs(){ return ResponseEntity.ok(logRepo.findAll()); }
}

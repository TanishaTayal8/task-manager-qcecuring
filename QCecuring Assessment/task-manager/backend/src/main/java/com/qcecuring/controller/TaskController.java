package com.qcecuring.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import com.qcecuring.repository.TaskRepository;
import com.qcecuring.repository.LogRepository;
import com.qcecuring.model.Task;
import com.qcecuring.model.AuditLog;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.Instant;
import java.util.*;

@RestController
@RequestMapping("/api")
public class TaskController {
  @Autowired TaskRepository taskRepo;
  @Autowired LogRepository logRepo;

  // helper sanitization
  private String sanitize(String s){
    if(s==null) return "";
    return s.replaceAll("<.*?>","").trim();
  }

  @GetMapping("/tasks")
  public ResponseEntity<?> list(@RequestParam(defaultValue="") String q,
                                @RequestParam(defaultValue="1") int page,
                                @RequestParam(defaultValue="5") int size){
    if(page<1) page=1;
    if(size<1) size=5;
    List<Task> tasks = taskRepo.findAllFiltered(sanitize(q), page, size);
    int total = taskRepo.countFiltered(sanitize(q));
    Map<String,Object> out = new HashMap<>();
    out.put("tasks", tasks);
    out.put("page", page);
    out.put("size", size);
    out.put("total", total);
    return ResponseEntity.ok(out);
  }

  @PostMapping("/tasks")
  public ResponseEntity<?> create(@RequestBody Map<String,String> payload){
    String title = sanitize(payload.get("title"));
    String desc = sanitize(payload.get("description"));
    if(title.isEmpty() || desc.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Title and description cannot be empty"));
    if(title.length()>100 || desc.length()>500) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Length limits exceeded"));
    Task t = new Task();
    t.setTitle(title);
    t.setDescription(desc);
    t.setCreatedAt(Instant.now().toString());
    Task created = taskRepo.create(t);
    AuditLog log = new AuditLog();
    log.setTimestamp(Instant.now().toString());
    log.setAction("Create Task");
    log.setTaskId(created.getId());
    // store full task JSON as updated_content
    log.setUpdatedContent("{\"title\":\""+title+"\",\"description\":\""+desc+"\"}");
    logRepo.create(log);
    return ResponseEntity.status(HttpStatus.CREATED).body(created);
  }

  @PutMapping("/tasks/{id}")
  public ResponseEntity<?> update(@PathVariable int id, @RequestBody Map<String,String> payload){
    try {
      Task existing = taskRepo.findById(id);
      String nt = sanitize(payload.get("title"));
      String nd = sanitize(payload.get("description"));
      if(nt.isEmpty() || nd.isEmpty()) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Title and description cannot be empty"));
      if(nt.length()>100 || nd.length()>500) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error","Length limits exceeded"));
      Map<String,String> changed = new HashMap<>();
      if(!existing.getTitle().equals(nt)) changed.put("title", nt);
      if(!existing.getDescription().equals(nd)) changed.put("description", nd);
      existing.setTitle(nt); existing.setDescription(nd);
      taskRepo.update(existing);
      AuditLog log = new AuditLog();
      log.setTimestamp(Instant.now().toString());
      log.setAction("Update Task");
      log.setTaskId(id);
      log.setUpdatedContent(changed.isEmpty()?null: new com.fasterxml.jackson.databind.ObjectMapper().writeValueAsString(changed));
      logRepo.create(log);
      return ResponseEntity.ok(existing);
    } catch (Exception e){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Task not found"));
    }
  }

  @DeleteMapping("/tasks/{id}")
  public ResponseEntity<?> delete(@PathVariable int id){
    try {
      Task t = taskRepo.findById(id);
      taskRepo.delete(id);
      AuditLog log = new AuditLog();
      log.setTimestamp(Instant.now().toString());
      log.setAction("Delete Task");
      log.setTaskId(id);
      log.setUpdatedContent(null);
      logRepo.create(log);
      return ResponseEntity.ok(Map.of("message","Deleted"));
    } catch (Exception e){
      return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error","Task not found"));
    }
  }
}

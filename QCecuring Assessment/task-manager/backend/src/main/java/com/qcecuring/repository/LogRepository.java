package com.qcecuring.repository;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import com.qcecuring.model.AuditLog;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;
import java.util.List;

@Repository
public class LogRepository {
  private final JdbcTemplate jdbc;
  public LogRepository(JdbcTemplate jdbc){ this.jdbc = jdbc; }
  private final RowMapper<AuditLog> mapper = (ResultSet rs,int rn) -> {
    AuditLog l = new AuditLog();
    l.setId(rs.getInt("id"));
    l.setTimestamp(rs.getString("timestamp"));
    l.setAction(rs.getString("action"));
    l.setTaskId(rs.getObject("task_id") == null ? null : rs.getInt("task_id"));
    l.setUpdatedContent(rs.getString("updated_content"));
    return l;
  };
  public void create(AuditLog l){
    jdbc.update("INSERT INTO audit_logs(timestamp,action,task_id,updated_content) VALUES(?,?,?,?)", l.getTimestamp(), l.getAction(), l.getTaskId(), l.getUpdatedContent());
  }
  public List<AuditLog> findAll(){ return jdbc.query("SELECT * FROM audit_logs ORDER BY id DESC", mapper); }
}

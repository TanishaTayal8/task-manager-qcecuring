package com.qcecuring.repository;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;
import com.qcecuring.model.Task;
import java.util.List;
import java.sql.ResultSet;
import org.springframework.jdbc.core.RowMapper;

@Repository
public class TaskRepository {
  private final JdbcTemplate jdbc;
  public TaskRepository(JdbcTemplate jdbc){ this.jdbc = jdbc; }
  private final RowMapper<Task> mapper = (ResultSet rs, int rowNum) -> {
    Task t = new Task();
    t.setId(rs.getInt("id"));
    t.setTitle(rs.getString("title"));
    t.setDescription(rs.getString("description"));
    t.setCreatedAt(rs.getString("created_at"));
    return t;
  };

  public Task create(Task t){
    jdbc.update("INSERT INTO tasks(title,description,created_at) VALUES(?,?,?)", t.getTitle(), t.getDescription(), t.getCreatedAt());
    Integer id = jdbc.queryForObject("SELECT last_insert_rowid()", Integer.class);
    t.setId(id);
    return t;
  }

  public void update(Task t){
    jdbc.update("UPDATE tasks SET title=?, description=? WHERE id=?", t.getTitle(), t.getDescription(), t.getId());
  }

  public void delete(int id){
    jdbc.update("DELETE FROM tasks WHERE id=?", id);
  }

  public Task findById(int id){
    return jdbc.queryForObject("SELECT * FROM tasks WHERE id=?", mapper, id);
  }

  public List<Task> findAllFiltered(String q, int page, int size){
    int offset = (page-1)*size;
    String like = "%" + q + "%";
    return jdbc.query("SELECT * FROM tasks WHERE title LIKE ? OR description LIKE ? ORDER BY id DESC LIMIT ? OFFSET ?", mapper, like, like, size, offset);
  }

  public int countFiltered(String q){
    String like = "%" + q + "%";
    return jdbc.queryForObject("SELECT count(*) FROM tasks WHERE title LIKE ? OR description LIKE ?", Integer.class, like, like);
  }
}

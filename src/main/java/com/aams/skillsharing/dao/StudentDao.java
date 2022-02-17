package com.aams.skillsharing.dao;

import com.aams.skillsharing.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Repository
public class StudentDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource ds) {
        jdbcTemplate = new JdbcTemplate(ds);
    }

    public void addStudent(Student student) throws DuplicateKeyException {
        jdbcTemplate.update("INSERT INTO student VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                student.getUsername(),
                student.getPassword(),
                student.getBalanceHours(),
                student.isBlocked(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getStreet(),
                student.getNumber(),
                student.getPc(),
                student.getLocality(),
                student.isSkp(),
                student.getDegree()
        );
    }

    public void deleteStudent(Student student) {
        jdbcTemplate.update("DELETE FROM student WHERE username = ?",
                student.getUsername()
        );
    }

    public void deleteStudent(String username){
        jdbcTemplate.update("DELETE FROM student WHERE username = ?",
                username
        );
    }

    public void updateStudent(Student student) {
        jdbcTemplate.update("UPDATE student SET username = ?, password = ?, balance_hours = ?, is_blocked = ?, name = ?, surname = ?, " +
                        "email = ?, street = ?, number = ?, pc = ?, locality = ?, is_skp = ?, degree = ? WHERE username = ?",
                student.getUsername(),
                student.getPassword(),
                student.getBalanceHours(),
                student.isBlocked(),
                student.getName(),
                student.getSurname(),
                student.getEmail(),
                student.getStreet(),
                student.getNumber(),
                student.getPc(),
                student.getLocality(),
                student.isSkp(),
                student.getDegree(),
                student.getUsername()
        );
    }

    public Student getStudent(String username){
        try {
            return jdbcTemplate.queryForObject("SELECT * FROM student WHERE username = ?",
                    new StudentRowMapper(),
                    username
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Student> getStudents(){
        try {
            return jdbcTemplate.query("SELECT * FROM student",
                    new StudentRowMapper()
            );
        } catch (EmptyResultDataAccessException e) {
            return new ArrayList<>();
        }
    }
}

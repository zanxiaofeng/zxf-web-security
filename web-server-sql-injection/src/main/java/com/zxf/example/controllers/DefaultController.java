package com.zxf.example.controllers;

import com.zxf.example.domain.User;
import com.zxf.example.mapper.UserRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class DefaultController {
    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @GetMapping("/jdbc/security/single")
    public User securityJdbcSingle(@RequestParam String id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM USER WHERE ID=?";
            System.out.println("Query: " + query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return new User();
            }
            return new UserRowMapper().mapRow(resultSet, 0);
        }
    }

    @GetMapping("/jdbc/un-security/single")
    public User unSecurityJdbcSingle(@RequestParam String id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format("SELECT * FROM USER WHERE ID='%s'", id);
            System.out.println("Query: " + query);
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            if (!resultSet.next()) {
                return new User();
            }
            return new UserRowMapper().mapRow(resultSet, 0);
        }
    }

    @GetMapping("/jdbc/security/list")
    public List<User> securityJdbcList(@RequestParam String title) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM USER WHERE TITLE=?";
            System.out.println("Query: " + query);
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, title);
            ResultSet resultSet = preparedStatement.executeQuery();

            UserRowMapper userRowMapper = new UserRowMapper();
            List<User> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(userRowMapper.mapRow(resultSet, 0));
            }
            return results;
        }
    }

    @GetMapping("/jdbc/un-security/list")
    public List<User> unSecurityJdbcList(@RequestParam String title) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format("SELECT * FROM USER WHERE TITLE='%s'", title);
            System.out.println("Query: " + query);
            ResultSet resultSet = connection.createStatement().executeQuery(query);

            List<User> results = new ArrayList<>();
            while (resultSet.next()) {
                results.add(new UserRowMapper().mapRow(resultSet, 0));
            }
            return results;
        }
    }

    @GetMapping("/template/security/single")
    public User securityTemplateSingle(@RequestParam String id) {
        String query = "SELECT * FROM USER WHERE ID = ?";
        System.out.println("Query: " + query);
        try {
            return jdbcTemplate.queryForObject(query, new UserRowMapper(), id);
        } catch (EmptyResultDataAccessException ex) {
            return new User();
        }
    }

    @GetMapping("/template/un-security/single")
    public User unSecurityTemplateSingle(@RequestParam String id) {
        String query = String.format("SELECT * FROM USER WHERE ID = '%s'", id);
        System.out.println("Query: " + query);
        try {
            return jdbcTemplate.queryForObject(query, new UserRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return new User();
        }
    }

    @GetMapping("/template/security/list")
    public List<User> securityTemplateList(@RequestParam String title) {
        String query = "SELECT * FROM USER WHERE TITLE = :title";
        System.out.println("Query: " + query);
        Map<String, Object> parameters = Collections.singletonMap("title", title);
        return namedParameterJdbcTemplate.query(query, parameters, new UserRowMapper());
    }

    @GetMapping("/template/un-security/list")
    public List<User> unSecurityTemplateList(@RequestParam String title) {
        String query = String.format("SELECT * FROM USER WHERE TITLE = '%s'", title);
        System.out.println("Query: " + query);
        return namedParameterJdbcTemplate.query(query, new UserRowMapper());
    }
}

package com.zxf.example.controllers;

import com.zxf.example.domain.Customer;
import com.zxf.example.domain.Product;
import com.zxf.example.mapper.ProductRowMapper;
import com.zxf.example.mapper.CustomerRowMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
public class DefaultController {
    @Autowired
    DataSource dataSource;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @GetMapping("/security/update")
    public Integer securityUpdate(@RequestParam String id) throws SQLException {
        log.info("securityUpdate:: id={}", id);
        try (Connection connection = dataSource.getConnection()) {
            String newName = "New - " + LocalDateTime.now();
            String query = "UPDATE CUSTOMER SET NAME=? WHERE ID=?";
            log.info("securityUpdate:: query={}, parameters={}, {}", query, newName, id);

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, id);
                return preparedStatement.executeUpdate();
            }
        }
    }

    @GetMapping("/un-security/update")
    public Integer unSecurityUpdate(@RequestParam String id) throws SQLException {
        log.info("unSecurityUpdate:: id={}", id);
        try (Connection connection = dataSource.getConnection()) {
            String newName = "New - " + LocalDateTime.now();
            String query = String.format("UPDATE CUSTOMER SET NAME='%s' WHERE ID='%s'", newName, id);
            log.info("unSecurityUpdate:: query={}", query);

            try (Statement statement = connection.createStatement()) {
                return statement.executeUpdate(query);
            }
        }
    }

    @GetMapping("/security/query/single")
    public Customer securitySingleQuery(@RequestParam String id) {
        log.info("securitySingleQuery:: id={}", id);
        String query = "SELECT * FROM CUSTOMER WHERE ID=?";
        log.info("securitySingleQuery:: query={}, parameters={}", query, id);

        try {
            return jdbcTemplate.queryForObject(query, new CustomerRowMapper(), id);
        } catch (EmptyResultDataAccessException ex) {
            return new Customer();
        }
    }

    @GetMapping("/un-security/query/single")
    public Customer unSecuritySingleQuery(@RequestParam String id) {
        log.info("unSecuritySingleQuery:: id={}", id);
        String query = String.format("SELECT * FROM CUSTOMER WHERE ID='%s'", id);
        log.info("unSecuritySingleQuery:: query={}", query);

        try {
            return jdbcTemplate.queryForObject(query, new CustomerRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return new Customer();
        }
    }

    @GetMapping("/security/query/list")
    public List<Customer> securityListQuery(@RequestParam String title) {
        log.info("securityListQuery:: title={}", title);
        String query = "SELECT * FROM CUSTOMER WHERE TITLE=:title";
        log.info("securityListQuery:: query={}, parameters={}", query, title);

        Map<String, Object> parameters = Collections.singletonMap("title", title);
        return namedParameterJdbcTemplate.query(query, parameters, new CustomerRowMapper());
    }

    @GetMapping("/un-security/query/list")
    public List<Customer> unSecurityListQuery(@RequestParam String title) {
        log.info("unSecurityListQuery:: title={}", title);
        String query = String.format("SELECT * FROM CUSTOMER WHERE TITLE='%s'", title);
        log.info("unSecurityListQuery:: query={}", query);

        return namedParameterJdbcTemplate.query(query, new CustomerRowMapper());
    }

    @GetMapping("/products")
    public List<Product> products() {
        String query = "SELECT * FROM PRODUCT";
        log.info("products:: query={}", query);
        return namedParameterJdbcTemplate.query(query, new ProductRowMapper());
    }
}

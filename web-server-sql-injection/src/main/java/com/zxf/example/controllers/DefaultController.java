package com.zxf.example.controllers;

import com.zxf.example.domain.Customer;
import com.zxf.example.domain.Product;
import com.zxf.example.mapper.ProductRowMapper;
import com.zxf.example.mapper.CustomerRowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
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

    @GetMapping("/jdbc/security/update")
    public Integer securityJdbcUpdate(@RequestParam String id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "UPDATE CUSTOMER SET NAME=? WHERE ID=?";
            System.out.println("Query: " + query);

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                String newName = "New - " + LocalDateTime.now();
                preparedStatement.setString(1, newName);
                preparedStatement.setString(2, id);
                return preparedStatement.executeUpdate();
            }
        }
    }

    @GetMapping("/jdbc/un-security/update")
    public Integer unSecurityJdbcUpdate(@RequestParam String id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String newName = "New - " + LocalDateTime.now();
            String query = String.format("UPDATE CUSTOMER SET NAME='%s' WHERE ID='%s'", newName, id);
            System.out.println("Query: " + query);

            try (Statement statement = connection.createStatement()) {
                return statement.executeUpdate(query);
            }
        }
    }

    @GetMapping("/jdbc/security/single")
    public Customer securityJdbcSingle(@RequestParam String id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM CUSTOMER WHERE ID=?";
            System.out.println("Query: " + query);

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, id);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (!resultSet.next()) {
                        return new Customer();
                    }
                    return new CustomerRowMapper().mapRow(resultSet, 0);
                }
            }
        }
    }

    @GetMapping("/jdbc/un-security/single")
    public Customer unSecurityJdbcSingle(@RequestParam String id) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format("SELECT * FROM CUSTOMER WHERE ID='%s'", id);
            System.out.println("Query: " + query);

            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                if (!resultSet.next()) {
                    return new Customer();
                }
                return new CustomerRowMapper().mapRow(resultSet, 0);
            }
        }
    }

    @GetMapping("/jdbc/security/list")
    public List<Customer> securityJdbcList(@RequestParam String title) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = "SELECT * FROM CUSTOMER WHERE TITLE=?";
            System.out.println("Query: " + query);

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setString(1, title);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    CustomerRowMapper customerRowMapper = new CustomerRowMapper();
                    List<Customer> results = new ArrayList<>();
                    while (resultSet.next()) {
                        results.add(customerRowMapper.mapRow(resultSet, 0));
                    }
                    return results;
                }
            }
        }
    }

    @GetMapping("/jdbc/un-security/list")
    public List<Customer> unSecurityJdbcList(@RequestParam String title) throws SQLException {
        try (Connection connection = dataSource.getConnection()) {
            String query = String.format("SELECT * FROM CUSTOMER WHERE TITLE='%s';", title);
            System.out.println("Query: " + query);

            try (Statement statement = connection.createStatement(); ResultSet resultSet = statement.executeQuery(query)) {
                List<Customer> results = new ArrayList<>();
                while (resultSet.next()) {
                    results.add(new CustomerRowMapper().mapRow(resultSet, 0));
                }
                return results;
            }
        }
    }

    @GetMapping("/template/security/single")
    public Customer securityTemplateSingle(@RequestParam String id) {
        String query = "SELECT * FROM CUSTOMER WHERE ID = ?";
        System.out.println("Query: " + query);
        try {
            return jdbcTemplate.queryForObject(query, new CustomerRowMapper(), id);
        } catch (EmptyResultDataAccessException ex) {
            return new Customer();
        }
    }

    @GetMapping("/template/un-security/single")
    public Customer unSecurityTemplateSingle(@RequestParam String id) {
        String query = String.format("SELECT * FROM CUSTOMER WHERE ID = '%s'", id);
        System.out.println("Query: " + query);
        try {
            return jdbcTemplate.queryForObject(query, new CustomerRowMapper());
        } catch (EmptyResultDataAccessException ex) {
            return new Customer();
        }
    }

    @GetMapping("/template/security/list")
    public List<Customer> securityTemplateList(@RequestParam String title) {
        String query = "SELECT * FROM CUSTOMER WHERE TITLE = :title";
        System.out.println("Query: " + query);
        Map<String, Object> parameters = Collections.singletonMap("title", title);
        return namedParameterJdbcTemplate.query(query, parameters, new CustomerRowMapper());
    }

    @GetMapping("/template/un-security/list")
    public List<Customer> unSecurityTemplateList(@RequestParam String title) {
        String query = String.format("SELECT * FROM CUSTOMER WHERE TITLE = '%s'", title);
        System.out.println("Query: " + query);
        return namedParameterJdbcTemplate.query(query, new CustomerRowMapper());
    }

    @GetMapping("/products")
    public List<Product> products() {
        return namedParameterJdbcTemplate.query("SELECT * FROM PRODUCT", new ProductRowMapper());
    }
}

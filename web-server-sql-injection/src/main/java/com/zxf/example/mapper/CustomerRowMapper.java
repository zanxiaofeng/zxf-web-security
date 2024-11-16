package com.zxf.example.mapper;

import com.zxf.example.domain.Customer;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CustomerRowMapper implements RowMapper<Customer> {
    @Override
    public Customer mapRow(ResultSet rs, int rowNum) throws SQLException {
        Customer customer = new Customer();
        customer.setId(rs.getString("ID"));
        customer.setName(rs.getString("NAME"));
        customer.setTitle(rs.getString("TITLE"));
        return customer;
    }
}

package com.zxf.example.mapper;

import com.zxf.example.domain.Product;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductRowMapper implements RowMapper<Product> {
    @Override
    public Product mapRow(ResultSet rs, int rowNum) throws SQLException {
        Product product = new Product();
        product.setId(rs.getString("ID"));
        product.setName(rs.getString("NAME"));
        product.setTitle(rs.getString("TITLE"));
        return product;
    }
}

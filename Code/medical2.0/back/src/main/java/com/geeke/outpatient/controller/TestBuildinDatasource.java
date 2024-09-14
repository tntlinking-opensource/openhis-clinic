package com.geeke.outpatient.controller;

import com.bstek.ureport.definition.datasource.BuildinDatasource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

@Component
class TestBuildinDatasource implements BuildinDatasource {
    @Autowired
    private DataSource dataSource;
    /**
     * @return 返回数据源名称
     */
    @Override
    public String name() {
        return "内置数据源DEMO";
    }
    /**
     * @return 返回当前采用数据源的一个连接
     */
    @Override
    public Connection getConnection() {
        try {
            return dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }
}

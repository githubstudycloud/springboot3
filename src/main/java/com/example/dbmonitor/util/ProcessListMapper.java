package com.example.dbmonitor.util;

import com.example.dbmonitor.entity.ProcessInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * MySQL ProcessList结果映射工具类
 * 统一处理SHOW FULL PROCESSLIST查询结果的映射
 */
@Slf4j
public class ProcessListMapper implements RowMapper<ProcessInfo> {

    @Override
    public ProcessInfo mapRow(ResultSet rs, int rowNum) throws SQLException {
        ProcessInfo process = new ProcessInfo();

        // 安全地获取值，处理可能的NULL
        process.setId(getLongValue(rs, "Id"));
        process.setUser(getStringValue(rs, "User"));
        process.setHost(getStringValue(rs, "Host"));
        process.setDb(getStringValue(rs, "db"));
        process.setCommand(getStringValue(rs, "Command"));
        process.setTime(getLongValue(rs, "Time"));
        process.setState(getStringValue(rs, "State"));
        process.setInfo(getStringValue(rs, "Info"));

        return process;
    }

    private Long getLongValue(ResultSet rs, String columnName) throws SQLException {
        long value = rs.getLong(columnName);
        return rs.wasNull() ? null : value;
    }

    private String getStringValue(ResultSet rs, String columnName) throws SQLException {
        String value = rs.getString(columnName);
        // 处理过长的SQL语句，避免内存问题
        if (value != null && value.length() > 1000) {
            return value.substring(0, 1000) + "...";
        }
        return value;
    }
}
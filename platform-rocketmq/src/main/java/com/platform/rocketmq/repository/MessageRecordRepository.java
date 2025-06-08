package com.platform.rocketmq.repository;

import com.platform.rocketmq.entity.MessageRecord;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 消息记录仓库
 * 负责消息记录的持久化操作
 *
 * @author Platform Team
 * @since 1.0.0
 */
@Repository
public class MessageRecordRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    private static final String INSERT_SQL = """
        INSERT INTO message_record (
            message_id, topic, tag, consumer_group, message_body, 
            message_key, consume_status, retry_times, error_code, 
            error_message, receive_time, consume_time, create_time, update_time
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
    
    private static final String UPDATE_STATUS_SQL = """
        UPDATE message_record 
        SET consume_status = ?, error_code = ?, error_message = ?, 
            retry_times = ?, consume_time = ?, update_time = ?
        WHERE message_id = ?
        """;
    
    private static final String FIND_BY_MESSAGE_ID_SQL = """
        SELECT * FROM message_record WHERE message_id = ?
        """;
    
    private static final String FIND_FAILED_MESSAGES_SQL = """
        SELECT * FROM message_record 
        WHERE consume_status = 'FAILED' 
        AND retry_times < ?
        ORDER BY create_time ASC
        LIMIT ?
        """;
    
    public MessageRecordRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
        initTable();
    }
    
    /**
     * 初始化表结构
     */
    private void initTable() {
        String createTableSql = """
            CREATE TABLE IF NOT EXISTS message_record (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                message_id VARCHAR(128) NOT NULL UNIQUE,
                topic VARCHAR(255) NOT NULL,
                tag VARCHAR(255),
                consumer_group VARCHAR(255) NOT NULL,
                message_body TEXT,
                message_key VARCHAR(255),
                consume_status VARCHAR(32) NOT NULL,
                retry_times INT DEFAULT 0,
                error_code VARCHAR(64),
                error_message TEXT,
                receive_time TIMESTAMP NOT NULL,
                consume_time TIMESTAMP,
                create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                INDEX idx_message_id (message_id),
                INDEX idx_status_retry (consume_status, retry_times),
                INDEX idx_create_time (create_time)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci
            """;
        
        jdbcTemplate.execute(createTableSql);
    }
    
    /**
     * 保存消息记录
     *
     * @param record 消息记录
     * @return 保存结果
     */
    @Transactional
    public boolean save(MessageRecord record) {
        LocalDateTime now = LocalDateTime.now();
        record.setCreateTime(now);
        record.setUpdateTime(now);
        
        int rows = jdbcTemplate.update(INSERT_SQL,
            record.getMessageId(),
            record.getTopic(),
            record.getTag(),
            record.getConsumerGroup(),
            record.getMessageBody(),
            record.getMessageKey(),
            record.getConsumeStatus(),
            record.getRetryTimes(),
            record.getErrorCode(),
            record.getErrorMessage(),
            Timestamp.valueOf(record.getReceiveTime()),
            record.getConsumeTime() != null ? Timestamp.valueOf(record.getConsumeTime()) : null,
            Timestamp.valueOf(record.getCreateTime()),
            Timestamp.valueOf(record.getUpdateTime())
        );
        
        return rows > 0;
    }
    
    /**
     * 更新消费状态
     *
     * @param messageId 消息ID
     * @param status 消费状态
     * @param errorCode 错误码
     * @param errorMessage 错误信息
     * @param retryTimes 重试次数
     * @return 更新结果
     */
    @Transactional
    public boolean updateConsumeStatus(String messageId, String status, 
                                     String errorCode, String errorMessage, int retryTimes) {
        LocalDateTime now = LocalDateTime.now();
        
        int rows = jdbcTemplate.update(UPDATE_STATUS_SQL,
            status,
            errorCode,
            errorMessage,
            retryTimes,
            Timestamp.valueOf(now),
            Timestamp.valueOf(now),
            messageId
        );
        
        return rows > 0;
    }
    
    /**
     * 根据消息ID查询
     *
     * @param messageId 消息ID
     * @return 消息记录
     */
    public Optional<MessageRecord> findByMessageId(String messageId) {
        List<MessageRecord> records = jdbcTemplate.query(
            FIND_BY_MESSAGE_ID_SQL,
            new MessageRecordRowMapper(),
            messageId
        );
        
        return records.isEmpty() ? Optional.empty() : Optional.of(records.get(0));
    }
    
    /**
     * 查询失败的消息（用于重试）
     *
     * @param maxRetryTimes 最大重试次数
     * @param limit 查询限制
     * @return 失败的消息列表
     */
    public List<MessageRecord> findFailedMessages(int maxRetryTimes, int limit) {
        return jdbcTemplate.query(
            FIND_FAILED_MESSAGES_SQL,
            new MessageRecordRowMapper(),
            maxRetryTimes,
            limit
        );
    }
    
    /**
     * 消息记录行映射器
     */
    private static class MessageRecordRowMapper implements RowMapper<MessageRecord> {
        @Override
        public MessageRecord mapRow(ResultSet rs, int rowNum) throws SQLException {
            MessageRecord record = new MessageRecord();
            record.setId(rs.getLong("id"));
            record.setMessageId(rs.getString("message_id"));
            record.setTopic(rs.getString("topic"));
            record.setTag(rs.getString("tag"));
            record.setConsumerGroup(rs.getString("consumer_group"));
            record.setMessageBody(rs.getString("message_body"));
            record.setMessageKey(rs.getString("message_key"));
            record.setConsumeStatus(rs.getString("consume_status"));
            record.setRetryTimes(rs.getInt("retry_times"));
            record.setErrorCode(rs.getString("error_code"));
            record.setErrorMessage(rs.getString("error_message"));
            
            Timestamp receiveTime = rs.getTimestamp("receive_time");
            if (receiveTime != null) {
                record.setReceiveTime(receiveTime.toLocalDateTime());
            }
            
            Timestamp consumeTime = rs.getTimestamp("consume_time");
            if (consumeTime != null) {
                record.setConsumeTime(consumeTime.toLocalDateTime());
            }
            
            Timestamp createTime = rs.getTimestamp("create_time");
            if (createTime != null) {
                record.setCreateTime(createTime.toLocalDateTime());
            }
            
            Timestamp updateTime = rs.getTimestamp("update_time");
            if (updateTime != null) {
                record.setUpdateTime(updateTime.toLocalDateTime());
            }
            
            return record;
        }
    }
}
-- 库存表
CREATE TABLE IF NOT EXISTS inventories (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id VARCHAR(255) NOT NULL UNIQUE,
    available_quantity INT NOT NULL,
    reserved_quantity INT NOT NULL,
    min_threshold INT,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP NOT NULL
);

-- 初始化一些测试数据
INSERT INTO inventories (product_id, available_quantity, reserved_quantity, min_threshold, status, created_at, updated_at)
VALUES
    ('P001', 100, 0, 10, 'IN_STOCK', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    ('P002', 50, 0, 5, 'IN_STOCK', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    ('P003', 20, 0, 15, 'LOW_STOCK', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP()),
    ('P004', 0, 10, 5, 'OUT_OF_STOCK', CURRENT_TIMESTAMP(), CURRENT_TIMESTAMP());

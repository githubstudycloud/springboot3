package com.example.platform.config.domain.service;

/**
 * 配置加密服务接口
 * 
 * <p>定义配置内容加密解密的核心业务逻辑</p>
 */
public interface ConfigEncryptionDomainService {
    
    /**
     * 加密配置内容
     *
     * @param content 原始内容
     * @return 加密后的内容
     */
    String encrypt(String content);
    
    /**
     * 解密配置内容
     *
     * @param encryptedContent 加密内容
     * @return 解密后的内容
     */
    String decrypt(String encryptedContent);
    
    /**
     * 判断内容是否已加密
     *
     * @param content 配置内容
     * @return 是否已加密
     */
    boolean isEncrypted(String content);
    
    /**
     * 处理配置内容加密
     * 如果配置需要加密且未加密，则进行加密处理
     *
     * @param content 配置内容
     * @param needEncryption 是否需要加密
     * @return 处理后的配置内容
     */
    String handleEncryption(String content, boolean needEncryption);
    
    /**
     * 处理配置内容解密
     * 如果配置内容已加密，则进行解密处理
     *
     * @param content 配置内容
     * @return 解密后的配置内容
     */
    String handleDecryption(String content);
}

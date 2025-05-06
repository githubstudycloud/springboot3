package com.example.platform.config.domain.service.impl;

import com.example.platform.config.domain.service.ConfigEncryptionDomainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

/**
 * 配置加密服务实现
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConfigEncryptionDomainServiceImpl implements ConfigEncryptionDomainService {

    private static final String ALGORITHM = "AES";
    private static final String CIPHER_TRANSFORMATION = "AES/ECB/PKCS5Padding";
    private static final String ENCRYPTED_PREFIX = "ENC:";

    @Value("${config.encrypt.key:platform-config-encrypt-key}")
    private String encryptKey;

    @Override
    public String encrypt(String content) {
        if (content == null || content.isEmpty()) {
            return content;
        }
        
        try {
            SecretKeySpec secretKey = generateKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            byte[] encryptedBytes = cipher.doFinal(content.getBytes(StandardCharsets.UTF_8));
            return ENCRYPTED_PREFIX + Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            log.error("Error encrypting content", e);
            throw new RuntimeException("Failed to encrypt content", e);
        }
    }

    @Override
    public String decrypt(String encryptedContent) {
        if (encryptedContent == null || encryptedContent.isEmpty()) {
            return encryptedContent;
        }
        
        String content = encryptedContent;
        if (content.startsWith(ENCRYPTED_PREFIX)) {
            content = content.substring(ENCRYPTED_PREFIX.length());
        } else {
            // 内容不是加密格式
            return encryptedContent;
        }
        
        try {
            SecretKeySpec secretKey = generateKey();
            Cipher cipher = Cipher.getInstance(CIPHER_TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, secretKey);
            byte[] decodedBytes = Base64.getDecoder().decode(content);
            byte[] decryptedBytes = cipher.doFinal(decodedBytes);
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error decrypting content", e);
            throw new RuntimeException("Failed to decrypt content", e);
        }
    }

    @Override
    public boolean isEncrypted(String content) {
        return content != null && content.startsWith(ENCRYPTED_PREFIX);
    }

    @Override
    public String handleEncryption(String content, boolean needEncryption) {
        if (content == null) {
            return null;
        }
        
        if (needEncryption && !isEncrypted(content)) {
            return encrypt(content);
        }
        
        return content;
    }

    @Override
    public String handleDecryption(String content) {
        if (content == null) {
            return null;
        }
        
        if (isEncrypted(content)) {
            return decrypt(content);
        }
        
        return content;
    }

    /**
     * 生成加密密钥
     *
     * @return 密钥
     * @throws NoSuchAlgorithmException 如果加密算法不存在
     */
    private SecretKeySpec generateKey() throws NoSuchAlgorithmException {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(encryptKey.getBytes(StandardCharsets.UTF_8));
        keyBytes = Arrays.copyOf(keyBytes, 16); // 使用前16位作为AES密钥
        return new SecretKeySpec(keyBytes, ALGORITHM);
    }
}

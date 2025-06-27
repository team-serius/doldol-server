package doldol_server.doldol.auth.util;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class EncryptionUtil {
    
    private final PooledPBEStringEncryptor jasyptEncryptor;
    
    @Autowired
    public EncryptionUtil(@Qualifier("jasyptStringEncryptor") PooledPBEStringEncryptor jasyptEncryptor) {
        this.jasyptEncryptor = jasyptEncryptor;
    }
    
    public String encrypt(String plainText) {
        return jasyptEncryptor.encrypt(plainText);
    }

    public String decrypt(String encryptedText) {
        return jasyptEncryptor.decrypt(encryptedText);
    }
    
}
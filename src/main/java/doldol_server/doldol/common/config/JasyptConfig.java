package doldol_server.doldol.common.config;

import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.jasypt.salt.FixedStringSaltGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JasyptConfig {

    @Value("${jasypt.encryptor.password}")
    private String encryptKey;

    @Value("${jasypt.encryptor.salt}")
    private String saltValue;

    @Bean(name = "jasyptStringEncryptor")
    public PooledPBEStringEncryptor stringEncryptor() {
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();

        config.setPassword(encryptKey);
        config.setStringOutputType("base64");

        encryptor.setConfig(config);

        FixedStringSaltGenerator saltGenerator = new FixedStringSaltGenerator();
        saltGenerator.setSalt(saltValue);
        encryptor.setSaltGenerator(saltGenerator);

        return encryptor;
    }
}
package doldol_server.doldol.common;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import jakarta.transaction.Transactional;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
public abstract class ServiceTest {
}
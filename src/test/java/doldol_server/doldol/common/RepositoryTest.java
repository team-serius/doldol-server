package doldol_server.doldol.common;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.test.context.ActiveProfiles;

import doldol_server.doldol.common.config.QueryDslConfig;

@EnableJpaAuditing
@DataJpaTest
@ActiveProfiles("test")
@Import(QueryDslConfig.class)
public abstract class RepositoryTest {
}
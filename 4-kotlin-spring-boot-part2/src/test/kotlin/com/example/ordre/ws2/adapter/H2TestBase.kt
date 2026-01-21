package com.example.ordre.ws2.adapter

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.TestPropertySource

@DataJpaTest
@TestPropertySource(
    properties = [
        // Eksplisitt H2 in-memory database konfiguration
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.datasource.username=sa",
        "spring.datasource.password=",
        // JPA config
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",  // Mindre verbose i tester
        // Flyway - deaktiver for H2 tests (vi bruker ddl-auto i stedet)
        "spring.flyway.enabled=false",
        "spring.jpa.defer-datasource-initialization=true"
    ]
)
abstract class H2TestBase {
    // Ingen ekstra setup nødvendig - H2 starter automatisk
    // @DataJpaTest gir oss også TestEntityManager automatisk
}

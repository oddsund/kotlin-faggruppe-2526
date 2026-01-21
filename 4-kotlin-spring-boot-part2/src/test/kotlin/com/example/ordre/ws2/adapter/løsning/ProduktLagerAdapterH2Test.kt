package com.example.ordre.ws2.adapter.løsning

import com.example.ordre.ws2.adapter.H2TestBase
import com.example.ordre.ws2.persistence.entity.ProduktLagerEntity
import com.example.ordre.ws2.persistence.repository.ProduktLagerRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

/**
 * Integration test for ProduktLagerAdapter med H2 in-memory database
 *
 * Samme test som ProduktLagerAdapterTest, men bruker H2 i stedet for Testcontainers!
 *
 * Fordeler:
 * - Raskere kjøring
 * - Ingen Docker-avhengighet
 * - Perfekt for TDD og rask utvikling
 *
 * Test coverage:
 * - Mapping mellom Entity og Domain model
 * - Nullable håndtering
 * - Edge cases (utsolgte produkter, store tall)
 *
 * MERK: Eneste forskjell fra ProduktLagerAdapterTest er base class (H2TestBase vs PostgresTestBase)
 */
class ProduktLagerAdapterH2Test() : H2TestBase() {

    @Autowired
    private lateinit var produktLagerRepository: ProduktLagerRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `skal mappe ProduktLagerEntity til ProduktLager domain model`() {
        val produktEntity = ProduktLagerEntity(
            produktId = "TEST-001",
            antallPåLager = 150
        )
        produktLagerRepository.save(produktEntity)

        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("TEST-001")

        produktLager shouldNotBe null
        produktLager!!.produktId shouldBe "TEST-001"
        produktLager.antallPåLager shouldBe 150
    }

    @Test
    fun `skal returnere null når produkt ikke finnes`() {
        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("FINNES-IKKE")

        produktLager shouldBe null
    }

    @Test
    fun `skal håndtere utsolgte produkter (antall = 0)`() {
        val utsolgtProdukt = ProduktLagerEntity(
            produktId = "UTSOLGT",
            antallPåLager = 0
        )
        produktLagerRepository.save(utsolgtProdukt)

        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("UTSOLGT")

        produktLager shouldNotBe null
        produktLager!!.antallPåLager shouldBe 0
    }

    @Test
    fun `skal håndtere store lagerbeholdninger`() {
        val produktEntity = ProduktLagerEntity(
            produktId = "BULK-ITEM",
            antallPåLager = 999999
        )
        produktLagerRepository.save(produktEntity)

        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("BULK-ITEM")

        produktLager shouldNotBe null
        produktLager!!.antallPåLager shouldBe 999999
    }
}

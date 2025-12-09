package com.example.ordre.ws2.adapter.løsning

import com.example.ordre.ws2.adapter.PostgresTestBase
import com.example.ordre.ws2.persistence.entity.ProduktLagerEntity
import com.example.ordre.ws2.persistence.repository.ProduktLagerRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

/**
 * Integration test for ProduktLagerAdapter
 *
 * Samme konsept som KundeAdapterTest:
 * - Fokuserer på mapping mellom Entity og Domain model
 * - Tester mot ekte database (PostgreSQL)
 * - Verifiserer at adapteren håndterer nullable korrekt
 *
 * ProduktLagerAdapter er enklere enn KundeAdapter fordi repository
 * returnerer nullable direkte (ikke Optional)
 */
class ProduktLagerAdapterTest : PostgresTestBase() {

    @Autowired
    private lateinit var produktLagerRepository: ProduktLagerRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `skal mappe ProduktLagerEntity til ProduktLager domain model`() {
        // Arrange: Lagre et produkt i database
        val produktEntity = ProduktLagerEntity(
            produktId = "TEST-001",
            antallPåLager = 150
        )
        entityManager.persist(produktEntity)
        entityManager.flush()
        entityManager.clear()

        // Act: Hent via adapter
        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("TEST-001")

        // Assert: Verifiser mapping
        produktLager shouldNotBe null
        produktLager!!.produktId shouldBe "TEST-001"
        produktLager.antallPåLager shouldBe 150
    }

    @Test
    fun `skal returnere null når produkt ikke finnes`() {
        // Act: Forsøk å hente ikke-eksisterende produkt
        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("FINNES-IKKE")

        // Assert: Skal returnere null
        produktLager shouldBe null
    }

    @Test
    fun `skal håndtere utsolgte produkter (antall = 0)`() {
        // Arrange: Lagre utsolgt produkt
        val utsolgtProdukt = ProduktLagerEntity(
            produktId = "UTSOLGT",
            antallPåLager = 0
        )
        entityManager.persist(utsolgtProdukt)
        entityManager.flush()

        // Act
        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("UTSOLGT")

        // Assert: Skal finne produktet, men antall skal være 0
        produktLager shouldNotBe null
        produktLager!!.antallPåLager shouldBe 0
    }

    @Test
    fun `skal håndtere store lagerbeholdninger`() {
        // Arrange: Produkt med stor beholdning
        val produktEntity = ProduktLagerEntity(
            produktId = "BULK-ITEM",
            antallPåLager = 999999
        )
        entityManager.persist(produktEntity)
        entityManager.flush()

        // Act
        val adapter = ProduktLagerAdapter(produktLagerRepository)
        val produktLager = adapter.hentLagerStatus("BULK-ITEM")

        // Assert
        produktLager shouldNotBe null
        produktLager!!.antallPåLager shouldBe 999999
    }
}

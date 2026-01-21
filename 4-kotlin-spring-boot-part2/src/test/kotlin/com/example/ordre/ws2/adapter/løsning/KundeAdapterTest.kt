package com.example.ordre.ws2.adapter.løsning

import com.example.ordre.ws2.adapter.PostgresTestBase
import com.example.ordre.ws2.persistence.entity.KundeEntity
import com.example.ordre.ws2.persistence.repository.KundeRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

/**
 * Integration test for KundeAdapter
 *
 * Hva tester vi her?
 * - At adapteren korrekt mapper fra KundeEntity (persistence) til Kunde (domain)
 * - At adapteren håndterer Optional -> nullable korrekt
 * - At adapteren fungerer med ekte database (PostgreSQL via Testcontainers)
 *
 * Hva tester vi IKKE?
 * - JPA repository funksjonalitet (det er Spring Data sitt ansvar)
 * - Domain logic (det testes i OrdreServiceTest)
 *
 * Derfor er testene enkle - vi fokuserer på mapping, ikke business logic
 */
class KundeAdapterTest : PostgresTestBase() {

    @Autowired
    private lateinit var kundeRepository: KundeRepository

    @Test
    fun `skal mappe KundeEntity til Kunde domain model`() {
        val kundeEntity = KundeEntity(
            navn = "Test Kunde AS",
            erAktiv = true
        )
        kundeRepository.save(kundeEntity)

        val savedId = kundeEntity.id
        savedId shouldNotBe null

        val adapter = KundeAdapter(kundeRepository)
        val kunde = adapter.hentKunde(savedId!!)

        kunde shouldNotBe null
        kunde!!.id shouldBe savedId
        kunde.navn shouldBe "Test Kunde AS"
        kunde.erAktiv shouldBe true
    }

    @Test
    fun `skal returnere null når kunde ikke finnes`() {
        val adapter = KundeAdapter(kundeRepository)
        val kunde = adapter.hentKunde(99999)

        kunde shouldBe null
    }

    @Test
    fun `skal håndtere inaktiv kunde korrekt`() {
        val inaktivKunde = KundeEntity(
            id = null,
            navn = "Inaktiv Kunde",
            erAktiv = false
        )
        kundeRepository.save(inaktivKunde)
        val savedId = inaktivKunde.id!!

        val adapter = KundeAdapter(kundeRepository)
        val kunde = adapter.hentKunde(savedId)

        kunde shouldNotBe null
        kunde!!.erAktiv shouldBe false
    }
}

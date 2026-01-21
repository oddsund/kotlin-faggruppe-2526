package com.example.ordre.ws2.adapter.løsning

import com.example.ordre.ws2.adapter.H2TestBase
import com.example.ordre.ws2.persistence.entity.KundeEntity
import com.example.ordre.ws2.persistence.repository.KundeRepository
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager

/**
 * Integration test for KundeAdapter med H2 in-memory database
 *
 * Identisk som KundeAdapterTest, men bruker H2 i stedet for Testcontainers!
 *
 * Fordeler:
 * - Raskere kjøring (~1-2 sekunder vs ~5-10 sekunder for Testcontainers)
 * - Ingen Docker-avhengighet
 * - Enklere setup - bare extend H2TestBase
 *
 * Test coverage:
 * - At adapteren korrekt mapper fra KundeEntity til Kunde domain model
 * - At adapteren håndterer Optional -> nullable korrekt
 * - At adapteren fungerer med H2 database
 *
 * MERK: Sammenlign denne med KundeAdapterTest - eneste forskjell er base class!
 */
class KundeAdapterH2Test : H2TestBase() {

    @Autowired
    private lateinit var kundeRepository: KundeRepository

    @Autowired
    private lateinit var entityManager: TestEntityManager

    @Test
    fun `skal mappe KundeEntity til Kunde domain model`() {
        val kundeEntity = KundeEntity(
            id = null,  // Genereres av database
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

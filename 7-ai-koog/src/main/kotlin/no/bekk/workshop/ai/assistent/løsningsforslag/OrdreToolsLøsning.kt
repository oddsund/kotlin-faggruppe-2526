package no.bekk.workshop.ai.assistent.løsningsforslag

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import no.bekk.workshop.ai.produkt.ProduktKatalog
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository

@LLMDescription("Verktøy for å hente informasjon om kunder, lagerbeholdning og produkter")
class OrdreToolsLøsning(
    private val kundeRepo: KundeRepository,
    private val lagerRepo: LagerRepository,
    private val produktKatalog: ProduktKatalog,
) : ToolSet {

    @Tool
    @LLMDescription("Slår opp en kunde basert på ID og returnerer status og navn")
    suspend fun slåOppKunde(
        @LLMDescription("Kundens unike ID") kundeId: Long,
    ): String {
        val kunde = kundeRepo.hent(kundeId)
            ?: return "Kunde $kundeId ble ikke funnet"
        val status = if (kunde.erAktiv) "aktiv" else "inaktiv"
        return "Kunde $kundeId: ${kunde.navn} ($status)"
    }

    @Tool
    @LLMDescription("Sjekker antall på lager for et gitt produkt")
    suspend fun sjekkLager(
        @LLMDescription("Produkt-ID, f.eks. P001") produktId: String,
    ): String {
        val beholdning = lagerRepo.hentBeholdning(produktId)
        return "Produkt $produktId: $beholdning på lager"
    }

    @Tool
    @LLMDescription("Henter produktbeskrivelse fra produktkatalogen")
    fun hentProduktBeskrivelse(
        @LLMDescription("Produkt-ID, f.eks. P001") produktId: String,
    ): String {
        val produkt = produktKatalog.finn(produktId)
            ?: return "Produkt $produktId ble ikke funnet i katalogen"
        return "Produkt ${produkt.id} (${produkt.navn}): ${produkt.beskrivelse}"
    }
}

package no.bekk.workshop.ai.assistent

import ai.koog.agents.core.tools.annotations.LLMDescription
import ai.koog.agents.core.tools.annotations.Tool
import ai.koog.agents.core.tools.reflect.ToolSet
import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository

@LLMDescription("Verktøy for å hente informasjon om kunder og lagerbeholdning")
class OrdreTools(
    private val kundeRepo: KundeRepository,
    private val lagerRepo: LagerRepository,
) : ToolSet {

    @Tool
    @LLMDescription("Slår opp en kunde basert på ID og returnerer status og navn")
    suspend fun slåOppKunde(
        @LLMDescription("Kundens unike ID") kundeId: Long,
    ): String {
        TODO("Implementer: hent kunden fra kundeRepo og returner en lesbar streng med navn og status")
    }

    @Tool
    @LLMDescription("Sjekker antall på lager for et gitt produkt")
    suspend fun sjekkLager(
        @LLMDescription("Produkt-ID, f.eks. P001") produktId: String,
    ): String {
        TODO("Implementer: hent beholdning fra lagerRepo og returner en lesbar streng")
    }
}

package no.bekk.workshop.ai.diagnose.løsningsforslag

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("DiagnoseLøsning")
@LLMDescription("Strukturert diagnose av en support-henvendelse om en ordre")
data class DiagnoseLøsning(
    @property:LLMDescription("Kort, faktuell oppsummering av problemet i én setning. Ikke gjenta hele henvendelsen.")
    val sammendrag: String,
    @property:LLMDescription(
        "Den mest sannsynlige tekniske eller domeneårsaken (f.eks. lagerstatus, kundestatus, ordre-id-feil). " +
            "Skriv én konkret hypotese, ikke en liste."
    )
    val sannsynligÅrsak: String,
    @property:LLMDescription(
        "Konkret neste steg support kan utføre nå (f.eks. 'sjekk lagerbeholdning for produkt X', " +
            "'verifiser at kunden er aktiv'). Ikke generelle råd."
    )
    val foreslåttHandling: String,
)

package no.bekk.workshop.ai.diagnose

import ai.koog.agents.core.tools.annotations.LLMDescription
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
@SerialName("Diagnose")
@LLMDescription("Strukturert diagnose av en support-henvendelse om en ordre")
data class Diagnose(
    @property:LLMDescription("Et sammendrag")
    val sammendrag: String,
    @property:LLMDescription("Årsaken")
    val sannsynligÅrsak: String,
    @property:LLMDescription("Hva gjøres nå")
    val foreslåttHandling: String,
)

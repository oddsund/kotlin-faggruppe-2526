package no.bekk.workshop.domain

data class Ordre(
    val kundeId: Long,
    val varer: List<OrdreVare>
) {
    fun totalBeløp(): Double = varer.sumOf { it.totalPris() }

    companion object
}

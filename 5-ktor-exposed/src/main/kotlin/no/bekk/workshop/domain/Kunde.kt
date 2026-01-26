package no.bekk.workshop.domain

data class Kunde(
    val id: Long,
    val navn: String,
    val erAktiv: Boolean
) {
    companion object
}

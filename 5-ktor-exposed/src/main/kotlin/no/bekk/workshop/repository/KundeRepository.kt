package no.bekk.workshop.repository

import no.bekk.workshop.domain.Kunde

interface KundeRepository {
    suspend fun hent(id: Long): Kunde?
    suspend fun hentAlle(): List<Kunde>
    suspend fun lagre(kunde: Kunde): Long
    suspend fun oppdater(id: Long, erAktiv: Boolean): Boolean
}

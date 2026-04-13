package no.bekk.workshop.domain

import no.bekk.workshop.repository.KundeRepository
import no.bekk.workshop.repository.LagerRepository

class OrdreValidering(
    private val kundeRepository: KundeRepository,
    private val lagerRepository: LagerRepository
) {
    companion object {
        const val MINIMUM_ORDRE_TOTAL = 100.0
    }

    suspend fun valider(ordre: Ordre): ValideringsResultat {
        // 1. Sjekk at total er over minimum
        val total = ordre.totalBeløp()
        if (total < MINIMUM_ORDRE_TOTAL) {
            return ValideringsResultat.Ugyldig.TotalForLav(total, MINIMUM_ORDRE_TOTAL)
        }

        // 2. Hent kunde (nullable, ikke Optional!)
        val kunde = kundeRepository.hent(ordre.kundeId)

        // 3. Sjekk at kunde eksisterer
        if (kunde == null) {
            return ValideringsResultat.Ugyldig.KundeIkkeFunnet(ordre.kundeId)
        }

        // 4. Sjekk at kunde er aktiv
        if (!kunde.erAktiv) {
            return ValideringsResultat.Ugyldig.KundeInaktiv(ordre.kundeId)
        }

        // 5. For hver vare: sjekk at det er nok på lager
        for (vare in ordre.varer) {
            val beholdning = lagerRepository.hentBeholdning(vare.produktId)
            if (beholdning <= 0) {
                return ValideringsResultat.Ugyldig.UtAvLager(vare.produktId)
            }
        }

        return ValideringsResultat.Gyldig
    }
}

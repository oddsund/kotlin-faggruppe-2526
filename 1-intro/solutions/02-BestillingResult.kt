package workshop.data

// Refaktorert: sealed interface + data object + data class for payload
sealed interface BestillingResultat

data object Venter : BestillingResultat
data object Feil : BestillingResultat
data class Fullfoert(val melding: String) : BestillingResultat

fun prosesserBestilling(beloep: Int): BestillingResultat =
    when {
        beloep <= 0 -> Feil
        beloep < 100 -> Venter
        else -> Fullfoert("beloep of $beloep ok!")
    }

fun formaterBestillingResultat(r: BestillingResultat): String = when (r) {
    Venter -> "VENTER:kø"
    Feil -> "FEIL:ugyldig beloep"
    is Fullfoert -> "FULLFOERT:${r.melding}"
}

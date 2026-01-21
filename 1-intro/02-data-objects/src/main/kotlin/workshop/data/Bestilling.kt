package workshop.data

// LEGACY: enum + payload-style data class. Hva om vi
enum class BestillingStatus { VENTER, FULLFOERT, FEIL }

data class BestillingResultat(val status: BestillingStatus, val melding: String?)

fun prosesserBestilling(beloep: Int): BestillingResultat {
    return when {
        beloep <= 0 -> BestillingResultat(BestillingStatus.FEIL, "ugyldig beloep")
        beloep < 100 -> BestillingResultat(BestillingStatus.VENTER, "kø")
        else -> BestillingResultat(BestillingStatus.FULLFOERT, "beloep of $beloep ok!")
    }
}

fun formaterBestillingResultat(r: BestillingResultat): String = "${r.status}:${r.melding ?: "" }"

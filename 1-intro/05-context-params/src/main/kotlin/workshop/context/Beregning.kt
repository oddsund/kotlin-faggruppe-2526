package workshop.context

// I dette eksemplet er det en wrapper for at testene ikke trengs å oppdateres.
// Hvis dere oppdaterer testen til å wrappe beregn med with(logger) { ... }, så trengs
// ikke denne
fun beregn(brukerId: Int, logger: Logger): Bestilling {
    return with(logger) {
        beregnMedContext(brukerId)
    }
}

context(logger: Logger)
fun beregnMedContext(brukerId: Int): Bestilling {
    logger.info("beregn start:$brukerId")
    val bruker = behandleBruker(brukerId)
    val bestilling = hentBestilling(bruker)
    logger.info("beregn slutt:$brukerId")
    return bestilling
}

context(logger: Logger)
fun behandleBruker(brukerId: Int): Bruker {
    logger.info("behandleBruker:$brukerId")
    return Bruker(brukerId)
}

// Merk at her prop-driller vi enkelt og greit ved å beholde contexten
context(_: Logger)
fun hentBestilling(bruker: Bruker): Bestilling {
    val bestilling = Bestilling(42, bruker.id)
    return validerBestilling(bestilling)
}

context(logger: Logger)
fun validerBestilling(bestilling: Bestilling): Bestilling {
    logger.info("validerBestilling:${bestilling.id}")
    return bestilling
}


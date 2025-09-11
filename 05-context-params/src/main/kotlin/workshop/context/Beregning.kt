package workshop.context

fun beregn(brukerId: Int, logger: Logger): Bestilling {
    logger.info("beregn start:$brukerId")
    val bruker = behandleBruker(brukerId, logger)
    val bestilling = hentBestilling(bruker, logger)
    logger.info("beregn slutt:$brukerId")
    return bestilling
}

fun behandleBruker(brukerId: Int, logger: Logger): Bruker {
    logger.info("behandleBruker:$brukerId")
    return Bruker(brukerId)
}

fun hentBestilling(bruker: Bruker, logger: Logger): Bestilling {
    val bestilling = Bestilling(42, bruker.id)
    return validerBestilling(bestilling, logger)
}

fun validerBestilling(bestilling: Bestilling, logger: Logger): Bestilling {
    logger.info("validerBestilling:${bestilling.id}")
    return bestilling
}
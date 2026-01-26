package no.bekk.workshop

import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import no.bekk.workshop.plugins.configureRouting
import no.bekk.workshop.plugins.configureSerialization

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()

    // Opprett produksjons-dependencies via composition root
    val deps = AppFactory.createProductionApp()

    configureRouting(deps.ordreValidering, deps.kundeRepository)
}

package com.example.ordre.ws2.domain.port.løsning

import com.example.ordre.ws2.domain.model.løsning.Kunde

interface KundePort {
    fun hentKunde(kundeId: Long): Kunde?
}

package com.example.ordre.ws2.domain.port.løsning

import com.example.ordre.ws2.domain.model.løsning.ProduktLager

interface ProduktLagerPort {
    fun hentLagerStatus(produktId: String): ProduktLager?
}

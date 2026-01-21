package workshop.context

import kotlin.test.Test
import kotlin.test.assertEquals

class BeregningTest {
    @Test
    fun `logger innhold sjekkes`() {
        val logger = Logger.Default()
        val bestilling = beregn(1, logger)

        assertEquals(42, bestilling.id)
        assertEquals(
            listOf(
                "beregn start:1",
                "behandleBruker:1",
                "validerBestilling:42",
                "beregn slutt:1"
            ),
            logger.meldinger
        )
    }
}



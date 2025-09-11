package workshop.data

import kotlin.test.Test
import kotlin.test.assertEquals

class BestillingTest {
    @Test
    fun `Lite beløp venter`() {
        assertEquals("VENTER:kø", formaterBestillingResultat(prosesserBestilling(10)))
    }

    @Test
    fun `0 beløp feiler`() {
        assertEquals("FEIL:ugyldig beloep", formaterBestillingResultat(prosesserBestilling(0)))
    }

    @Test
    fun `Stort beløp er fullført`() {
        assertEquals("FULLFOERT:beloep of 150 ok!", formaterBestillingResultat(prosesserBestilling(150)))
    }
}

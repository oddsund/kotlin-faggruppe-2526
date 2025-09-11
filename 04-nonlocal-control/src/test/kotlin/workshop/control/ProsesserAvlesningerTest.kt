package workshop.control

import kotlin.test.Test
import kotlin.test.assertEquals

class ProsesserAvlesningerTest {
    @Test
    fun `hopper over markerte avlesninger`() {
        val input = listOf(
            Avlesning("s1", 12.3),
            Avlesning("s2", 9.8, hoppOver = true),
            Avlesning("s3", 15.0)
        )

        val resultat = prosesserAvlesninger(input)
        assertEquals(
            listOf("avlesning:s1:12.3", "avlesning:s3:15.0"),
            resultat
        )
    }

    @Test
    fun `prosesserer alle hvis ingen markert`() {
        val input = listOf(
            Avlesning("s1", 1.0),
            Avlesning("s2", 2.0)
        )

        val resultat = prosesserAvlesninger(input)
        assertEquals(
            listOf("avlesning:s1:1.0", "avlesning:s2:2.0"),
            resultat
        )
    }

    @Test
    fun `returnerer tom liste hvis alle markert`() {
        val input = listOf(
            Avlesning("s1", 0.0, hoppOver = true),
            Avlesning("s2", 0.0, hoppOver = true)
        )

        val resultat = prosesserAvlesninger(input)
        assertEquals(emptyList(), resultat)
    }
}

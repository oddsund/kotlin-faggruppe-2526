package workshop.control

fun prosesserAvlesninger(avlesninger: List<Avlesning>): List<String> {
    val prosessert = mutableListOf<String>()

    for (avlesning in avlesninger) {
        avlesning.let {
            // Kun koden inni let'en her skal endres
            if (!it.hoppOver) {
                prosessert += "avlesning:${avlesning.id}:${avlesning.verdi}"
            }
        }
    }

    return prosessert
}

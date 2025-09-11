package workshop.context

interface Logger {
    fun info(melding: String)
    class Default : Logger {
        val meldinger = mutableListOf<String>()
        override fun info(melding: String) {
            meldinger += melding
        }
    }
}
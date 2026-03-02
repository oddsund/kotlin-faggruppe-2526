package reisesok.util

private val startTid = System.currentTimeMillis()

fun log(melding: String) {
    val relativTid = System.currentTimeMillis() - startTid
    val tråd = Thread.currentThread().name
    println("[+${relativTid}ms] [$tråd] $melding")
}

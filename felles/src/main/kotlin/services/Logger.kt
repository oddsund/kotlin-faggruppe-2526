package services

interface Logger {
    fun info(message: String, vararg args: Any?)
    fun warn(message: String, vararg args: Any?)
    fun error(message: String, throwable: Throwable? = null, vararg args: Any?)
    fun debug(message: String, vararg args: Any?)
}

class InMemoryLogger : Logger {
    val logs = mutableListOf<LogEntry>()

    override fun info(message: String, vararg args: Any?) {
        logs.add(LogEntry(LogLevel.INFO, format(message, args)))
    }

    override fun warn(message: String, vararg args: Any?) {
        logs.add(LogEntry(LogLevel.WARN, format(message, args)))
    }

    override fun error(message: String, throwable: Throwable?, vararg args: Any?) {
        logs.add(LogEntry(LogLevel.ERROR, format(message, args), throwable))
    }

    override fun debug(message: String, vararg args: Any?) {
        logs.add(LogEntry(LogLevel.DEBUG, format(message, args)))
    }

    private fun format(message: String, args: Array<out Any?>): String {
        var result = message
        args.forEach { arg ->
            result = result.replaceFirst("{}", arg.toString())
        }
        return result
    }

    fun clear() = logs.clear()
}

data class LogEntry(
    val level: LogLevel,
    val message: String,
    val throwable: Throwable? = null,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LogLevel {
    DEBUG, INFO, WARN, ERROR
}
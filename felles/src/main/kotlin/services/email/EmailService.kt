package services.email

import services.email.BatchResult

interface EmailService {
    fun send(email: Email): Boolean
    fun sendBatch(emails: List<Email>): BatchResult
}
import services.email.EmailService
import domain.Order
import domain.OrderItem
import domain.OrderStatus
import domain.Product
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import services.Logger
import services.notification.NotificationService
import services.order.OrderRepository

/*
 * MockK - Mocking-bibliotek for Kotlin
 * Dokumentasjon: https://mockk.io/
 *
 * Viktige funksjoner du vil trenge:
 * - mockk<Type>(): Oppretter en mock av en type
 * - mockk<Type>(relaxed = true): Oppretter en relaxed mock som returnerer default-verdier
 * - spyk(objekt): Oppretter en spy som wrapper et ekte objekt
 * - every { mock.metode(args) } returns verdi: Setter opp hva en mock skal returnere
 * - every { mock.metode(args) } throws exception: Setter opp at en mock skal kaste exception
 * - verify(exactly = N) { mock.metode(args) }: Verifiserer at en metode ble kalt N ganger
 * - verify { mock.metode(match { predicate }) }: Verifiserer med custom matching
 * - verifyOrder { ... }: Verifiserer at kall skjedde i en spesifikk rekkefølge
 * - any(): Matcher alle argumenter av en type
 */
class NotificationServiceTest {
    
    private lateinit var emailService: EmailService
    private lateinit var orderRepository: OrderRepository
    private lateinit var logger: Logger
    private lateinit var notificationService: NotificationService
    
    private val testOrder = Order(
        id = "ORDER-123",
        customerId = "CUST-1",
        customerEmail = "customer@example.com",
        items = listOf(
            OrderItem(
                Product("PROD-1", "Test Product", 100),
                quantity = 2
            )
        ),
        status = OrderStatus.PENDING
    )
    
    @BeforeEach
    fun setup() {
        // TODO: Opprett mock av EmailService
        // TODO: Opprett ekte implementasjoner av OrderRepository og Logger
        // TODO: Initialiser NotificationService med avhengighetene
        // TODO: Lagre testOrder i repository
    }
    
    @Test
    fun `skal sende e-post ved ordre-bekreftelse`() {
        // TODO: Sett opp EmailService mock til å returnere true
        
        // TODO: Kall notifyOrderConfirmed
        
        // TODO: Verifiser at emailService.send ble kalt én gang
        // TODO: Verifiser at e-posten inneholder riktig recipient
        // TODO: Verifiser at e-posten inneholder ordre-ID i subject
    }
    
    @Test
    fun `skal oppdatere ordre-status til CONFIRMED etter vellykket e-post`() {
        // TODO: Sett opp EmailService mock
        
        // TODO: Kall notifyOrderConfirmed
        
        // TODO: Hent ordre fra repository
        // TODO: Verifiser at status er CONFIRMED
    }
    
    @Test
    fun `skal logge info når ordre-bekreftelse sendes`() {
        // TODO: Sett opp EmailService mock
        
        // TODO: Kall notifyOrderConfirmed
        
        // TODO: Verifiser at logger inneholder info-melding om "confirmed"
    }
    
    @Test
    fun `skal returnere false og ikke oppdatere status hvis e-post feiler`() {
        // TODO: Sett opp EmailService mock til å returnere false
        
        // TODO: Kall notifyOrderConfirmed
        
        // TODO: Verifiser at resultatet er false
        // TODO: Verifiser at ordre-status fortsatt er PENDING
        // TODO: Verifiser at error ble logget
    }
    
    @Test
    fun `skal håndtere exception fra EmailService`() {
        // TODO: Sett opp EmailService mock til å kaste exception
        
        // TODO: Kall notifyOrderConfirmed
        
        // TODO: Verifiser at resultatet er false
        // TODO: Verifiser at exception ble logget med error-nivå
    }
    
    @Test
    fun `skal returnere false hvis ordre ikke finnes`() {
        // TODO: Kall notifyOrderConfirmed med ugyldig ordre-ID
        
        // TODO: Verifiser at resultatet er false
        // TODO: Verifiser at emailService.send IKKE ble kalt
        // TODO: Verifiser at error ble logget
    }
    
    @Test
    fun `skal ikke sende e-post hvis ordre ikke er i PENDING status`() {
        // TODO: Oppdater ordre til CONFIRMED status i repository
        
        // TODO: Kall notifyOrderConfirmed
        
        // TODO: Verifiser at resultatet er false
        // TODO: Verifiser at emailService.send IKKE ble kalt
        // TODO: Verifiser at warning ble logget
    }
    
    @Test
    fun `skal sende kansellerings-e-post med årsak`() {
        // TODO: Sett opp EmailService mock
        
        // TODO: Kall notifyOrderCancelled med årsak
        
        // TODO: Verifiser at emailService.send ble kalt
        // TODO: Verifiser at e-post body inneholder årsaken
        // TODO: Verifiser at ordre-status er CANCELLED
    }
    
    @Test
    fun `skal sende batch-notifikasjoner`() {
        // TODO: Opprett flere ordre i repository
        // TODO: Sett opp EmailService.sendBatch mock
        
        // TODO: Kall notifyBatchOrders
        
        // TODO: Verifiser at sendBatch ble kalt med riktig antall e-poster
        // TODO: Verifiser logg-meldinger
    }
    
    @Test
    fun `skal verifisere rekkefølge på kall til logger`() {
        // TODO: Sett opp EmailService mock, og lag ny logger ved hjelp av spyk
        
        // TODO: Kall notifyOrderConfirmed
        
        // TODO: Bruk verifyOrder for å sjekke at:
        // 1. Info logges først (processing)
        // 2. Deretter logges suksess
    }

    @Test
    fun `skal bruke relaxed mock for å teste kun det som er viktig`() {
        // TODO: Sett opp relaxed EmailService mock og lag ny NotificationService

        // TODO: Kall notifyOrderConfirmed

        // TODO: Verifiser at emailService ble kalt
    }
}
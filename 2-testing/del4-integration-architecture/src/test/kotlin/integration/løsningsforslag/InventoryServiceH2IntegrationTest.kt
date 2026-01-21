package integration.løsningsforslag
import assertk.assertThat
import assertk.assertions.isEqualTo
import infra.DatabaseFactory
import org.junit.jupiter.api.*
import services.inventory.InventoryService
import services.inventory.JdbcInventoryService

class InventoryServiceH2IntegrationTest {
    
    private lateinit var inventoryService: InventoryService
    
    @BeforeEach
    fun setup() {
        DatabaseFactory.init()
        inventoryService = JdbcInventoryService()
        DatabaseFactory.reset()
        
        // Setup test data
        inventoryService.addInventory("LAPTOP-1", 100)
        inventoryService.addInventory("MOUSE-1", 200)
    }
    
    @Test
    fun `skal reservere inventory korrekt`() {
        // Act
        inventoryService.reserve("LAPTOP-1", 10)
        
        // Assert
        val inventory = inventoryService.getInventory("LAPTOP-1")!!
        assertThat(inventory.availableQuantity).isEqualTo(90)
        assertThat(inventory.reservedQuantity).isEqualTo(10)
    }
    
    @Test
    fun `skal kaste exception ved forsøk på å reservere mer enn tilgjengelig`() {
        // Act & Assert
        assertThrows<IllegalStateException> {
            inventoryService.reserve("LAPTOP-1", 150)
        }
        
        // Verifiser at inventory ikke ble endret
        val inventory = inventoryService.getInventory("LAPTOP-1")!!
        assertThat(inventory.availableQuantity).isEqualTo(100)
        assertThat(inventory.reservedQuantity).isEqualTo(0)
    }
    
    @Test
    fun `skal bekrefte reservasjon korrekt`() {
        // Arrange
        inventoryService.reserve("LAPTOP-1", 10)
        
        // Act
        inventoryService.confirm("LAPTOP-1", 10)
        
        // Assert
        val inventory = inventoryService.getInventory("LAPTOP-1")!!
        assertThat(inventory.availableQuantity).isEqualTo(90)
        assertThat(inventory.reservedQuantity).isEqualTo(0)
    }
    
    @Test
    fun `skal frigi reservasjon korrekt`() {
        // Arrange
        inventoryService.reserve("LAPTOP-1", 10)
        
        // Act
        inventoryService.release("LAPTOP-1", 10)
        
        // Assert
        val inventory = inventoryService.getInventory("LAPTOP-1")!!
        assertThat(inventory.availableQuantity).isEqualTo(100)
        assertThat(inventory.reservedQuantity).isEqualTo(0)
    }
    
    @Test
    fun `skal håndtere flere samtidige reservasjoner`() {
        // Act
        inventoryService.reserve("LAPTOP-1", 10)
        inventoryService.reserve("LAPTOP-1", 20)
        inventoryService.reserve("LAPTOP-1", 30)
        
        // Assert
        val inventory = inventoryService.getInventory("LAPTOP-1")!!
        assertThat(inventory.availableQuantity).isEqualTo(40)
        assertThat(inventory.reservedQuantity).isEqualTo(60)
    }
}
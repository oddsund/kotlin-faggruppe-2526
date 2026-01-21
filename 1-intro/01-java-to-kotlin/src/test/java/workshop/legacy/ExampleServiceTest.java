package workshop.legacy;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ExampleServiceTest {

    @Test
    void findUserById_returnsUser() {
        ExampleService service = new ExampleService();
        ExampleService.User user = service.findUserById("42");

        assertNotNull(user);
        assertEquals("42", user.getId());
        assertEquals("TestUser", user.getName());
    }

    @Test
    void findUserById_nullId_returnsNull() {
        ExampleService service = new ExampleService();
        ExampleService.User user = service.findUserById(null);

        assertNull(user);
    }
}

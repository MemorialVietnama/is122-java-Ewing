import org.example.atm_maven_jfx.Database.DatabaseService;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class FirstAuthTest {

    @Test
    public void testAuthenticateUserSuccess() {
        boolean result = DatabaseService.authenticateUser("1", "1");
        assertTrue(result, "Authentication should succeed with correct credentials");
    }

    @Test
    public void testAuthenticateUserFailure() {
        boolean result = DatabaseService.authenticateUser("2", "2");
        assertFalse(result, "Authentication should fail with incorrect credentials");
    }

}
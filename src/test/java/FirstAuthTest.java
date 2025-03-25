import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.AdminSrc.AdminMenu;
import org.example.atm_maven_jfx.AdminSrc.FirstAuth;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class FirstAuthTest {

    @Test
    public void testAuthenticateUserSuccess() {
        boolean result = DatabaseService.authenticateUser("1", "1");; // Depends on real DatabaseService
        assertTrue(result, "Authentication should succeed with correct credentials");
    }

    @Test
    public void testAuthenticateUserFailure() {
        boolean result = DatabaseService.authenticateUser("2", "2");
        assertFalse(result, "Authentication should fail with incorrect credentials");
    }

}
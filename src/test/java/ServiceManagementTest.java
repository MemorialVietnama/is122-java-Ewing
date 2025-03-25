/* import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableView;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.atm_maven_jfx.AdminSrc.Window.Service.ServiceManagement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceManagementTest {

    private ServiceManagement serviceManagement;
    private ObservableList<ServiceManagement.Service> services;

    @BeforeEach
    void setUp() {
        // Инициализация объектов перед каждым тестом
        Stage dummyStage = new Stage();
        Scene dummyScene = new Scene(new VBox(), 100, 100);
        serviceManagement = new ServiceManagement(dummyStage, dummyScene);
        services = FXCollections.observableArrayList();

        // Привязываем services к tableView через рефлексию, если нужно
        try {
            java.lang.reflect.Field servicesField = ServiceManagement.class.getDeclaredField("services");
            servicesField.setAccessible(true);
            servicesField.set(serviceManagement, services);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException("Не удалось инициализировать services", e);
        }
    }

    @Test
    void testLoadServicesFromDatabase_Success() throws SQLException {
        TableView<ServiceManagement.Service> tableView = new TableView<>();
        List<ServiceManagement.Service> stubServices = List.of(
                new ServiceManagement.Service(1L, "Service1", true),
                new ServiceManagement.Service(2L, "Service2", false)
        );
        services.clear();
        services.addAll(stubServices);

        serviceManagement.loadServicesFromDatabase(tableView);

        assertEquals(2, tableView.getItems().size());
        assertEquals("Service1", tableView.getItems().get(0).getName());
        assertTrue(tableView.getItems().get(0).isActiveStatus());
    }

    @Test
    void testAddService_NewService() throws SQLException {
        TableView<ServiceManagement.Service> tableView = new TableView<>();
        tableView.setItems(services);
        ServiceManagement.Service newService = new ServiceManagement.Service(3L, "NewService", true);
        services.add(newService);

        serviceManagement.addService(tableView);

        assertTrue(services.stream().anyMatch(s -> s.getName().equals("NewService")));
        assertEquals(1, services.size());
    }

    @Test
    void testDeleteSelectedServices() {
        TableView<ServiceManagement.Service> tableView = new TableView<>();
        services.addAll(
                new ServiceManagement.Service(1L, "Service1", true),
                new ServiceManagement.Service(2L, "Service2", false)
        );
        tableView.setItems(services);
        services.get(0).selectedProperty().set(true);

        serviceManagement.deleteSelectedServices(tableView);

        assertEquals(1, services.size());
        assertEquals("Service2", services.get(0).getName());
    }

    @Test
    void testSaveConfiguration_NoChanges() throws SQLException {
        services.add(new ServiceManagement.Service(1L, "Service1", true));
        // Здесь должен быть вызов заглушки для DatabaseService.getActiveServices(),
        // но без моков мы просто проверяем логику пропуска
        serviceManagement.saveConfiguration();

        assertEquals(1, services.size());
        assertTrue(services.get(0).isActiveStatus());
    }

    @Test
    void testServiceProperties() {
        ServiceManagement.Service service = new ServiceManagement.Service(1L, "TestService", true);

        assertEquals(1L, service.getId());
        assertEquals("TestService", service.getName());
        assertTrue(service.isActiveStatus());
        assertFalse(service.isSelected());

        service.activeStatusProperty().set(false);
        service.selectedProperty().set(true);

        assertFalse(service.isActiveStatus());
        assertTrue(service.isSelected());
    }
}

 */
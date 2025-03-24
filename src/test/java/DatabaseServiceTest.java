import javafx.collections.ObservableList;
import org.example.atm_maven_jfx.AdminSrc.Window.Service.ServiceManagement;
import org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.Incantations.CashStorage;
import org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.RemoveMoneyAction;
import org.example.atm_maven_jfx.Database.DatabaseService;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit.DepositOperation;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney.MoneyWithdrawalScene;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.TransactionHistoryMenu;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class DatabaseServiceTest {

    private Connection connection;


    @Test
    public void testGetConnection() throws SQLException {
        assertNotNull("Connection must not be null", DatabaseService.getConnection());
    }

    @Test
    public void testAuthenticateUser() {
        assertTrue("Authentication should pass", DatabaseService.authenticateUser("admin", "admin"));
        assertFalse("Authentication should fail", DatabaseService.authenticateUser("wrongUser", "wrongPass"));
    }

    @Test
    public void testLoadCashStorageData() throws SQLException {
        List<CashStorage> cashList = DatabaseService.loadCashStorageData();
        assertNotNull("List must not be null", cashList);
        assertFalse("List must not be empty", cashList.isEmpty());
    }

    @Test
    public void testCalculateTotalAmount() throws SQLException {
        int total = DatabaseService.calculateTotalAmount();
        assertTrue("Total must be >= 0", total >= 0);
    }

    @Test
    public void testUpdateCurrentAmount() throws SQLException {
        DatabaseService.updateCurrentAmount(2000, "123456");
        String balance = DatabaseService.loadAtmBalance();
        assertTrue("Balance should update", balance.contains("2000"));
    }

    @Test
    public void testLoadAtmBalance() throws SQLException {
        String balance = DatabaseService.loadAtmBalance();
        assertNotNull("Balance must not be null", balance);
        assertTrue("Balance must contain ID_ATM", balance.contains("123456"));
    }

    @Test
    public void testGetMaxCashId() throws SQLException {
        String maxId = DatabaseService.getMaxCashId();
        assertNotNull("Max ID must not be null", maxId);
    }

    @Test
    public void testGenerateNextCashId() throws SQLException {
        String nextId = DatabaseService.generateNextCashId();
        assertTrue("ID must start with CS", nextId.startsWith("CS"));
    }

    @Test
    public void testInsertCashIntoDatabase() throws SQLException {
        int initialSize = DatabaseService.loadCashStorageData().size();
        DatabaseService.insertCashIntoDatabase(1, 100); // Adjust params if method signature differs
        int newSize = DatabaseService.loadCashStorageData().size();
        assertEquals("One bill should be added", initialSize + 1, newSize);
    }

    @Test
    public void testGenerateSerialNumber() {
        String serial = DatabaseService.generateSerialNumber();
        assertTrue("Serial must start with SN", serial.startsWith("SN"));
        assertEquals("Serial length must be 15", 15, serial.length());
    }

    @Test
    public void testGetCurrentCashCount() throws SQLException {
        Map<Integer, Integer> cashCount = DatabaseService.getCurrentCashCount();
        assertNotNull("Cash count must not be null", cashCount);
        assertFalse("Cash count must not be empty", cashCount.isEmpty());
    }


    @Test
    public void testGetCashStorageData() throws SQLException {
        List<MoneyWithdrawalScene.CashStorage> cashList = DatabaseService.getCashStorageData();
        assertNotNull("List must not be null", cashList);
        assertFalse("List must not be empty", cashList.isEmpty());
    }

    @Test
    public void testDeleteIssuedBills() throws SQLException {
        List<MoneyWithdrawalScene.CashStorage> bills = DatabaseService.getCashStorageData();
        int initialSize = bills.size();
        DatabaseService.deleteIssuedBills(bills.subList(0, 1));
        int newSize = DatabaseService.getCashStorageData().size();
        assertEquals("One bill should be removed", initialSize - 1, newSize);
    }

    @Test
    public void testUpdateCardBalance() throws SQLException {
        DatabaseService.updateCardBalance("1234567890123456", 500.0);
        double balance = DatabaseService.getCardBalance("1234567890123456");
        assertEquals("Balance should be 500", 500.0, balance, 0.01);
    }

    @Test
    public void testCheckCardInDatabase() throws SQLException {
        assertTrue("Card should exist", DatabaseService.checkCardInDatabase("1234567890123456"));
        assertFalse("Card should not exist", DatabaseService.checkCardInDatabase("0000000000000000"));
    }

    @Test
    public void testCheckPinCode() throws SQLException {
        assertTrue("Pin should be valid", DatabaseService.checkPinCode("1234567890123456", "1234"));
        assertFalse("Pin should be invalid", DatabaseService.checkPinCode("1234567890123456", "0000"));
    }

    @Test
    public void testLoadServices() throws SQLException {
        List<ServiceManagement.Service> services = DatabaseService.loadServices();
        assertNotNull("Service list must not be null", services);
        assertFalse("Service list must not be empty", services.isEmpty());
    }



    @Test
    public void testGetLastInsertedId() throws SQLException {
        long id = DatabaseService.getLastInsertedId();
        assertTrue("ID must be positive", id > 0);
    }

    @Test
    public void testGetActiveServices() throws SQLException {
        List<String> services = DatabaseService.getActiveServices();
        assertNotNull("Active services list must not be null", services);
        assertFalse("Active services list must not be empty", services.isEmpty());
    }

    @Test
    public void testLogTransaction() {
        assertTrue("Transaction should be logged", DatabaseService.logTransaction("1234567890123456", "TestService", "100"));
    }

    @Test
    public void testUpdateBalance() {
        assertTrue("Balance should update", DatabaseService.updateBalance("1234567890123456", 1500));
        double balance = DatabaseService.getCardBalance("1234567890123456");
        assertEquals("Balance should be 1500", 1500.0, balance, 0.01);
    }


    @Test
    public void testDoesNominalExist() throws SQLException {
        try (Connection conn = DatabaseService.getConnection()) {
            assertTrue("Nominal should exist", DatabaseService.doesNominalExist("100", conn));
            assertFalse("Nominal should not exist", DatabaseService.doesNominalExist("999", conn));
        }
    }

    @Test
    public void testGenerateUniqueId() {
        String id = DatabaseService.generateUniqueId();
        assertTrue("ID must start with CS", id.startsWith("CS"));
    }

    @Test
    public void testLogOperation() {
        DatabaseService.logOperation("1234567890123456", "TestOperation", "TestComment");
        ObservableList<TransactionHistoryMenu.OperationInfo> history = DatabaseService.loadTransactionHistory("1234567890123456");
        assertFalse("Operation history must not be empty", history.isEmpty());
    }

    @Test
    public void testLoadTransactionHistory() {
        DatabaseService.logOperation("1234567890123456", "TestOperation", "TestComment");
        ObservableList<TransactionHistoryMenu.OperationInfo> history = DatabaseService.loadTransactionHistory("1234567890123456");
        assertNotNull("History must not be null", history);
        assertFalse("History must not be empty", history.isEmpty());
    }
}
package org.example.atm_jfx.Database;

import org.example.atm_jfx.AdminSrc.Windows.Incossations.Incossations.CashStorage;
import org.example.atm_jfx.AdminSrc.Windows.Incossations.RemoveMoneyAction;
import org.example.atm_jfx.Windows.MainMenu.SubClasses.OutPutMoney.MoneyWithdrawalScene;

import java.sql.*;
import java.util.*;

public class DatabaseService {

    // Константы подключения к базе данных
    private static final String JDBC_URL = "jdbc:firebirdsql:localhost/3050:C:/ATMV_MODEL_DBASE";
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "010802";

    // SQL-запросы
    private static final String AUTHENTICATE_USER_QUERY = "{call AUTHENTICATE_USER(?, ?)}";
    private static final String LOAD_CASH_STORAGE_QUERY = "SELECT ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER, DATE_INSERTED FROM ATM_CASH_STORAGE";
    private static final String CALCULATE_TOTAL_AMOUNT_QUERY = "SELECT SUM(CAST(DENOMINATIONS AS INTEGER)) AS TOTAL_AMOUNT FROM ATM_CASH_STORAGE";
    private static final String UPDATE_CURRENT_AMOUNT_QUERY = "UPDATE ATM_BALANCE SET CURRENT_AMOUNT = ? WHERE ID_ATM = ?";
    private static final String LOAD_ATM_BALANCE_QUERY = "SELECT ID_ATM, MAX_AMOUNT, MIN_AMOUNT, CURRENT_AMOUNT FROM ATM_BALANCE";
    private static final String INSERT_CASH_QUERY = "INSERT INTO ATM_CASH_STORAGE (ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER) VALUES (?, ?, ?, ?)";

    // Метод для получения соединения с базой данных
    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Firebird JDBC driver not found", e);
        }
        return DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
    }

    // Метод для аутентификации пользователя
    public static boolean authenticateUser(String username, String password) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(AUTHENTICATE_USER_QUERY)) {

            pstmt.setString(1, username);
            pstmt.setString(2, password);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBoolean("AUTHENTICATED");
                }
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при аутентификации пользователя: " + e.getMessage());
        }
        return false;
    }

    // Метод для загрузки данных из таблицы ATM_CASH_STORAGE
    public static List<CashStorage> loadCashStorageData() throws SQLException {
        List<CashStorage> cashStorageList = new ArrayList<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(LOAD_CASH_STORAGE_QUERY)) {

            while (rs.next()) {
                CashStorage cashStorage = new CashStorage(
                        rs.getString("ID_CASH"),
                        rs.getString("ID_ATM"),
                        rs.getString("DENOMINATIONS"),
                        rs.getString("SERIAL_NUMBER"),
                        rs.getTimestamp("DATE_INSERTED")
                );
                cashStorageList.add(cashStorage);
            }
        }

        return cashStorageList;
    }

    public static int calculateTotalAmount() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(CALCULATE_TOTAL_AMOUNT_QUERY)) {

            if (rs.next()) {
                return rs.getInt("TOTAL_AMOUNT");
            }
        }
        return 0;
    }

    // Метод для обновления CURRENT_AMOUNT в таблице ATM_BALANCE
    public static void updateCurrentAmount(int totalAmount, String idAtm) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_CURRENT_AMOUNT_QUERY)) {

            pstmt.setInt(1, totalAmount);
            pstmt.setString(2, idAtm);
            pstmt.executeUpdate();
        }
    }

    // Метод для загрузки баланса банкомата
    public static String loadAtmBalance() throws SQLException {
        StringBuilder balanceInfo = new StringBuilder("Баланс банкомата:\n");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(LOAD_ATM_BALANCE_QUERY)) {

            while (rs.next()) {
                balanceInfo.append(String.format(
                        "ID: %s | Макс: %d | Мин: %d | Текущий: %d%n",
                        rs.getString("ID_ATM"),
                        rs.getInt("MAX_AMOUNT"),
                        rs.getInt("MIN_AMOUNT"),
                        rs.getInt("CURRENT_AMOUNT")
                ));
            }
        }

        return balanceInfo.toString();
    }

    // Метод для добавления денег в базу данных
    public static void insertCashIntoDatabase(int count, int denomination) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_CASH_QUERY)) {

            for (int i = 0; i < count; i++) {
                String idCash = generateUniqueId();
                String idAtm = "123456"; // Замените на реальный ID банкомата
                String serialNumber = generateSerialNumber();

                pstmt.setString(1, idCash);          // ID_CASH
                pstmt.setString(2, idAtm);          // ID_ATM
                pstmt.setInt(3, denomination);      // DENOMINATIONS (номинал)
                pstmt.setString(4, serialNumber);   // SERIAL_NUMBER
                pstmt.addBatch(); // Добавляем запрос в пакет
            }

            pstmt.executeBatch(); // Выполняем пакетную вставку
        }
    }

    // Генерация уникального ID
    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    // Генерация серийного номера
    private static String generateSerialNumber() {
        return "SN" + System.currentTimeMillis();
    }
    // Метод для получения текущего количества банкнот
    public static Map<Integer, Integer> getCurrentCashCount() throws SQLException {
        Map<Integer, Integer> cashCount = new HashMap<>();
        String query = "SELECT DN.NOMINAL AS Nominal, COUNT(ACS.ID_CASH) AS Quantity " +
                "FROM ATM_CASH_STORAGE ACS " +
                "JOIN DIC_NOMINAL DN ON ACS.DENOMINATIONS = DN.NOMINAL " +
                "GROUP BY DN.NOMINAL";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                int denomination = rs.getInt("Nominal");
                int count = rs.getInt("Quantity");
                cashCount.put(denomination, count);
            }
        }

        return cashCount;
    }
    public static List<RemoveMoneyAction.CashStorage> getCashToRemove(int amountToRemove) {
        List<RemoveMoneyAction.CashStorage> cashToRemove = new ArrayList<>();
        final String url = "jdbc:firebirdsql:localhost/3050:C:/ATMV_MODEL_DBASE";
        final String user = "SYSDBA";
        final String password = "010802";

        String query = "SELECT ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER, DATE_INSERTED " +
                "FROM ATM_CASH_STORAGE " +
                "ORDER BY DENOMINATIONS DESC, DATE_INSERTED ASC"; // Сортируем по номиналу и дате

        try (Connection conn = DriverManager.getConnection(url, user, password);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            int remainingAmount = amountToRemove;

            while (rs.next() && remainingAmount > 0) {
                String idCash = rs.getString("ID_CASH");
                String idAtm = rs.getString("ID_ATM");
                String denominations = rs.getString("DENOMINATIONS");
                String serialNumber = rs.getString("SERIAL_NUMBER");
                Timestamp dateInserted = rs.getTimestamp("DATE_INSERTED");

                int denomination = Integer.parseInt(denominations);

                if (denomination <= remainingAmount) {
                    cashToRemove.add(new RemoveMoneyAction.CashStorage(idCash, idAtm, denominations, serialNumber, dateInserted));
                    remainingAmount -= denomination;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return cashToRemove;
    }
    // Метод для удаления купюр из базы данных
    public static void removeCashFromDatabase(List<RemoveMoneyAction.CashStorage> cashToRemove) throws SQLException {
        final String DELETE_CASH_QUERY = "DELETE FROM ATM_CASH_STORAGE WHERE ID_CASH = ?";

        try (Connection conn = getConnection()) {
            for (RemoveMoneyAction.CashStorage cash : cashToRemove) {
                try (PreparedStatement pstmt = conn.prepareStatement(DELETE_CASH_QUERY)) {
                    pstmt.setString(1, cash.idCash());
                    pstmt.executeUpdate();
                }
            }
        }
    }



    public static List<MoneyWithdrawalScene.CashStorage> getCashStorageData() throws SQLException {
            List<MoneyWithdrawalScene.CashStorage> cashStorageList = new ArrayList<>();
            String query = "SELECT DENOMINATIONS, SERIAL_NUMBER FROM ATM_CASH_STORAGE";

            try (Connection connection = getConnection();
                 Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(query)) {

                while (resultSet.next()) {
                    String denomination = resultSet.getString("DENOMINATIONS");
                    String serialNumber = resultSet.getString("SERIAL_NUMBER");
                    cashStorageList.add(new MoneyWithdrawalScene.CashStorage(denomination, serialNumber));
                }
            }

            return cashStorageList;
        }
        public static void deleteIssuedBills(List<MoneyWithdrawalScene.CashStorage> issuedBills) throws SQLException {
            String query = "DELETE FROM ATM_CASH_STORAGE WHERE SERIAL_NUMBER = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                for (MoneyWithdrawalScene.CashStorage bill : issuedBills) {
                    statement.setString(1, bill.getSerialNumber());
                    statement.addBatch();
                }

                statement.executeBatch();
            }
        }

        // Метод для обновления баланса карты
        public static void updateCardBalance(String cardNumber, int amount) throws SQLException {
            String query = "UPDATE BALANCE_CARD SET BALANCE = BALANCE - ? WHERE FK_CARD = ?";

            try (Connection connection = getConnection();
                 PreparedStatement statement = connection.prepareStatement(query)) {

                statement.setInt(1, amount);
                statement.setString(2, cardNumber);

                int rowsUpdated = statement.executeUpdate();
                if (rowsUpdated == 0) {
                    throw new SQLException("Карта не найдена.");
                }
            }
        }

    }


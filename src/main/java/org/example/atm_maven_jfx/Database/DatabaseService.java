package org.example.atm_maven_jfx.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.atm_maven_jfx.AdminSrc.Window.Service.ServiceManagement;
import org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.Incantations.CashStorage;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit.DepositOperation;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.SettingsCardMenu;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.TransactionHistoryMenu;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class DatabaseService {
    private static final String DATABASE_PATH = "src/main/resources/org/example/atm_maven_jfx/Database/ATM_MODEL_DBASE.fdb";
    private static final String JDBC_URL = "jdbc:firebirdsql:localhost/3050:" + new File(DATABASE_PATH).getAbsolutePath();
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "010802";

    private static final String AUTHENTICATE_USER_QUERY = "{call AUTHENTICATE_USER(?, ?)}";
    private static final String LOAD_CASH_STORAGE_QUERY = "{call GET_ATM_CASH_STORAGE()}";
    private static final String CALCULATE_TOTAL_AMOUNT_QUERY = "{CALL GET_TOTAL_AMOUNT_IN_ATM()}";
    private static final String UPDATE_CURRENT_AMOUNT_QUERY = "{call UPDATE_CURRENT_AMOUNT(?,?)}";
    private static final String LOAD_ATM_BALANCE_QUERY = "{call LOAD_ATM_BALANCE()}";
    private static final String INSERT_CASH_QUERY = "{call INSERT_CASH(?,?,?,?)}";
    private static final String CHECK_CARD_IN_DATABASE = "{call CHECK_CARD_IN_DATABASE(?)}";
    private static final String GET_BALANCE_QUERY = "{call GET_BALANCE(?)}";
    private static final String UPDATE_BALANCE_QUERY = "{call UPDATE_BALANCE(?,?)}";
    private static final String CHECK_NOMINAL_EXISTS_QUERY = "{CALL CHECK_NOMINAL_EXISTS(?)}";
    private static final String GET_CLIENT_INFO_QUERY = "{CALL GET_CLIENT_INFO(?)}";
    private static final String LOG_OPERATION_QUERY = "{CALL LOG_OPERATION(?, ?, ?, ?)}";
    private static final String ADD_SERVICE_QUERY = "{CALL INSERT_SERVICE(?, ?)}";
    private static final String DELETE_SERVICE_QUERY = "{CALL DELETE_SERVICE_BY_NAME(?)}";
    private static final String UPDATE_SERVICE_STATUS_QUERY = "{CALL UPDATE_SERVICE_STATUS(?, ?)}";
    private static final String CHECK_SERVICE_EXISTS_QUERY = "{CALL COUNT_SERVICES_BY_NAME(?)}";
    private static final String GET_LAST_INSERTED_ID_QUERY = "{CALL GET_LAST_SERVICE_ID}";
    private static final String GET_ACTIVE_SERVICES_QUERY = "SELECT NAME_SERVICE FROM GET_ACTIVE_SERVICES";
    private static final String TRANSACTION_HISTORY_QUERY = "{CALL GET_CLIENT_OPERATIONS(?)}";
    private static final String GET_ID_MAX_ATM_CASH = "{CALL GET_MAX_ATM_CASH_ID()}";

    public static void main(String[] args) {
        System.out.println("DEBUG: Запуск метода main");
        File dbFile = new File(DATABASE_PATH);
        if (!dbFile.exists()) {
            System.out.println("DEBUG: Файл базы данных не найден: " + dbFile.getAbsolutePath());
            return;
        }

        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            System.out.println("DEBUG: Драйвер Firebird загружен");
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("DEBUG: Соединение установлено успешно!");
            connection.close();
            System.out.println("DEBUG: Соединение закрыто");
        } catch (Exception e) {
            System.err.println("DEBUG: Ошибка подключения: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        System.out.println("DEBUG: Вызов метода getConnection");
        try {
            Class.forName("org.firebirdsql.jdbc.FBDriver");
            System.out.println("DEBUG: Драйвер Firebird успешно загружен");
        } catch (ClassNotFoundException e) {
            System.out.println("DEBUG: Ошибка загрузки драйвера Firebird: " + e.getMessage());
            throw new SQLException("Firebird JDBC driver not found", e);
        }
        Connection conn = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
        System.out.println("DEBUG: Соединение с базой данных установлено");
        return conn;
    }

    public static boolean authenticateUser(String username, String password) {
        System.out.println("DEBUG: Вызов authenticateUser с username=" + username + ", password=" + password);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(AUTHENTICATE_USER_QUERY)) {
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    boolean result = rs.getBoolean("AUTHENTICATED");
                    System.out.println("DEBUG: Результат аутентификации: " + result);
                    return result;
                }
            }
        } catch (SQLException e) {
            System.err.println("DEBUG: Ошибка при аутентификации: " + e.getMessage());
        }
        System.out.println("DEBUG: Аутентификация не удалась, возвращаем false");
        return false;
    }

    public static List<CashStorage> loadCashStorageData() throws SQLException {
        System.out.println("DEBUG: Вызов loadCashStorageData");
        List<CashStorage> cashStorageList = new ArrayList<>();
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(LOAD_CASH_STORAGE_QUERY);
             ResultSet rs = stmt.executeQuery()) {
            int rowCount = 0;
            while (rs.next()) {
                CashStorage cashStorage = new CashStorage(
                        rs.getString("ID_CASH"),
                        rs.getString("ID_ATM"),
                        String.valueOf(rs.getFloat("DENOMINATIONS")),
                        rs.getString("SERIAL_NUMBER"),
                        rs.getTimestamp("DATE_INSERTED")
                );
                cashStorageList.add(cashStorage);
                rowCount++;
            }
            System.out.println("DEBUG: Загружено записей: " + rowCount);
        }
        return cashStorageList;
    }

    public static int calculateTotalAmount() throws SQLException {
        System.out.println("DEBUG: Вызов calculateTotalAmount");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(CALCULATE_TOTAL_AMOUNT_QUERY)) {
            if (rs.next()) {
                int total = rs.getInt("TOTAL_AMOUNT");
                System.out.println("DEBUG: Общая сумма: " + total);
                return total;
            }
        }
        System.out.println("DEBUG: Сумма не найдена, возвращаем 0");
        return 0;
    }

    public static void updateCurrentAmount(int totalAmount, String idAtm) throws SQLException {
        System.out.println("DEBUG: Вызов updateCurrentAmount с totalAmount=" + totalAmount + ", idAtm=" + idAtm);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_CURRENT_AMOUNT_QUERY)) {
            pstmt.setInt(1, totalAmount);
            pstmt.setString(2, idAtm);
            pstmt.executeUpdate();
            System.out.println("DEBUG: Баланс банкомата обновлён");
        }
    }

    public static String loadAtmBalance() throws SQLException {
        System.out.println("DEBUG: Вызов loadAtmBalance");
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
            System.out.println("DEBUG: Баланс банкомата: " + balanceInfo);
        }
        return balanceInfo.toString();
    }

    public static String getMaxCashId() throws SQLException {
        System.out.println("DEBUG: Вызов getMaxCashId");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_ID_MAX_ATM_CASH)) {
            if (rs.next()) {
                String maxId = rs.getString(1);
                System.out.println("DEBUG: Максимальный ID_CASH: " + maxId);
                return maxId;
            }
        }
        System.out.println("DEBUG: Максимальный ID_CASH не найден, возвращаем null");
        return null;
    }

    public static String generateNextCashId() throws SQLException {
        System.out.println("DEBUG: Вызов generateNextCashId");
        String maxId = getMaxCashId();
        if (maxId == null) {
            System.out.println("DEBUG: Таблица пуста, возвращаем CS1");
            return "CS1";
        }
        long numericPart = Long.parseLong(maxId.substring(2));
        String nextId = "CS" + (numericPart + 1);
        System.out.println("DEBUG: Сгенерирован новый ID_CASH: " + nextId);
        return nextId;
    }

    public static void insertCashIntoDatabase(int count, int denomination) throws SQLException {
        System.out.println("DEBUG: Вызов insertCashIntoDatabase с count=" + count + ", denomination=" + denomination);
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false); // Начало транзакции
            try (PreparedStatement pstmt = conn.prepareStatement(INSERT_CASH_QUERY)) {
                String maxId = getMaxCashId(); // Получаем максимальный ID один раз
                long numericPart = maxId == null ? 0 : Long.parseLong(maxId.substring(2));

                int batchSize = 100; // Размер пакета
                for (int i = 0; i < count; i++) {
                    numericPart++; // Увеличиваем для каждой купюры
                    String idCash = "CS" + numericPart;
                    String idAtm = "123456";
                    String serialNumber = generateSerialNumber();
                    pstmt.setString(1, idCash);
                    pstmt.setString(2, idAtm);
                    pstmt.setString(3, String.valueOf(denomination));
                    pstmt.setString(4, serialNumber);
                    pstmt.addBatch();

                    if (i % batchSize == 0 || i == count - 1) {
                        pstmt.executeBatch(); // Выполняем пакет
                        System.out.println("DEBUG: Выполнен пакет из " + batchSize + " записей");
                    }
                }
                conn.commit(); // Фиксация транзакции
                System.out.println("DEBUG: Купюры успешно добавлены");
            } catch (SQLException e) {
                conn.rollback(); // Откат при ошибке
                System.out.println("DEBUG: Ошибка, rollback выполнен");
                throw e;
            }
        }
    }
    public static String generateSerialNumber() {
        System.out.println("DEBUG: Вызов generateSerialNumber");
        String prefix = "SN";
        Random random = new Random();
        StringBuilder digits = new StringBuilder();
        for (int i = 0; i < 13; i++) {
            digits.append(random.nextInt(10));
        }
        String serial = prefix + digits;
        System.out.println("DEBUG: Сгенерирован серийный номер: " + serial);
        return serial;
    }

    public static Map<Integer, Integer> getCurrentCashCount(String atmId) throws SQLException {
        System.out.println("DEBUG: Вызов getCurrentCashCount для банкомата: " + atmId);
        Map<Integer, Integer> cashCount = new HashMap<>();

        // Запрос с фильтрацией по ID_ATM
        String query = """
        SELECT
            CAST(TRIM(DENOMINATIONS) AS INTEGER) AS Nominal,
            COUNT(*) AS Quantity
        FROM
            ATM_CASH_STORAGE
        WHERE
            ID_ATM = ?
        GROUP BY
            TRIM(DENOMINATIONS);
    """;

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            // Устанавливаем параметр ID_ATM
            stmt.setString(1, atmId);

            // Выполняем запрос
            try (ResultSet rs = stmt.executeQuery()) {
                // Проверка метаданных ResultSet
                ResultSetMetaData metaData = rs.getMetaData();
                int columnCount = metaData.getColumnCount();
                System.out.println("DEBUG: Количество столбцов в ResultSet: " + columnCount);

                // Логирование названий столбцов
                for (int i = 1; i <= columnCount; i++) {
                    System.out.println("DEBUG: Столбец " + i + ": " + metaData.getColumnName(i));
                }

                // Обработка строк ResultSet
                int rowCount = 0;
                while (rs.next()) {
                    rowCount++;
                    int denomination = rs.getInt("Nominal");
                    int count = rs.getInt("Quantity");

                    // Проверка на NULL
                    if (rs.wasNull()) {
                        System.out.println("DEBUG: Обнаружен NULL в строке " + rowCount);
                        continue; // Пропускаем эту строку
                    }

                    System.out.println("DEBUG: Обработана строка " + rowCount + ": Номинал = " + denomination + ", Количество = " + count);
                    cashCount.put(denomination, count);
                }

                System.out.println("DEBUG: Всего обработано строк: " + rowCount);
            }
        } catch (SQLException e) {
            System.out.println("DEBUG: Ошибка при выполнении запроса: " + e.getMessage());
            throw e; // Пробрасываем исключение дальше
        }

        return cashCount;
    }



    public static boolean checkCardInDatabase(String cardNumber) throws SQLException {
        System.out.println("DEBUG: Вызов checkCardInDatabase с cardNumber=" + cardNumber);
        try (Connection conn = getConnection();
             CallableStatement callableStatement = conn.prepareCall(CHECK_CARD_IN_DATABASE)) {
            callableStatement.setString(1, cardNumber);
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    int cardExists = resultSet.getInt("CARD_EXISTS");
                    System.out.println("DEBUG: Карта существует: " + (cardExists > 0));
                    return cardExists > 0;
                }
            }
        }
        System.out.println("DEBUG: Карта не найдена, возвращаем false");
        return false;
    }

    public static boolean checkPinCode(String cardNumber, String enteredPin) throws SQLException {
        System.out.println("DEBUG: Вызов checkPinCode с cardNumber=" + cardNumber + ", enteredPin=" + enteredPin);
        String query = "{CALL CHECK_PIN_CODE(?, ?)}";
        try (Connection conn = getConnection();
             CallableStatement callableStatement = conn.prepareCall(query)) {
            callableStatement.setString(1, cardNumber);
            callableStatement.setString(2, enteredPin);
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    int pinValid = resultSet.getInt("PIN_VALID");
                    System.out.println("DEBUG: Пин-код валиден: " + (pinValid == 1));
                    return pinValid == 1;
                }
            }
        }
        System.out.println("DEBUG: Пин-код неверный, возвращаем false");
        return false;
    }

    public static List<ServiceManagement.Service> loadServices() throws SQLException {
        System.out.println("DEBUG: Вызов loadServices");
        List<ServiceManagement.Service> services = new ArrayList<>();
        String query = "SELECT ID_SERVICE, NAME_SERVICE, ACTIVE_STATUS FROM SEVICE";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                long id = rs.getLong("ID_SERVICE");
                String name = rs.getString("NAME_SERVICE");
                boolean activeStatus = rs.getBoolean("ACTIVE_STATUS");
                services.add(new ServiceManagement.Service(id, name, activeStatus));
                System.out.println("DEBUG: Загружена услуга: ID=" + id + ", Name=" + name + ", ActiveStatus=" + activeStatus);
            }
        }
        return services;
    }

    public static boolean addService(String serviceName) throws SQLException {
        System.out.println("DEBUG: Вызов addService с serviceName=" + serviceName);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ADD_SERVICE_QUERY)) {
            pstmt.setString(1, serviceName);
            pstmt.setBoolean(2, true);
            int rows = pstmt.executeUpdate();
            System.out.println("DEBUG: Услуга добавлена, строк обновлено: " + rows);
            return rows > 0;
        }
    }

    public static boolean deleteService(String serviceName) throws SQLException {
        System.out.println("DEBUG: Вызов deleteService с serviceName=" + serviceName);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SERVICE_QUERY)) {
            pstmt.setString(1, serviceName);
            int rows = pstmt.executeUpdate();
            System.out.println("DEBUG: Услуга удалена, строк обновлено: " + rows);
            return rows > 0;
        }
    }

    public static boolean updateServiceStatus(long serviceId, boolean isActive) throws SQLException {
        System.out.println("DEBUG: Вызов updateServiceStatus с serviceId=" + serviceId + ", isActive=" + isActive);
        try (Connection conn = getConnection();
             CallableStatement stmt = conn.prepareCall(UPDATE_SERVICE_STATUS_QUERY)) {
            stmt.setBoolean(1, isActive); // Используем setBoolean вместо setString
            stmt.setLong(2, serviceId);
            int rows = stmt.executeUpdate();
            System.out.println("DEBUG: Статус услуги обновлён, строк обновлено: " + rows);
            return rows > 0;
        }
    }

    public static boolean isServiceExists(String serviceName) throws SQLException {
        System.out.println("DEBUG: Вызов isServiceExists с serviceName=" + serviceName);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(CHECK_SERVICE_EXISTS_QUERY)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                boolean exists = rs.getInt(1) > 0;
                System.out.println("DEBUG: Услуга существует: " + exists);
                return exists;
            }
        }
        System.out.println("DEBUG: Услуга не найдена, возвращаем false");
        return false;
    }

    public static long getLastInsertedId() throws SQLException {
        System.out.println("DEBUG: Вызов getLastInsertedId");
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_LAST_INSERTED_ID_QUERY)) {
            if (rs.next()) {
                long id = rs.getLong(1);
                System.out.println("DEBUG: Последний ID услуги: " + id);
                return id;
            }
        }
        System.out.println("DEBUG: ID не найден, выбрасываем исключение");
        throw new SQLException("Не удалось получить ID последней вставленной записи.");
    }

    public static List<String> getActiveServices() throws SQLException {
        System.out.println("DEBUG: Вызов getActiveServices");
        List<String> activeServices = new ArrayList<>();
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ACTIVE_SERVICES_QUERY);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String serviceName = rs.getString("NAME_SERVICE");
                activeServices.add(serviceName);
                System.out.println("DEBUG: Найдена активная услуга: " + serviceName);
            }
        }
        return activeServices;
    }

    public static double getCardBalance(String cardNumber) {
        System.out.println("DEBUG: Вызов getCardBalance с cardNumber=" + cardNumber);
        double balance = 0.0;
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BALANCE_QUERY)) {
            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                balance = resultSet.getDouble("BALANCE");
                System.out.println("DEBUG: Баланс карты: " + balance);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG: Ошибка при получении баланса: " + e.getMessage());
        }
        return balance;
    }

    public static boolean logTransaction(String cardNumber, String serviceName, String amount) {
        System.out.println("DEBUG: Вызов logTransaction с cardNumber=" + cardNumber + ", serviceName=" + serviceName + ", amount=" + amount);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(LOG_OPERATION_QUERY)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, "Оплата Услуги");
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, "Оплата услуги: " + serviceName + ", Сумма: " + amount);
            pstmt.executeUpdate();
            System.out.println("DEBUG: Транзакция успешно залогирована");
            return true;
        } catch (Exception e) {
            System.err.println("DEBUG: Ошибка при логировании транзакции: " + e.getMessage());
            return false;
        }
    }

    public static boolean updateBalance(String cardNumber, double amount) {
        System.out.println("DEBUG: Вызов updateBalance с cardNumber=" + cardNumber + ", amount=" + amount);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_BALANCE_QUERY)) {
            pstmt.setDouble(1, amount);
            pstmt.setString(2, cardNumber);
            int rows = pstmt.executeUpdate();
            System.out.println("DEBUG: Баланс обновлён, строк обновлено: " + rows);
            return true;
        } catch (Exception e) {
            System.err.println("DEBUG: Ошибка при обновлении баланса: " + e.getMessage());
            return false;
        }
    }

    public static boolean addBanknotesToStorage(List<DepositOperation.Denomination> banknotes) {
        System.out.println("DEBUG: Вызов addBanknotesToStorage с количеством банкнот=" + banknotes.size());
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_CASH_QUERY)) {
            for (DepositOperation.Denomination banknote : banknotes) {
                String denomination = banknote.demonition().replace(" руб.", "");
                if (!doesNominalExist(denomination, conn)) {
                    System.out.println("DEBUG: Номинал " + denomination + " не существует в DIC_NOMINAL");
                    continue;
                }
                String idCash = generateUniqueId();
                String atmId = "123456";
                String serialNumber = banknote.serias();
                pstmt.setString(1, idCash);
                pstmt.setString(2, atmId);
                pstmt.setString(3, denomination);
                pstmt.setString(4, serialNumber);
                pstmt.addBatch();
                System.out.println("DEBUG: Добавлена банкнота: " + idCash + ", " + serialNumber);
            }
            pstmt.executeBatch();
            System.out.println("DEBUG: Банкноты успешно добавлены в хранилище");
            return true;
        } catch (SQLException e) {
            System.err.println("DEBUG: Ошибка при добавлении банкнот: " + e.getMessage());
            return false;
        }
    }

    public static boolean doesNominalExist(String denomination, Connection conn) throws SQLException {
        System.out.println("DEBUG: Вызов doesNominalExist с denomination=" + denomination);
        if (denomination == null || denomination.isEmpty()) {
            System.out.println("DEBUG: Номинал null или пустой, выбрасываем исключение");
            throw new IllegalArgumentException("Номинал не может быть null или пустым.");
        }
        try (CallableStatement callableStmt = conn.prepareCall(CHECK_NOMINAL_EXISTS_QUERY)) {
            callableStmt.setString(1, denomination);
            try (ResultSet resultSet = callableStmt.executeQuery()) {
                if (resultSet.next()) {
                    int existsFlag = resultSet.getInt("EXISTS_FLAG");
                    System.out.println("DEBUG: Номинал существует: " + (existsFlag == 1));
                    return existsFlag == 1;
                }
            }
        }
        System.out.println("DEBUG: Номинал не найден, возвращаем false");
        return false;
    }

    public static String generateUniqueId() {
        System.out.println("DEBUG: Вызов generateUniqueId");
        String id = "CS" + System.currentTimeMillis();
        System.out.println("DEBUG: Сгенерирован уникальный ID: " + id);
        return id;
    }

    public static void logOperation(String cardNumber, String operation, String comment) {
        System.out.println("DEBUG: Вызов logOperation с cardNumber=" + cardNumber + ", operation=" + operation + ", comment=" + comment);
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(LOG_OPERATION_QUERY)) {
            pstmt.setString(1, cardNumber);
            pstmt.setString(2, operation);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, comment);
            pstmt.executeUpdate();
            System.out.println("DEBUG: Операция успешно залогирована");
        } catch (SQLException e) {
            System.err.println("DEBUG: Ошибка при логировании операции: " + e.getMessage());
        }
    }

    public static class DatabaseHelper {
        public static SettingsCardMenu.ClientInfo getClientInfo(String cardNumber) {
            System.out.println("DEBUG: Вызов getClientInfo с cardNumber=" + cardNumber);
            SettingsCardMenu.ClientInfo clientInfo = null;
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(GET_CLIENT_INFO_QUERY)) {
                pstmt.setString(1, cardNumber);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    clientInfo = new SettingsCardMenu.ClientInfo(
                            rs.getString("FULL_FIO"),
                            rs.getString("AGE"),
                            rs.getString("GENGER"),
                            rs.getDate("DATA_BIRTH").toString(),
                            rs.getString("PASSPORT"),
                            rs.getString("WHERE_PASSPORT"),
                            rs.getDate("DATE_PASSPORT").toString(),
                            rs.getString("SNILS"),
                            rs.getString("INN"),
                            rs.getString("STATUS")
                    );
                    System.out.println("DEBUG: Информация о клиенте загружена: " + clientInfo.fullFio());
                }
            } catch (Exception e) {
                System.err.println("DEBUG: Ошибка при получении информации о клиенте: " + e.getMessage());
                e.printStackTrace();
            }
            return clientInfo;
        }
    }

    public static ObservableList<TransactionHistoryMenu.OperationInfo> loadTransactionHistory(String cardNumber) {
        System.out.println("DEBUG: Вызов loadTransactionHistory с cardNumber=" + cardNumber);
        ObservableList<TransactionHistoryMenu.OperationInfo> operationData = FXCollections.observableArrayList();
        try (Connection connection = getConnection();
             PreparedStatement stmt = connection.prepareStatement(TRANSACTION_HISTORY_QUERY)) {
            stmt.setString(1, cardNumber);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                String cardNum = rs.getString("CARD_NUM");
                String operation = rs.getString("OPERATION");
                Timestamp ddateStamp = rs.getTimestamp("DDATE_STAMP");
                String dateOperation = ddateStamp != null ? ddateStamp.toString() : "N/A";
                String comment = rs.getString("COMMENT");
                operationData.add(new TransactionHistoryMenu.OperationInfo(cardNum, operation, dateOperation, comment));
                System.out.println("DEBUG: Загружена операция: " + operation + ", " + dateOperation);
            }
        } catch (SQLException e) {
            System.err.println("DEBUG: Ошибка при загрузке истории операций: " + e.getMessage());
            e.printStackTrace();
        }
        return operationData;
    }
}
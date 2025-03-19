package org.example.atm_maven_jfx.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.example.atm_maven_jfx.AdminSrc.Window.Service.ServiceManagement;
import org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.Incossations.CashStorage;
import org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.RemoveMoneyAction;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Deposit.DepositOperation;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.OutPutMoney.MoneyWithdrawalScene;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.SettingsCardMenu;
import org.example.atm_maven_jfx.Windows.MainMenu.SubClasses.Settings.TransactionHistoryMenu;

import java.io.File;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;


public class DatabaseService {
    // Константы подключения к базе данных
    private static final String DATABASE_PATH = "src/main/java/org/example/atm_maven_jfx/Database/ATM_MODEL_DBASE.fdb"; // Относительный путь с расширением
    private static final String JDBC_URL = "jdbc:firebirdsql:localhost/3050:" + new File(DATABASE_PATH).getAbsolutePath();
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "010802";
    /// sql

    // SQL-запросы
    private static final String AUTHENTICATE_USER_QUERY = "{call AUTHENTICATE_USER(?, ?)}";
    private static final String LOAD_CASH_STORAGE_QUERY = "{call LOAD_CASH_STORAGE(?,?,?,?,?)}";
    private static final String CALCULATE_TOTAL_AMOUNT_QUERY = "SELECT SUM(CAST(DENOMINATIONS AS INTEGER)) AS TOTAL_AMOUNT FROM ATM_CASH_STORAGE";
    private static final String UPDATE_CURRENT_AMOUNT_QUERY = "{call UPDATE_CURRENT_AMOUNT(?,?)}";
    private static final String LOAD_ATM_BALANCE_QUERY = "{call LOAD_ATM_BALANCE(?)}";
    private static final String INSERT_CASH_QUERY = "{call INSERT_CASH(?,?,?,?)}";
    private static final String CHECK_CARD_IN_DATABASE = "{call CHECK_CARD_IN_DATABASE(?)}";
    private static final String GET_BALANCE_QUERY = "{call GET_BALANCE(?)}";
    private static final String UPDATE_BALANCE_QUERY = "{call UPDATE_BALANCE(?,?)}";
    private static final String INSERT_CASH_STORAGE_QUERY = "{call INSERT_CASH_STORAGE(?,?,?,?)}";
    private static final String CHECK_NOMINAL_EXISTS_QUERY = "SELECT 1 FROM DIC_NOMINAL WHERE NOMINAL = ?";
    private static final String GET_CLIENT_INFO_QUERY = "SELECT * FROM CLIENT_INFO ci JOIN CLIENT_CARD cc ON ci.FULL_FIO = cc.FK_CLIENT WHERE cc.NUMBER_CARD = ?";
    private static final String LOG_OPERATION_QUERY = "INSERT INTO CLIENT_OPERATION (CARD_NUM, OPERATION, DDATE_STAMP, COMMENT) VALUES (?, ?, ?, ?)";
    private static final String LOAD_SERVICES_QUERY = "SELECT ID_SERVICE, NAME_SERVICE, ACTIVE_STATUS FROM SEVICE";
    private static final String ADD_SERVICE_QUERY = "INSERT INTO SEVICE (NAME_SERVICE, ACTIVE_STATUS) VALUES (?, ?)";
    private static final String DELETE_SERVICE_QUERY = "DELETE FROM SEVICE WHERE NAME_SERVICE = ?";
    private static final String UPDATE_SERVICE_STATUS_QUERY = "UPDATE SEVICE SET ACTIVE_STATUS = ? WHERE ID_SERVICE = ?";
    private static final String CHECK_SERVICE_EXISTS_QUERY = "SELECT COUNT(*) FROM SEVICE WHERE NAME_SERVICE = ?";
    private static final String GET_LAST_INSERTED_ID_QUERY = "SELECT MAX(ID_SERVICE) AS LAST_ID FROM SEVICE";
    private static final String GET_ACTIVE_SERVICES_QUERY = "SELECT NAME_SERVICE FROM SEVICE WHERE ACTIVE_STATUS = TRUE";
    private static final String GET_CASH_TO_REMOVE_QUERY = "SELECT ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER, DATE_INSERTED FROM ATM_CASH_STORAGE ORDER BY DENOMINATIONS DESC, DATE_INSERTED ASC";
    private static final String DELETE_CASH_QUERY = "DELETE FROM ATM_CASH_STORAGE WHERE ID_CASH = ?";
    private static final String GET_CASH_STORAGE_DATA_QUERY = "SELECT DENOMINATIONS, SERIAL_NUMBER FROM ATM_CASH_STORAGE";
    private static final String DELETE_ISSUED_BILLS_QUERY = "DELETE FROM ATM_CASH_STORAGE WHERE SERIAL_NUMBER = ?";
    private static final String GET_CURRENT_CASH_COUNT = "SELECT DN.NOMINAL AS Nominal, COUNT(ACS.ID_CASH) AS Quantity \" +\n" +
            "                \"FROM ATM_CASH_STORAGE ACS \" +\n" +
            "                \"JOIN DIC_NOMINAL DN ON ACS.DENOMINATIONS = DN.NOMINAL \" +\n" +
            "                \"GROUP BY DN.NOMINAL";
    private static final String TRANSACTION_HISTORY_QUERY = """
            SELECT co.CARD_NUM, co.OPERATION, co.DDATE_STAMP, co.COMMENT
            FROM CLIENT_OPERATION co
            JOIN CLIENT_CARD cc ON co.CARD_NUM = cc.NUMBER_CARD
            WHERE cc.FK_CLIENT = (SELECT FK_CLIENT FROM CLIENT_CARD WHERE NUMBER_CARD = ?)
            ORDER BY co.DDATE_STAMP DESC
            """;

    public static void main(String[] args) {
        // Проверка существования файла
        File dbFile = new File(DATABASE_PATH);
        if (!dbFile.exists()) {
            System.out.println("Файл базы данных не найден: " + dbFile.getAbsolutePath());
            return;
        }

        try {
            // Загрузка драйвера Firebird
            Class.forName("org.firebirdsql.jdbc.FBDriver");

            // Подключение к базе данных
            Connection connection = DriverManager.getConnection(JDBC_URL, USER, PASSWORD);
            System.out.println("Соединение установлено успешно!");
            connection.close();
        } catch (Exception e) {
            System.err.println("Ошибка подключения: " + e.getMessage());
            e.printStackTrace();
        }
    }
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

    // Генерация серийного номера
    private static String generateSerialNumber() {
        return "SN" + System.currentTimeMillis();
    }

    // Метод для получения текущего количества банкнот
    public static Map<Integer, Integer> getCurrentCashCount() throws SQLException {
        Map<Integer, Integer> cashCount = new HashMap<>();

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_CURRENT_CASH_COUNT)) {

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

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_CASH_TO_REMOVE_QUERY)) {

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

        try (Connection connection = getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(GET_CASH_STORAGE_DATA_QUERY)) {

            while (resultSet.next()) {
                String denomination = resultSet.getString("DENOMINATIONS");
                String serialNumber = resultSet.getString("SERIAL_NUMBER");
                cashStorageList.add(new MoneyWithdrawalScene.CashStorage(denomination, serialNumber));
            }
        }

        return cashStorageList;
    }

    public static void deleteIssuedBills(List<MoneyWithdrawalScene.CashStorage> issuedBills) throws SQLException {

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(DELETE_ISSUED_BILLS_QUERY)) {

            for (MoneyWithdrawalScene.CashStorage bill : issuedBills) {
                statement.setString(1, bill.getSerialNumber());
                statement.addBatch();
            }

            statement.executeBatch();
        }
    }

    // Метод для обновления баланса карты
    public static void updateCardBalance(String cardNumber, int amount) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(UPDATE_BALANCE_QUERY )) {

            statement.setInt(1, amount);
            statement.setString(2, cardNumber);

            int rowsUpdated = statement.executeUpdate();
            if (rowsUpdated == 0) {
                throw new SQLException("Карта не найдена.");
            }
        }
    }
    // Метод для проверки наличия карты в базе данных
    public static boolean checkCardInDatabase(String cardNumber) throws SQLException {
        // Вызов хранимой процедуры
        try (Connection conn = getConnection();
             CallableStatement callableStatement = conn.prepareCall(CHECK_CARD_IN_DATABASE)) {
            callableStatement.setString(1, cardNumber); // Устанавливаем параметр CARD_NUMBER
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    int cardExists = resultSet.getInt("CARD_EXISTS"); // Получаем результат
                    return cardExists > 0; // Возвращаем true, если карта найдена
                }
            }
        }
        return false; // Если карта не найдена или произошла ошибка
    }
    // Метод для проверки пин-кода через хранимую процедуру
    public static boolean checkPinCode(String cardNumber, String enteredPin) throws SQLException {
        String query = "{CALL CHECK_PIN_CODE(?, ?)}"; // Вызов хранимой процедуры
        try (Connection conn = getConnection();
             CallableStatement callableStatement = conn.prepareCall(query)) {
            callableStatement.setString(1, cardNumber); // Устанавливаем параметр CARD_NUMBER
            callableStatement.setString(2, enteredPin); // Устанавливаем параметр ENTERED_PIN
            try (ResultSet resultSet = callableStatement.executeQuery()) {
                if (resultSet.next()) {
                    int pinValid = resultSet.getInt("PIN_VALID"); // Получаем результат
                    return pinValid == 1; // Возвращаем true, если пин-код верный
                }
            }
        }
        return false; // Если пин-код неверный или произошла ошибка
    }
    // Метод для загрузки всех услуг из таблицы SEVICE
    public static List<ServiceManagement.Service> loadServices() throws SQLException {
        List<ServiceManagement.Service> services = new ArrayList<>();
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(LOAD_SERVICES_QUERY)) {
            while (rs.next()) {
                long id = rs.getLong("ID_SERVICE");
                String name = rs.getString("NAME_SERVICE");
                boolean activeStatus = rs.getBoolean("ACTIVE_STATUS");
                services.add(new ServiceManagement.Service(id, name, activeStatus));
            }
        }
        return services;
    }

    // Метод для добавления новой услуги в таблицу SEVICE
    public static boolean addService(String serviceName) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(ADD_SERVICE_QUERY)) {
            pstmt.setString(1, serviceName); // NAME_SERVICE
            pstmt.setBoolean(2, true); // ACTIVE_STATUS (по умолчанию true)
            return pstmt.executeUpdate() > 0;
        }
    }

    // Метод для удаления услуги из таблицы SEVICE по её имени
    public static boolean deleteService(String serviceName) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(DELETE_SERVICE_QUERY)) {
            pstmt.setString(1, serviceName);
            return pstmt.executeUpdate() > 0;
        }
    }

    // Метод для обновления статуса активности услуги
    public static boolean updateServiceStatus(long serviceId, boolean isActive) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_SERVICE_STATUS_QUERY)) {
            pstmt.setBoolean(1, isActive); // ACTIVE_STATUS
            pstmt.setLong(2, serviceId); // ID_SERVICE
            return pstmt.executeUpdate() > 0;
        }
    }
    // Метод для проверки существования услуги по имени
    public static boolean isServiceExists(String serviceName) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(CHECK_SERVICE_EXISTS_QUERY)) {
            pstmt.setString(1, serviceName);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0; // Если количество записей > 0, услуга существует
            }
        }
        return false;
    }

    // Метод для получения ID последней вставленной услуги
    public static long getLastInsertedId() throws SQLException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(GET_LAST_INSERTED_ID_QUERY)) {
            if (rs.next()) {
                return rs.getLong(1); // Возвращаем ID последней вставленной записи
            }
        }
        throw new SQLException("Не удалось получить ID последней вставленной записи.");
    }
    public static List<String> getActiveServices() throws SQLException {
        List<String> activeServices = new ArrayList<>();

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(GET_ACTIVE_SERVICES_QUERY);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                activeServices.add(rs.getString("NAME_SERVICE"));
            }
        }
        return activeServices;
    }
    // Новый метод для получения баланса карты
    public static double getCardBalance(String cardNumber) {
        double balance = 0.0; // Значение по умолчанию
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(GET_BALANCE_QUERY)) {

            statement.setString(1, cardNumber);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                balance = resultSet.getDouble("BALANCE");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка при получении баланса карты: " + e.getMessage());
        }
        return balance;
    }
    public static boolean logTransaction(String cardNumber, String serviceName, String amount) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(LOG_OPERATION_QUERY)) {

            pstmt.setString(1, cardNumber); // Номер карты
            pstmt.setString(2, "Оплата Услуги"); // Тип операции
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now())); // Дата и время операции
            pstmt.setString(4, "Оплата услуги: " + serviceName + ", Сумма: " + amount); // Комментарий

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean updateBalance(String cardNumber, double amount) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(UPDATE_BALANCE_QUERY)) {

            pstmt.setDouble(1, amount);
            pstmt.setString(2, cardNumber);

            pstmt.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Добавляет банкноты в хранилище банкомата.
     *
     * @param banknotes Список банкнот для добавления.
     * @return true, если все банкноты были успешно добавлены, иначе false.
     */
    public static boolean addBanknotesToStorage(List<DepositOperation.Denomination> banknotes) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(INSERT_CASH_STORAGE_QUERY)) {

            for (DepositOperation.Denomination banknote : banknotes) {
                String denomination = banknote.getDemonition().replace(" руб.", "");
                if (!doesNominalExist(denomination, conn)) {
                    System.out.println("Номинал " + denomination + " не существует в таблице DIC_NOMINAL.");
                    continue;
                }

                String idCash = generateUniqueId();
                String atmId = "123456";
                String serialNumber = banknote.getSerias();

                pstmt.setString(1, idCash);
                pstmt.setString(2, atmId);
                pstmt.setString(3, denomination);
                pstmt.setString(4, serialNumber);
                pstmt.addBatch();
            }

            pstmt.executeBatch();
            System.out.println("Банкноты успешно добавлены в хранилище.");
            return true;
        } catch (SQLException e) {
            System.err.println("Ошибка при добавлении банкнот в хранилище: " + e.getMessage());
            return false;
        }
    }

    /**
     * Проверяет, существует ли номинал в таблице DIC_NOMINAL.
     *
     * @param denomination Номинал банкноты.
     * @param conn         Соединение с базой данных.
     * @return true, если номинал существует, иначе false.
     */
    private static boolean doesNominalExist(String denomination, Connection conn) throws SQLException {
        try (PreparedStatement checkStmt = conn.prepareStatement(CHECK_NOMINAL_EXISTS_QUERY)) {
            checkStmt.setString(1, denomination);
            return checkStmt.executeQuery().next();
        }
    }

    /**
     * Генерирует уникальный ID для банкноты.
     *
     * @return Уникальный ID.
     */
    private static String generateUniqueId() {
        return "CS" + System.currentTimeMillis();
    }

    /**
     * Логирует операцию в базе данных.
     *
     * @param cardNumber Номер карты.
     * @param operation  Тип операции.
     * @param comment    Комментарий к операции.
     */
    public static void logOperation(String cardNumber, String operation, String comment) {
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(LOG_OPERATION_QUERY)) {

            pstmt.setString(1, cardNumber);
            pstmt.setString(2, operation);
            pstmt.setTimestamp(3, Timestamp.valueOf(LocalDateTime.now()));
            pstmt.setString(4, comment);

            pstmt.executeUpdate();
            System.out.println("Операция успешно логирована.");
        } catch (SQLException e) {
            System.err.println("Ошибка при логировании операции: " + e.getMessage());
        }
    }
    public static class DatabaseHelper {
        public static SettingsCardMenu.ClientInfo getClientInfo(String cardNumber) {
            SettingsCardMenu.ClientInfo clientInfo = null;
            try (Connection conn = getConnection();
                 PreparedStatement pstmt = conn.prepareStatement(GET_CLIENT_INFO_QUERY)) {{
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
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return clientInfo;
        }
    }
    /**
     * Загружает историю операций для указанной карты.
     *
     * @param cardNumber Номер карты клиента.
     * @return Список объектов OperationInfo с данными о транзакциях.
     */
    public static ObservableList<TransactionHistoryMenu.OperationInfo> loadTransactionHistory(String cardNumber) {
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
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return operationData;
    }
}


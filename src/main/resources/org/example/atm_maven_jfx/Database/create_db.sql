
/* Setting properties */

SET NAMES UTF8;
SET SQL DIALECT 3;
CONNECT 'localhost/3050:src/main/resources/org/example/atm_maven_jfx/Database/ATM_MODEL_DBASE.fdb' USER 'SYSDBA' PASSWORD '010802';
SET AUTODDL ON;

/* ----- Creating Tables ----- */

/* ATM_BALANCE */
CREATE TABLE ATM_BALANCE (
    ID_ATM VARCHAR(155) NOT NULL,
    MAX_AMOUNT INTEGER NOT NULL,
    MIN_AMOUNT INTEGER NOT NULL,
    CURRENT_AMOUNT INTEGER NOT NULL);


/* ATM_CASH_STORAGE */
CREATE TABLE ATM_CASH_STORAGE (
    ID_CASH VARCHAR(255) NOT NULL,
    ID_ATM VARCHAR(155) NOT NULL,
    DENOMINATIONS VARCHAR(150),
    SERIAL_NUMBER VARCHAR(255) NOT NULL,
    DATE_INSERTED TIMESTAMP DEFAULT CURRENT_TIMESTAMP);


/* ATM_STATS */
CREATE TABLE ATM_STATS (
    ID_ATM VARCHAR(155),
    CLIENTS BIGINT DEFAULT 0,
    SUM_OPERATION FLOAT DEFAULT 0,
    OPERATIONS INTEGER DEFAULT 0);


/* ATM_TOTAL_CASH */
CREATE TABLE ATM_TOTAL_CASH (
    ID_ATM VARCHAR(155) NOT NULL,
    TOTAL_CASH FLOAT,
    LAST_UPDATED TIMESTAMP DEFAULT CURRENT_TIMESTAMP);


/* BALANCE_CARD */
CREATE TABLE BALANCE_CARD (
    ID INTEGER NOT NULL,
    FK_CARD VARCHAR(700),
    BALANCE FLOAT DEFAULT 0);


/* BANK_DIC */
CREATE TABLE BANK_DIC (
    ID INTEGER NOT NULL,
    TITLE VARCHAR(500),
    SMALL_TITTLE VARCHAR(500),
    FACTOR_ADRESS VARCHAR(500),
    WORK_NUMBER VARCHAR(500));


/* CLIENT_AUG */
CREATE TABLE CLIENT_AUG (
    AUG_ID INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    CARD_NUMBER VARCHAR(20) NOT NULL,
    PHOTO_DATA BLOB SUB_TYPE BINARY SEGMENT SIZE 8192 NOT NULL,
    AUGMENTATION_DATE TIMESTAMP NOT NULL);


/* CLIENT_BIOMETRY */
CREATE TABLE CLIENT_BIOMETRY (
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    CARD_NUMBER VARCHAR(20) NOT NULL,
    PHOTO_INDEX INTEGER NOT NULL,
    IMAGE_DATA BLOB SUB_TYPE BINARY NOT NULL,
    ENCODING_DATA BLOB SUB_TYPE BINARY,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    IS_AUGMENTED BOOLEAN DEFAULT FALSE,
    AUGMENTATION_TYPE VARCHAR(50));


/* CLIENT_CARD */
CREATE TABLE CLIENT_CARD (
    ID INTEGER NOT NULL,
    FK_CLIENT VARCHAR(765),
    FK_CARD_BANK VARCHAR(700),
    NUMBER_CARD VARCHAR(16),
    PIN_CODE VARCHAR(14),
    VALIDATION DATE,
    FK_TYPE_CARD VARCHAR(255));


/* CLIENT_DATA */
CREATE TABLE CLIENT_DATA (
    ID INTEGER NOT NULL,
    FK_CLIENT VARCHAR(765) NOT NULL,
    PHOTO_PATH BLOB SUB_TYPE BINARY);


/* CLIENT_INFO */
CREATE TABLE CLIENT_INFO (
    ID INTEGER NOT NULL,
    FULL_FIO VARCHAR(765) NOT NULL,
    SURNAME VARCHAR(255),
    NAME_CLIENT VARCHAR(255),
    NAME_FATHER VARCHAR(255),
    AGE VARCHAR(10),
    GENGER VARCHAR(25),
    DATA_BIRTH DATE,
    PASSPORT VARCHAR(255),
    WHERE_PASSPORT VARCHAR(500),
    DATE_PASSPORT DATE,
    SNILS VARCHAR(255),
    INN VARCHAR(255),
    STATUS VARCHAR(255));


/* CLIENT_OPERATION */
CREATE TABLE CLIENT_OPERATION (
    ID INTEGER NOT NULL,
    CARD_NUM VARCHAR(500),
    DDATE_STAMP TIMESTAMP,
    OPERATION VARCHAR(500),
    COMMENT VARCHAR(155));


/* COMPANY */
CREATE TABLE COMPANY (
    ID INTEGER NOT NULL,
    TITLE VARCHAR(500),
    SMALL_TITLE VARCHAR(500),
    FACTOR_ADRESS VARCHAR(500),
    WORK_NUMBER VARCHAR(500),
    SNILS VARCHAR(500),
    INN VARCHAR(500));


/* DATASET_INFO */
CREATE TABLE DATASET_INFO (
    ID INTEGER GENERATED BY DEFAULT AS IDENTITY NOT NULL,
    DATASET_NAME VARCHAR(100) NOT NULL,
    CREATED_AT TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    TOTAL_PHOTOS INTEGER NOT NULL,
    TOTAL_CLIENTS INTEGER NOT NULL,
    DESCRIPTION VARCHAR(500),
    VERSION VARCHAR(20),
    IS_ACTIVE BOOLEAN DEFAULT FALSE,
    FILE_PATH VARCHAR(255));


/* DATASET_METADATA */
CREATE TABLE DATASET_METADATA (
    ID INTEGER DEFAULT 1 NOT NULL,
    LAST_AUGMENTATION TIMESTAMP,
    LAST_DATASET_CREATION TIMESTAMP);


/* DIC_CARD_TYPE */
CREATE TABLE DIC_CARD_TYPE (
    ID BIGINT NOT NULL,
    TYPE_CARD VARCHAR(255));


/* DIC_NOMINAL */
CREATE TABLE DIC_NOMINAL (
    ID BIGINT NOT NULL,
    NOMINAL VARCHAR(150));


/* DIC_OPERATION */
CREATE TABLE DIC_OPERATION (
    ID BIGINT NOT NULL,
    TITLE VARCHAR(50));


/* SEVICE */
CREATE TABLE SEVICE (
    ID_SERVICE BIGINT NOT NULL,
    NAME_SERVICE VARCHAR(255) NOT NULL,
    ACTIVE_STATUS BOOLEAN);


/* USERS */
CREATE TABLE USERS (
    ID INTEGER NOT NULL,
    USERNAME VARCHAR(255),
    "PASSWORD" VARCHAR(255),
    ROLE VARCHAR(250));


/* ----- Creating Sequences ----- */

/* GEN_BALANCE_CARD_ID */
CREATE OR ALTER SEQUENCE GEN_BALANCE_CARD_ID START WITH 0 INCREMENT BY 1;

/* GEN_CLIENT_CARD_ID */
CREATE OR ALTER SEQUENCE GEN_CLIENT_CARD_ID START WITH 0 INCREMENT BY 1;

/* GEN_COMPANY_ID */
CREATE OR ALTER SEQUENCE GEN_COMPANY_ID START WITH 0 INCREMENT BY 1;

/* SEQ_ATM_CASH_STORAGE_ID_CASH */
CREATE OR ALTER SEQUENCE SEQ_ATM_CASH_STORAGE_ID_CASH START WITH 0 INCREMENT BY 1;

/* SEQ_ATM_KUPER_AMOUT_ID_SERIAL */
CREATE OR ALTER SEQUENCE SEQ_ATM_KUPER_AMOUT_ID_SERIAL START WITH -1 INCREMENT BY 1;

/* SEQ_BALANCE_CARD_ID */
CREATE OR ALTER SEQUENCE SEQ_BALANCE_CARD_ID START WITH 0 INCREMENT BY 1;

/* SEQ_BANK_DIC_ID */
CREATE OR ALTER SEQUENCE SEQ_BANK_DIC_ID START WITH 0 INCREMENT BY 1;

/* SEQ_CLIENT_CARD_ID */
CREATE OR ALTER SEQUENCE SEQ_CLIENT_CARD_ID START WITH 0 INCREMENT BY 1;

/* SEQ_CLIENT_CREDIT_ID */
CREATE OR ALTER SEQUENCE SEQ_CLIENT_CREDIT_ID START WITH 0 INCREMENT BY 1;

/* SEQ_CLIENT_DATA_ID */
CREATE OR ALTER SEQUENCE SEQ_CLIENT_DATA_ID START WITH 0 INCREMENT BY 1;

/* SEQ_CLIENT_INFO_ID */
CREATE OR ALTER SEQUENCE SEQ_CLIENT_INFO_ID START WITH 0 INCREMENT BY 1;

/* SEQ_CLIENT_OPERATION_ID */
CREATE OR ALTER SEQUENCE SEQ_CLIENT_OPERATION_ID START WITH 0 INCREMENT BY 1;

/* SEQ_COMPANY_ID */
CREATE OR ALTER SEQUENCE SEQ_COMPANY_ID START WITH 0 INCREMENT BY 1;

/* SEQ_DIC_CARD_TYPE_ID */
CREATE OR ALTER SEQUENCE SEQ_DIC_CARD_TYPE_ID START WITH 0 INCREMENT BY 1;

/* SEQ_DIC_CREDIT_TYPE_ID */
CREATE OR ALTER SEQUENCE SEQ_DIC_CREDIT_TYPE_ID START WITH 0 INCREMENT BY 1;

/* SEQ_DIC_OPERATION_ID */
CREATE OR ALTER SEQUENCE SEQ_DIC_OPERATION_ID START WITH -1 INCREMENT BY 1;

/* SEQ_SEVICE_ID_SERVICE */
CREATE OR ALTER SEQUENCE SEQ_SEVICE_ID_SERVICE START WITH 0 INCREMENT BY 6;

/* SEQ_USERS_ID */
CREATE OR ALTER SEQUENCE SEQ_USERS_ID START WITH -1 INCREMENT BY 1;

/* ----- Creating Exceptions ----- */

/* UNKNOWN_CARD */
CREATE EXCEPTION UNKNOWN_CARD
	'Карта с указанным FK_CARD не найдена';

/* ----- Creating Table Triggers stubs ----- */

/* ATM_CASH_STORAGE_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER ATM_CASH_STORAGE_BI
	FOR ATM_CASH_STORAGE BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* BALANCE_CARD_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER BALANCE_CARD_BI
	FOR BALANCE_CARD BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* BANK_DIC_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER BANK_DIC_BI
	FOR BANK_DIC BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* CLIENT_CARD_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER CLIENT_CARD_BI
	FOR CLIENT_CARD BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* CLIENT_INFO_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER CLIENT_INFO_BI
	FOR CLIENT_INFO BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* CLIENT_OPERATION_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER CLIENT_OPERATION_BI
	FOR CLIENT_OPERATION BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* COMPANY_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER COMPANY_BI
	FOR COMPANY BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* DIC_CARD_TYPE_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER DIC_CARD_TYPE_BI
	FOR DIC_CARD_TYPE BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* DIC_OPERATION_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER DIC_OPERATION_BI
	FOR DIC_OPERATION BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* SEVICE_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER SEVICE_BI
	FOR SEVICE BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* USERS_BI (STUB) */
SET TERM ^;
CREATE OR ALTER TRIGGER USERS_BI
	FOR USERS BEFORE INSERT
AS BEGIN END^

SET TERM ;^

/* ----- Creating Procedures stubs ----- */

/* ADD_CASH_TO_STORAGE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE ADD_CASH_TO_STORAGE (
	ID_ATM VARCHAR(50),
	DENOMINATIONS VARCHAR(10),
	SERIAL_NUMBER VARCHAR(50)
) 
AS BEGIN END^
SET TERM ;^

/* AUTHENTICATE_USER (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE AUTHENTICATE_USER (
	USERNAME VARCHAR(50),
	"PASSWORD" VARCHAR(50)
) 
RETURNS (
	AUTHENTICATED BOOLEAN
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* CHECK_CARD_IN_DATABASE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE CHECK_CARD_IN_DATABASE (
	CARD_NUMBER VARCHAR(255)
) 
RETURNS (
	CARD_EXISTS INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* CHECK_NOMINAL_EXISTS (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE CHECK_NOMINAL_EXISTS (
	NOMINAL_PARAM VARCHAR(150)
) 
RETURNS (
	EXISTS_FLAG INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* CHECK_PIN_CODE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE CHECK_PIN_CODE (
	CARD_NUMBER VARCHAR(255),
	ENTERED_PIN VARCHAR(4)
) 
RETURNS (
	PIN_VALID INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* COUNT_SERVICES_BY_NAME (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE COUNT_SERVICES_BY_NAME (
	NAME_SERVICE VARCHAR(255)
) 
RETURNS (
	SERVICE_COUNT INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* DELETE_ATM_CASH_BY_ID (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE DELETE_ATM_CASH_BY_ID (
	CASH_ID INTEGER
) 
AS BEGIN END^
SET TERM ;^

/* DELETE_ATM_CASH_BY_SERIAL (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE DELETE_ATM_CASH_BY_SERIAL (
	SERIAL_NUMBER_PARAM VARCHAR(255)
) 
AS BEGIN END^
SET TERM ;^

/* DELETE_SERVICE_BY_NAME (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE DELETE_SERVICE_BY_NAME (
	NAME_SERVICE VARCHAR(255)
) 
AS BEGIN END^
SET TERM ;^

/* GET_ACTIVE_SERVICES (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ACTIVE_SERVICES
RETURNS (
	NAME_SERVICE VARCHAR(255)
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_ATM_CASH_DENOMINATIONS (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ATM_CASH_DENOMINATIONS
RETURNS (
	DENOMINATIONS FLOAT,
	SERIAL_NUMBER VARCHAR(255)
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_ATM_CASH_QUANTITY (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ATM_CASH_QUANTITY
RETURNS (
	NOMINAL FLOAT,
	QUANTITY INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_ATM_CASH_STORAGE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ATM_CASH_STORAGE
RETURNS (
	ID_CASH VARCHAR(255),
	ID_ATM VARCHAR(255),
	DENOMINATIONS FLOAT,
	SERIAL_NUMBER VARCHAR(255),
	DATE_INSERTED TIMESTAMP
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_BALANCE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_BALANCE (
	CARDNUMBER VARCHAR(700)
) 
RETURNS (
	BALANCE FLOAT
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_CLIENT_INFO (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_CLIENT_INFO (
	CARD_NUMBER VARCHAR(700)
) 
RETURNS (
	FULL_FIO VARCHAR(765),
	AGE VARCHAR(10),
	GENGER VARCHAR(25),
	DATA_BIRTH DATE,
	PASSPORT VARCHAR(255),
	WHERE_PASSPORT VARCHAR(500),
	DATE_PASSPORT DATE,
	SNILS VARCHAR(255),
	INN VARCHAR(255),
	STATUS VARCHAR(255)
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_CLIENT_OPERATIONS (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_CLIENT_OPERATIONS (
	CARD_NUMBER_PARAM VARCHAR(700)
) 
RETURNS (
	CARD_NUM VARCHAR(700),
	OPERATION VARCHAR(255),
	DDATE_STAMP TIMESTAMP,
	COMMENT VARCHAR(1000)
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_LAST_SERVICE_ID (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_LAST_SERVICE_ID
RETURNS (
	LAST_ID INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_MAX_ATM_CASH_ID (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_MAX_ATM_CASH_ID
RETURNS (
	MAX_ID_CASH VARCHAR(50)
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_SERVICES (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_SERVICES
RETURNS (
	ID_SERVICE BIGINT,
	NAME_SERVICE VARCHAR(255),
	ACTIVE_STATUS BOOLEAN
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* GET_TOTAL_AMOUNT_IN_ATM (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_TOTAL_AMOUNT_IN_ATM
RETURNS (
	TOTAL_AMOUNT INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* INSERT_CASH (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE INSERT_CASH (
	ID_CASH VARCHAR(50),
	ID_ATM VARCHAR(50),
	DENOMINATIONS VARCHAR(50),
	SERIAL_NUMBER VARCHAR(255)
) 
AS BEGIN END^
SET TERM ;^

/* INSERT_SERVICE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE INSERT_SERVICE (
	NAME_SERVICE VARCHAR(255),
	ACTIVE_STATUS VARCHAR(50)
) 
AS BEGIN END^
SET TERM ;^

/* LOAD_ATM_BALANCE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE LOAD_ATM_BALANCE
RETURNS (
	ID_ATM VARCHAR(50),
	MAX_AMOUNT INTEGER,
	MIN_AMOUNT INTEGER,
	CURRENT_AMOUNT INTEGER
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* LOAD_CASH_STORAGE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE LOAD_CASH_STORAGE
RETURNS (
	ID_CASH VARCHAR(50),
	ID_ATM VARCHAR(50),
	DENOMINATIONS DOUBLE PRECISION,
	SERIAL_NUMBER VARCHAR(50),
	DATE_INSERTED TIMESTAMP
)
AS BEGIN 
	SUSPEND;
END^
SET TERM ;^

/* LOG_OPERATION (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE LOG_OPERATION (
	CARD_NUM VARCHAR(700),
	OPERATION VARCHAR(255),
	DDATE_STAMP TIMESTAMP,
	COMMENT VARCHAR(1000)
) 
AS BEGIN END^
SET TERM ;^

/* UPDATE_BALANCE (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE UPDATE_BALANCE (
	AMOUNT DECIMAL(15,2),
	FK_CARD VARCHAR(16)
) 
AS BEGIN END^
SET TERM ;^

/* UPDATE_CURRENT_AMOUNT (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE UPDATE_CURRENT_AMOUNT (
	NEW_AMOUNT INTEGER,
	ATM_ID VARCHAR(50)
) 
AS BEGIN END^
SET TERM ;^

/* UPDATE_SERVICE_STATUS (STUB) */
SET TERM ^;
CREATE OR ALTER PROCEDURE UPDATE_SERVICE_STATUS (
	NEW_ACTIVE_STATUS BOOLEAN,
	SERVICE_ID INTEGER
) 
AS BEGIN END^
SET TERM ;^

/* ----- Creating Procedures ----- */

/* ADD_CASH_TO_STORAGE */
SET TERM ^;
CREATE OR ALTER PROCEDURE ADD_CASH_TO_STORAGE (
	ID_ATM VARCHAR(50),
	DENOMINATIONS VARCHAR(10),
	SERIAL_NUMBER VARCHAR(50)
) 
AS
BEGIN
    INSERT INTO ATM_CASH_STORAGE (ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER)
    VALUES (GEN_UUID(), :ID_ATM, :DENOMINATIONS, :SERIAL_NUMBER);
END
^
SET TERM ;^

/* AUTHENTICATE_USER */
SET TERM ^;
CREATE OR ALTER PROCEDURE AUTHENTICATE_USER (
	USERNAME VARCHAR(50),
	"PASSWORD" VARCHAR(50)
) 
RETURNS (
	AUTHENTICATED BOOLEAN
)
AS
BEGIN
    AUTHENTICATED = FALSE;

    IF (EXISTS(SELECT 1 FROM USERS WHERE USERNAME = :USERNAME AND PASSWORD = :PASSWORD)) THEN
    BEGIN
        AUTHENTICATED = TRUE;
    END

    SUSPEND;
END
^
SET TERM ;^

/* CHECK_CARD_IN_DATABASE */
SET TERM ^;
CREATE OR ALTER PROCEDURE CHECK_CARD_IN_DATABASE (
	CARD_NUMBER VARCHAR(255)
) 
RETURNS (
	CARD_EXISTS INTEGER
)
AS
BEGIN
    SELECT COUNT(*)
    FROM CLIENT_CARD
    WHERE NUMBER_CARD = :CARD_NUMBER
    INTO :CARD_EXISTS;

    SUSPEND;
END
^
SET TERM ;^

/* CHECK_NOMINAL_EXISTS */
SET TERM ^;
CREATE OR ALTER PROCEDURE CHECK_NOMINAL_EXISTS (
	NOMINAL_PARAM VARCHAR(150)
) 
RETURNS (
	EXISTS_FLAG INTEGER
)
AS
BEGIN
    -- Инициализация флага существования
    EXISTS_FLAG = 0;

    -- Проверка наличия номинала в таблице
    SELECT 1
    FROM DIC_NOMINAL
    WHERE NOMINAL = :NOMINAL_PARAM
    INTO :EXISTS_FLAG;

    SUSPEND;
END
^
SET TERM ;^

/* CHECK_PIN_CODE */
SET TERM ^;
CREATE OR ALTER PROCEDURE CHECK_PIN_CODE (
	CARD_NUMBER VARCHAR(255),
	ENTERED_PIN VARCHAR(4)
) 
RETURNS (
	PIN_VALID INTEGER
)
AS
BEGIN
    SELECT CASE 
        WHEN PIN_CODE = :ENTERED_PIN THEN 1 
        ELSE 0 
    END
    FROM CLIENT_CARD
    WHERE NUMBER_CARD = :CARD_NUMBER
    INTO :PIN_VALID;

    IF (:PIN_VALID IS NULL) THEN
        PIN_VALID = 0;

    SUSPEND;
END
^
SET TERM ;^

/* COUNT_SERVICES_BY_NAME */
SET TERM ^;
CREATE OR ALTER PROCEDURE COUNT_SERVICES_BY_NAME (
	NAME_SERVICE VARCHAR(255)
) 
RETURNS (
	SERVICE_COUNT INTEGER
)
AS
BEGIN
    -- Подсчет записей в таблице SEVICE
    SELECT COUNT(*)
    FROM SEVICE
    WHERE NAME_SERVICE = :NAME_SERVICE
    INTO :SERVICE_COUNT;

    SUSPEND;
END
^
SET TERM ;^

/* DELETE_ATM_CASH_BY_ID */
SET TERM ^;
CREATE OR ALTER PROCEDURE DELETE_ATM_CASH_BY_ID (
	CASH_ID INTEGER
) 
AS
BEGIN
    -- Удаление записи из таблицы ATM_CASH_STORAGE
    DELETE FROM ATM_CASH_STORAGE
    WHERE ID_CASH = :CASH_ID;
END
^
SET TERM ;^

/* DELETE_ATM_CASH_BY_SERIAL */
SET TERM ^;
CREATE OR ALTER PROCEDURE DELETE_ATM_CASH_BY_SERIAL (
	SERIAL_NUMBER_PARAM VARCHAR(255)
) 
AS
BEGIN
    -- Удаление записи из таблицы ATM_CASH_STORAGE
    DELETE FROM ATM_CASH_STORAGE
    WHERE SERIAL_NUMBER = :SERIAL_NUMBER_PARAM;
END
^
SET TERM ;^

/* DELETE_SERVICE_BY_NAME */
SET TERM ^;
CREATE OR ALTER PROCEDURE DELETE_SERVICE_BY_NAME (
	NAME_SERVICE VARCHAR(255)
) 
AS
BEGIN
    -- Удаление данных из таблицы SEVICE
    DELETE FROM SEVICE
    WHERE NAME_SERVICE = :NAME_SERVICE;
END
^
SET TERM ;^

/* GET_ACTIVE_SERVICES */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ACTIVE_SERVICES
RETURNS (
	NAME_SERVICE VARCHAR(255)
)
AS
BEGIN
    -- Выборка названий активных услуг
    FOR
        SELECT NAME_SERVICE
        FROM SEVICE
        WHERE ACTIVE_STATUS = 'TRUE'
        INTO :NAME_SERVICE
    DO
        SUSPEND;
END
^
SET TERM ;^

/* GET_ATM_CASH_DENOMINATIONS */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ATM_CASH_DENOMINATIONS
RETURNS (
	DENOMINATIONS FLOAT,
	SERIAL_NUMBER VARCHAR(255)
)
AS
BEGIN
    -- Выборка данных из таблицы ATM_CASH_STORAGE
    FOR
        SELECT
            DENOMINATIONS,
            SERIAL_NUMBER
        FROM
            ATM_CASH_STORAGE
        INTO
            :DENOMINATIONS,
            :SERIAL_NUMBER
    DO
        SUSPEND;
END
^
SET TERM ;^

/* GET_ATM_CASH_QUANTITY */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ATM_CASH_QUANTITY
RETURNS (
	NOMINAL FLOAT,
	QUANTITY INTEGER
)
AS
BEGIN
    -- Выборка данных с агрегацией
    FOR
        SELECT
            DN.NOMINAL,
            COUNT(ACS.ID_CASH)
        FROM
            ATM_CASH_STORAGE ACS
        JOIN
            DIC_NOMINAL DN
        ON
            ACS.DENOMINATIONS = DN.NOMINAL
        GROUP BY
            DN.NOMINAL
        INTO
            :NOMINAL,
            :QUANTITY
    DO
        SUSPEND;
END
^
SET TERM ;^

/* GET_ATM_CASH_STORAGE */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_ATM_CASH_STORAGE
RETURNS (
	ID_CASH VARCHAR(255),
	ID_ATM VARCHAR(255),
	DENOMINATIONS FLOAT,
	SERIAL_NUMBER VARCHAR(255),
	DATE_INSERTED TIMESTAMP
)
AS
BEGIN
    -- Выборка данных из таблицы ATM_CASH_STORAGE с сортировкой
    FOR
        SELECT
            ID_CASH,
            ID_ATM,
            DENOMINATIONS,
            SERIAL_NUMBER,
            DATE_INSERTED
        FROM
            ATM_CASH_STORAGE
        ORDER BY
            DENOMINATIONS DESC,
            DATE_INSERTED ASC
        INTO
            :ID_CASH,
            :ID_ATM,
            :DENOMINATIONS,
            :SERIAL_NUMBER,
            :DATE_INSERTED
    DO
        SUSPEND;
END
^
SET TERM ;^

/* GET_BALANCE */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_BALANCE (
	CARDNUMBER VARCHAR(700)
) 
RETURNS (
	BALANCE FLOAT
)
AS
BEGIN
    -- Инициализация переменной для баланса
    BALANCE = 0.0;

    -- Получение баланса из таблицы BALANCE_CARD
    SELECT COALESCE(B.BALANCE, 0.0)
    FROM BALANCE_CARD B
    WHERE B.FK_CARD = :CARDNUMBER
    INTO :BALANCE;

    -- Если запись не найдена, баланс остается равным 0.0
    SUSPEND;
END
^
SET TERM ;^

/* GET_CLIENT_INFO */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_CLIENT_INFO (
	CARD_NUMBER VARCHAR(700)
) 
RETURNS (
	FULL_FIO VARCHAR(765),
	AGE VARCHAR(10),
	GENGER VARCHAR(25),
	DATA_BIRTH DATE,
	PASSPORT VARCHAR(255),
	WHERE_PASSPORT VARCHAR(500),
	DATE_PASSPORT DATE,
	SNILS VARCHAR(255),
	INN VARCHAR(255),
	STATUS VARCHAR(255)
)
AS
BEGIN
    -- Инициализация выходных параметров
    FULL_FIO = NULL;
    AGE = NULL;
    GENGER = NULL;
    DATA_BIRTH = NULL;
    PASSPORT = NULL;
    WHERE_PASSPORT = NULL;
    DATE_PASSPORT = NULL;
    SNILS = NULL;
    INN = NULL;
    STATUS = NULL;

    -- Получение данных клиента на основе номера карты
    SELECT
        ci.FULL_FIO,
        ci.AGE,
        ci.GENGER,
        ci.DATA_BIRTH,
        ci.PASSPORT,
        ci.WHERE_PASSPORT,
        ci.DATE_PASSPORT,
        ci.SNILS,
        ci.INN,
        ci.STATUS
    FROM
        CLIENT_INFO ci
    JOIN
        CLIENT_CARD cc
    ON
        ci.FULL_FIO = cc.FK_CLIENT
    WHERE
        cc.NUMBER_CARD = :CARD_NUMBER
    INTO
        :FULL_FIO,
        :AGE,
        :GENGER,
        :DATA_BIRTH,
        :PASSPORT,
        :WHERE_PASSPORT,
        :DATE_PASSPORT,
        :SNILS,
        :INN,
        :STATUS;

    SUSPEND;
END
^
SET TERM ;^

/* GET_CLIENT_OPERATIONS */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_CLIENT_OPERATIONS (
	CARD_NUMBER_PARAM VARCHAR(700)
) 
RETURNS (
	CARD_NUM VARCHAR(700),
	OPERATION VARCHAR(255),
	DDATE_STAMP TIMESTAMP,
	COMMENT VARCHAR(1000)
)
AS
BEGIN
    -- Выборка данных о клиентских операциях
    FOR
        SELECT
            co.CARD_NUM,
            co.OPERATION,
            co.DDATE_STAMP,
            co.COMMENT
        FROM
            CLIENT_OPERATION co
        JOIN
            CLIENT_CARD cc
        ON
            co.CARD_NUM = cc.NUMBER_CARD
        WHERE
            cc.FK_CLIENT = (
                SELECT FK_CLIENT
                FROM CLIENT_CARD
                WHERE NUMBER_CARD = :CARD_NUMBER_PARAM
            )
        ORDER BY
            co.DDATE_STAMP DESC
        INTO
            :CARD_NUM,
            :OPERATION,
            :DDATE_STAMP,
            :COMMENT
    DO
        SUSPEND;
END
^
SET TERM ;^

/* GET_LAST_SERVICE_ID */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_LAST_SERVICE_ID
RETURNS (
	LAST_ID INTEGER
)
AS
BEGIN
    -- Поиск максимального значения в столбце ID_SERVICE
    SELECT MAX(ID_SERVICE)
    FROM SEVICE
    INTO :LAST_ID;

    SUSPEND;
END
^
SET TERM ;^

/* GET_MAX_ATM_CASH_ID */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_MAX_ATM_CASH_ID
RETURNS (
	MAX_ID_CASH VARCHAR(50)
)
AS
BEGIN
    -- Выполняем запрос для получения максимального ID_CASH
    SELECT MAX(ID_CASH)
    FROM ATM_CASH_STORAGE
    INTO :MAX_ID_CASH;
END
^
SET TERM ;^

/* GET_SERVICES */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_SERVICES
RETURNS (
	ID_SERVICE BIGINT,
	NAME_SERVICE VARCHAR(255),
	ACTIVE_STATUS BOOLEAN
)
AS
BEGIN
    FOR
        SELECT
            ID_SERVICE,
            NAME_SERVICE,
            ACTIVE_STATUS
        FROM
            SEVICE
        INTO
            :ID_SERVICE,
            :NAME_SERVICE,
            :ACTIVE_STATUS
    DO
        SUSPEND;
END
^
SET TERM ;^

/* GET_TOTAL_AMOUNT_IN_ATM */
SET TERM ^;
CREATE OR ALTER PROCEDURE GET_TOTAL_AMOUNT_IN_ATM
RETURNS (
	TOTAL_AMOUNT INTEGER
)
AS
BEGIN
    -- Вычисление общей суммы номиналов
    SELECT SUM(CAST(DENOMINATIONS AS INTEGER))
    FROM ATM_CASH_STORAGE
    INTO :TOTAL_AMOUNT;

    SUSPEND;
END
^
SET TERM ;^

/* INSERT_CASH */
SET TERM ^;
CREATE OR ALTER PROCEDURE INSERT_CASH (
	ID_CASH VARCHAR(50),
	ID_ATM VARCHAR(50),
	DENOMINATIONS VARCHAR(50),
	SERIAL_NUMBER VARCHAR(255)
) 
AS
BEGIN
    MERGE INTO ATM_CASH_STORAGE
    USING (SELECT :ID_CASH AS ID_CASH FROM RDB$DATABASE) AS source
    ON (ATM_CASH_STORAGE.ID_CASH = source.ID_CASH)
    WHEN MATCHED THEN
        UPDATE SET ID_ATM = :ID_ATM,
                   DENOMINATIONS = :DENOMINATIONS,
                   SERIAL_NUMBER = :SERIAL_NUMBER
    WHEN NOT MATCHED THEN
        INSERT (ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER)
        VALUES (:ID_CASH, :ID_ATM, :DENOMINATIONS, :SERIAL_NUMBER);
END
^
SET TERM ;^

/* INSERT_SERVICE */
SET TERM ^;
CREATE OR ALTER PROCEDURE INSERT_SERVICE (
	NAME_SERVICE VARCHAR(255),
	ACTIVE_STATUS VARCHAR(50)
) 
AS
BEGIN
    -- Вставка данных в таблицу SEVICE
    INSERT INTO SEVICE (NAME_SERVICE, ACTIVE_STATUS)
    VALUES (:NAME_SERVICE, :ACTIVE_STATUS);
END
^
SET TERM ;^

/* LOAD_ATM_BALANCE */
SET TERM ^;
CREATE OR ALTER PROCEDURE LOAD_ATM_BALANCE
RETURNS (
	ID_ATM VARCHAR(50),
	MAX_AMOUNT INTEGER,
	MIN_AMOUNT INTEGER,
	CURRENT_AMOUNT INTEGER
)
AS
BEGIN
    FOR SELECT ID_ATM, MAX_AMOUNT, MIN_AMOUNT, CURRENT_AMOUNT
        FROM ATM_BALANCE
        INTO :ID_ATM, :MAX_AMOUNT, :MIN_AMOUNT, :CURRENT_AMOUNT
    DO SUSPEND;
END
^
SET TERM ;^

/* LOAD_CASH_STORAGE */
SET TERM ^;
CREATE OR ALTER PROCEDURE LOAD_CASH_STORAGE
RETURNS (
	ID_CASH VARCHAR(50),
	ID_ATM VARCHAR(50),
	DENOMINATIONS DOUBLE PRECISION,
	SERIAL_NUMBER VARCHAR(50),
	DATE_INSERTED TIMESTAMP
)
AS
BEGIN
    FOR SELECT ID_CASH, ID_ATM, DENOMINATIONS, SERIAL_NUMBER, DATE_INSERTED
        FROM ATM_CASH_STORAGE
        INTO :ID_CASH, :ID_ATM, :DENOMINATIONS, :SERIAL_NUMBER, :DATE_INSERTED
    DO SUSPEND;
END
^
SET TERM ;^

/* LOG_OPERATION */
SET TERM ^;
CREATE OR ALTER PROCEDURE LOG_OPERATION (
	CARD_NUM VARCHAR(700),
	OPERATION VARCHAR(255),
	DDATE_STAMP TIMESTAMP,
	COMMENT VARCHAR(1000)
) 
AS
BEGIN
    -- Вставка данных в таблицу CLIENT_OPERATION
    INSERT INTO CLIENT_OPERATION (CARD_NUM, OPERATION, DDATE_STAMP, COMMENT)
    VALUES (:CARD_NUM, :OPERATION, :DDATE_STAMP, :COMMENT);
END
^
SET TERM ;^

/* UPDATE_BALANCE */
SET TERM ^;
CREATE OR ALTER PROCEDURE UPDATE_BALANCE (
	AMOUNT DECIMAL(15,2),
	FK_CARD VARCHAR(16)
) 
AS
BEGIN
    -- Выполняем обновление баланса
    UPDATE BALANCE_CARD
    SET BALANCE = BALANCE - :AMOUNT
    WHERE BALANCE_CARD.FK_CARD = :FK_CARD;

    -- Если нужно, можно добавить проверку на успешность обновления
    IF (ROW_COUNT = 0) THEN
    BEGIN
        -- Если запись не найдена, выбрасываем исключение
        EXCEPTION UNKNOWN_CARD;
    END
END
^
SET TERM ;^

/* UPDATE_CURRENT_AMOUNT */
SET TERM ^;
CREATE OR ALTER PROCEDURE UPDATE_CURRENT_AMOUNT (
	NEW_AMOUNT INTEGER,
	ATM_ID VARCHAR(50)
) 
AS
BEGIN
    UPDATE ATM_BALANCE
    SET CURRENT_AMOUNT = :NEW_AMOUNT
    WHERE ID_ATM = :ATM_ID;
END
^
SET TERM ;^

/* UPDATE_SERVICE_STATUS */
SET TERM ^;
CREATE OR ALTER PROCEDURE UPDATE_SERVICE_STATUS (
	NEW_ACTIVE_STATUS BOOLEAN,
	SERVICE_ID INTEGER
) 
AS
BEGIN
    UPDATE SEVICE
    SET ACTIVE_STATUS = :NEW_ACTIVE_STATUS
    WHERE ID_SERVICE = :SERVICE_ID;
END
^
SET TERM ;^

/* ----- Creating Table Triggers ----- */

/* ATM_CASH_STORAGE_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER ATM_CASH_STORAGE_BI FOR ATM_CASH_STORAGE
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID_CASH IS NULL) THEN
NEW.ID_CASH = GEN_ID(SEQ_ATM_CASH_STORAGE_ID_CASH,1);
END^
SET TERM ;^

/* BALANCE_CARD_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER BALANCE_CARD_BI FOR BALANCE_CARD
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_BALANCE_CARD_ID",1);
END^
SET TERM ;^

/* BANK_DIC_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER BANK_DIC_BI FOR BANK_DIC
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_BANK_DIC_ID",1);
END^
SET TERM ;^

/* CLIENT_CARD_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER CLIENT_CARD_BI FOR CLIENT_CARD
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_CLIENT_CARD_ID",1);
END^
SET TERM ;^

/* CLIENT_INFO_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER CLIENT_INFO_BI FOR CLIENT_INFO
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_CLIENT_INFO_ID",1);
END^
SET TERM ;^

/* CLIENT_OPERATION_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER CLIENT_OPERATION_BI FOR CLIENT_OPERATION
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_CLIENT_OPERATION_ID",1);
END^
SET TERM ;^

/* COMPANY_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER COMPANY_BI FOR COMPANY
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_COMPANY_ID",1);
END^
SET TERM ;^

/* DIC_CARD_TYPE_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER DIC_CARD_TYPE_BI FOR DIC_CARD_TYPE
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_DIC_CARD_TYPE_ID",1);
END^
SET TERM ;^

/* DIC_OPERATION_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER DIC_OPERATION_BI FOR DIC_OPERATION
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_DIC_OPERATION_ID",1);
END^
SET TERM ;^

/* SEVICE_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER SEVICE_BI FOR SEVICE
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID_SERVICE IS NULL) THEN
NEW.ID_SERVICE = GEN_ID("SEQ_SEVICE_ID_SERVICE",6);
END^
SET TERM ;^

/* USERS_BI */
SET TERM ^;
CREATE OR ALTER TRIGGER USERS_BI FOR USERS
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW.ID IS NULL) THEN
NEW.ID = GEN_ID("SEQ_USERS_ID",1);
END^
SET TERM ;^

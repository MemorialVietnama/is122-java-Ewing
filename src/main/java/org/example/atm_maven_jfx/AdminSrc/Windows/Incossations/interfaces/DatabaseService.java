package org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.interfaces;


import org.example.atm_maven_jfx.AdminSrc.Windows.Incossations.Incantations;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface DatabaseService {
    Connection getConnection() throws SQLException;

    void insertCashIntoDatabase(String idCash, String idAtm, int denomination, String serialNumber) throws SQLException;

    void removeCashFromDatabase(List<String> cashIds) throws SQLException;

    Map<Integer, Integer> getCurrentCashCount() throws SQLException;

    List<Incantations.CashStorage> getCashToRemove(int amountToRemove) throws SQLException;
}
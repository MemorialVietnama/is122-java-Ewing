package org.example.atm_jfx.AdminSrc.Interface;

import java.sql.SQLException;

public interface DatabaseLoader {
    void loadStats() throws SQLException;
}

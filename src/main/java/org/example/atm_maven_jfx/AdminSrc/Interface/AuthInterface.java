package org.example.atm_maven_jfx.AdminSrc.Interface;

import javafx.scene.Scene;

import java.sql.SQLException;

public interface AuthInterface {
    boolean authenticateUser(String username, String password);

    void switchToAdminLayout(Scene previousScene) throws SQLException;
}

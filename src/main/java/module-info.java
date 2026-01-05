module EksamMovie {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.naming;
    requires java.sql;
    requires java.desktop;
    requires com.microsoft.sqlserver.jdbc;

    opens GUI to javafx.fxml;
    opens BE to javafx.fxml;

    exports GUI;
}

module scheduling.demoschedulingapp {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;


    opens scheduling.demoschedulingapp to javafx.fxml;
    exports scheduling.demoschedulingapp;
    exports scheduling.demoschedulingapp.Controllers;
    opens scheduling.demoschedulingapp.Controllers to javafx.fxml;

}
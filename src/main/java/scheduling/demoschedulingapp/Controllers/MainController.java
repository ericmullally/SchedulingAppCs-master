package scheduling.demoschedulingapp.Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Tab;

import java.util.HashMap;
import java.util.Locale;

public class MainController {

    @FXML
    Tab manageCustomerTabLbl, manageAppointmentTabLbl, reportsTabLbl, logsTabLbl;

    @FXML
    public void initialize(){
        setLanguage();
    }

    private void setLanguage(){
        HashMap<String, String> FrenchMainTabs = new HashMap<String, String>(){
            {
                put("manage customers","Gérer les clients");
                put("manage appointments","Gérer les rendez-vous");
                put("Reports","Rapports");
                put("logs","Journaux");
            }
        };

        if(Locale.getDefault().getLanguage() == "fr"){
            manageCustomerTabLbl.setText(FrenchMainTabs.get("manage customers"));
            manageAppointmentTabLbl.setText(FrenchMainTabs.get("manage appointments"));
            reportsTabLbl.setText(FrenchMainTabs.get("Reports"));
            logsTabLbl.setText(FrenchMainTabs.get("logs"));
        }
    }
}

package scheduling.demoschedulingapp.Controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Tab;
import java.util.HashMap;
import java.util.Locale;

public class MainController {

    @FXML
    Tab manageCustomerTabLbl, viewAppointmentsTab,  manageAppointmentTabLbl, reportsTabLbl, logsTabLbl;

    @FXML
    public void initialize(){
        setLanguage();
    }

    /**
     * sets language to french if it detects the host computer is using french.
     * contains lambda.
     */
    private void setLanguage(){
        HashMap<Tab, String> FrenchMainTabs = new HashMap<Tab, String>(){
            {
                put(manageCustomerTabLbl,"Gérer les clients");
                put(manageAppointmentTabLbl,"Gérer les rendez-vous");
                put(reportsTabLbl,"Rapports");
                put(logsTabLbl,"Journaux");
            }
        };

        if(Locale.getDefault().getLanguage() == "fr"){
            FrenchMainTabs.forEach((k,v) -> k.setText(v));
        }
    }

    public static void refreshList(String nameOfController){
        switch(nameOfController){
            case "addCustomerController":
                ManageCustomersController.buildCustomerList();
                break;
            case "AddAppointmentController":
                ManageAppointmentsController.buildAppointmentList();
                break;
            default:
                return;
        }
   }



}


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
     *
     * Lambda expression justification:
     * the map of french tabs is iterable and could be operated on with
     * a normal for loop. however the built in method forEach uses the
     * same time cost and is much cleaner. though a little harder to read,
     * it substantially cuts down on the functions size.
     *
     * sets language to french if it detects the host computer is using french.
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

    /**
     * sends a refresh request to manage customers controller or manage appointments controller.
     * whenever an appointment or customer is added.
     * @param nameOfController String the name of the controller who called this function.
     */
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


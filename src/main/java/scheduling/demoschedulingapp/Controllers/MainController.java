package scheduling.demoschedulingapp.Controllers;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;



public class MainController {

    public MainController(){

    }

    public void updateCustomer(){

    }

    public void addCustomer(){

    }

    public void scheduleAppointment(){

    }

    public void viewAppointments(){

    }

    public void deleteCustomer(){

    }

    @FXML
    public void changeMainScene(ActionEvent e){

        e.consume();
        String tabId = ((Button)e.getSource()).getId().split("_")[1];

        switch(tabId){
            case "Customers":
                System.out.println("Customer Tab");
                break;
            case "Appointments":
                System.out.println("Appointments tab");
                break;
            case "Reports":
                System.out.println("Reports tab");
                break;
            case "Logs":
                System.out.println("Logs tab");
                break;
            default:
                System.out.println("You have a problem in changeMainScene");
        }
    }
}

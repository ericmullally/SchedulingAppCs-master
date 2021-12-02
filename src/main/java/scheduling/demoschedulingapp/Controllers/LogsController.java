package scheduling.demoschedulingapp.Controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import scheduling.demoschedulingapp.Classes.Log;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;

/**
 * controls all login activity.
 */
public class LogsController {

    @FXML
    TableView<Log> logsTable;
    @FXML
    TableColumn<Log, String> userNameCol, timeCol, dateCol, successCol;

    private ObservableList<Log> logs = FXCollections.observableArrayList();

    @FXML
    public void initialize(){
            userNameCol.setCellValueFactory(new PropertyValueFactory<>("userName"));
            timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
            successCol.setCellValueFactory(new PropertyValueFactory<>("success"));
            buildList();
            fillTable();
    }

    /**
     * builds the list of log objects.
     */
    private void buildList(){

        try{
            BufferedReader fr = new BufferedReader( new FileReader("login_activity.txt"));
            String line = fr.readLine();
            while(line != null){
                String[] logList= line.split("_");
                String name = logList[0];
                String success = logList[1];
                String dateTime = logList[2];
                String date = LocalDate.parse(dateTime.split("T")[0]).toString();
                String time = LocalTime.parse(dateTime.split("T")[1]).toString();
                Log newLog = new Log(name, time, date, success);
                logs.add(newLog);
                line = fr.readLine();
            }
            fr.close();

        }catch(IOException e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * fills the log table.
     */
    private void fillTable(){
        logsTable.setItems(logs);
    }


}

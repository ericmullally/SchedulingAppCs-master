package scheduling.demoschedulingapp.Classes;


/***
 * creates a customer object which is highly memory inefficient since I'm going to have to make a class for anything i want to display,
 * then store it in memory while my table loads. but since this is the only way to fill a table in java here we are.
 */
public class Customer {
    private String customer_id;
    private String name;
    private String create_date;
    private String phone;
    private String lastUpdate;


    public  Customer(String customer_id,String  name, String create_date,String phone, String lastUpdate){
        this.customer_id = customer_id;
        this.name = name;
        this.create_date = create_date;
        this.phone = phone;
        this.lastUpdate = lastUpdate;
    }

    public String getCustomer_id(){
        return customer_id;
    }
    public String getCreate_date(){
        return create_date;
    }
    public String getPhone() { return phone; }
    public String getLastUpdate(){
        return lastUpdate;
    }
    public String getName(){
        return name;
    }

    public void setCustomer_id(String  customer_id){
        this.customer_id = customer_id;
    }
    public void setCreate_date(String create_date ){
        this.create_date = create_date;
    }
    public void setPhone(String phone){
        this.phone = phone;
    }
    public void setLastUpdate(String  lastUpdate){
        this.lastUpdate = lastUpdate;
    }
    public void setName(String name){
        this.name = name;
    }
}

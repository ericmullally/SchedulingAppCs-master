package scheduling.demoschedulingapp.Classes;


/***
 * creates a customer object which is highly memory inefficient since I'm going to have to make a class for anything i want to display,
 * then store it in memory while my table loads. but since this is the only way to fill a table in java here we are.
 */
public class Customer {
    private String customer_id;
    private String name;
    private String address;
    private String zip;
    private String phone;
    private String create_date;
    private String createdBy;
    private String lastUpdate;
    private String lastUpdatedBy;
    private int divisionID;


    public Customer(String customer_id, String name, String address, String zip, String phone,
                    String create_date, String createdBy, String lastUpdate, String lastUpdatedBy, int divisionID) {
        this.customer_id = customer_id;
        this.name = name;
        this.create_date = create_date;
        this.phone = phone;
        this.lastUpdate = lastUpdate;
        this.address = address;
        this.zip = zip;
        this.lastUpdatedBy = lastUpdatedBy;
        this.createdBy = createdBy;
        this.divisionID = divisionID;

    }

    public String getCustomer_id() {
        return customer_id;
    }
    public String getCreate_date() {
        return create_date;
    }
    public String getPhone() {
        return phone;
    }
    public String getLastUpdate() {
        return lastUpdate;
    }
    public String getName() {
        return name;
    }
    public String getAddress(){ return this.address; }
    public String getZip() { return zip; }
    public String getLastUpdatedBy(){return lastUpdatedBy;}
    public String getCreatedBy(){return createdBy;}
    public int getDivisionID(){return divisionID;}


    public void setAddress(String address){this.address = address;}
    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }
    public void setCreate_date(String create_date) {
        this.create_date = create_date;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
    public void setZip(String zip) { this.zip = zip; }
    public void setLastUpdatedBy(String  lastUpdatedBy){this.lastUpdatedBy= lastUpdatedBy;}
    public void setCreatedBy(String lastUpdatedBy){this.lastUpdatedBy= lastUpdatedBy;}
    public void setDivisionID(int ID){this.divisionID = ID;}
}

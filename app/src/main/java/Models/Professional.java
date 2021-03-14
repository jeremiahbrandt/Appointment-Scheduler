package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Professional {
    private static Professional professional;

    private Account account;
    private Address address;
    private String profession;
    private String shareableCode;
    private String uid;
    private Appointment[] appointments;

    // Create Professional from jObject
    public Professional(JSONObject jsonObject) {
        try {
            this.account = new Account(jsonObject.getJSONObject("account"));
            this.address = new Address(jsonObject.getJSONObject("address"));
            this.profession = jsonObject.getString("name");
            this.shareableCode = jsonObject.getString("shareableCode");
            this.uid = jsonObject.getString("uid");

            JSONArray appointments = jsonObject.getJSONArray("appointments");
            this.appointments = new Appointment[appointments.length()];
            for(int i=0; i<appointments.length(); i++) {
                this.appointments[i] = new Appointment(appointments.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Account getAccount() {
        return account;
    }

    public Address getAddress() {
        return address;
    }

    public String getProfession() {
        return profession;
    }

    public String getShareableCode() {
        return shareableCode;
    }

    public String getUid() {
        return  uid;
    }

    public Appointment[] getAppointments() {
        return appointments;
    }
}
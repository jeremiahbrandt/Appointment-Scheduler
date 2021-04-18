package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Client {
    private String firstName;
    private String lastName;
    private Appointment[] appointments;

    public Client(JSONObject jsonObject) {
        try {
            this.firstName = jsonObject.getString("firstName");
            this.lastName = jsonObject.getString("lastName");

            JSONArray appointments = jsonObject.getJSONArray("appointments");
            this.appointments = new Appointment[appointments.length()];
            for(int i=0; i<appointments.length(); i++) {
                // this.appointments[i] = new Appointment(appointments.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Appointment[] getAppointments() {
        return appointments;
    }
}

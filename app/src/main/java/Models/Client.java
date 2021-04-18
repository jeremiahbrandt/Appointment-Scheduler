package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Client {
    private String firstName;
    private String lastName;
    private ClientAppointment[] appointments;

    public Client(JSONObject jsonObject) {
        try {
            this.firstName = jsonObject.getString("firstName");
            this.lastName = jsonObject.getString("lastName");

            JSONArray appointments = jsonObject.getJSONArray("appointments");
            this.appointments = new ClientAppointment[appointments.length()];
            for(int i=0; i<appointments.length(); i++) {
                 this.appointments[i] = new ClientAppointment(appointments.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List getAppointmentsStringList() {
        List<String> appointments = new ArrayList<>();
        for(ClientAppointment appointment : this.appointments) {
            appointments.add(appointment.getStartTime() + "\t| " + appointment.getAppointmentName() + "\t|" + appointment.getProfessionalLastName() + ", " + appointment.getProfessionalFirstName() + "\t| " + appointment.getOccupation());
        }
        return appointments;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public ClientAppointment[] getAppointments() {
        return this.appointments;
    }
}

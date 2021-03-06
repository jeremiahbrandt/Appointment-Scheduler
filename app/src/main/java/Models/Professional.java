package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Professional {
    private String firstName;
    private String lastName;
    private String emailAddress;
    private int streetNumber;
    private String streetName;
    private String city;
    private String state;
    private int zipCode;
    private String profession;
    private String shareableCode;
    private ProfessionalAppointment[] appointments;

    // Create Professional from jObject
    public Professional(JSONObject jsonObject) {
        try {
            this.firstName = jsonObject.getString("firstName");
            this.lastName = jsonObject.getString("lastName");
            this.emailAddress = jsonObject.getString("email");
            this.streetNumber = jsonObject.getInt("streetNumber");
            this.streetName = jsonObject.getString("streetName");
            this.city = jsonObject.getString("city");
            this.state = jsonObject.getString("state");
            this.zipCode = jsonObject.getInt("zipCode");
            this.profession = jsonObject.getString("occupation");
            this.shareableCode = jsonObject.getString("shareableCode");

            JSONArray appointments = jsonObject.getJSONArray("appointments");
            this.appointments = new ProfessionalAppointment[appointments.length()];
            for(int i=0; i<appointments.length(); i++) {
                this.appointments[i] = new ProfessionalAppointment(appointments.getJSONObject(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public List getAppointmentsStringList() {
        List<String> appointments = new ArrayList<>();
        for(ProfessionalAppointment professionalAppointment : this.appointments) {
            appointments.add(professionalAppointment.getStartTime() + "\t| " + professionalAppointment.getName() + "\t|" + professionalAppointment.getClientLastName() + ", " + professionalAppointment.getClientFirstName());
        }
        return appointments;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public int getStreetNumber() {
        return streetNumber;
    }

    public String getStreetName() {
        return streetName;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public int getZipCode() {
        return zipCode;
    }

    public String getProfession() {
        return profession;
    }

    public String getShareableCode() {
        return shareableCode;
    }

    public ProfessionalAppointment[] getAppointments() {
        return appointments;
    }
}
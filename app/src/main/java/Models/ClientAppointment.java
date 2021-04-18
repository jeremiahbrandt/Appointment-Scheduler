package Models;

import org.json.JSONObject;

import java.time.LocalDateTime;

import util.ApiEndpointProvider;

public class ClientAppointment {
    private int timeSlotId;
    private String appointmentName;
    private String appointmentDescription;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String professionalFirstName;
    private String professionalLastName;
    private String occupation;
    private String shareableCode;
    private int streetNumber;
    private String streetName;
    private String city;
    private String state;
    private String zipCode;

    public ClientAppointment(JSONObject jsonObject) {
        try {
            String start = jsonObject.getString("startTime") + ", CST";
            String end = jsonObject.getString("endTime") + ", CST";

            this.timeSlotId = jsonObject.getInt("timeSlotId");
            this.appointmentName = jsonObject.getString("appointmentName");
            this.appointmentDescription = jsonObject.getString("appointmentDescription");
            this.startTime = LocalDateTime.parse(start, ApiEndpointProvider.format);
            this.endTime = LocalDateTime.parse(end, ApiEndpointProvider.format);
            this.professionalFirstName = jsonObject.getString("professionalFirstName");
            this.professionalLastName = jsonObject.getString("professionalLastName");
            this.occupation = jsonObject.getString("occupation");
            this.shareableCode = jsonObject.getString("shareableCode");
            this.streetNumber = jsonObject.getInt("streetNumber");
            this.streetName = jsonObject.getString("streetName");
            this.city = jsonObject.getString("city");
            this.state = jsonObject.getString("state");
            this.zipCode = jsonObject.getString("zipCode");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public int getTimeSlotId() {
        return timeSlotId;
    }

    public String getAppointmentName() {
        return appointmentName;
    }

    public String getAppointmentDescription() {
        return appointmentDescription;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getProfessionalFirstName() {
        return professionalFirstName;
    }

    public String getProfessionalLastName() {
        return professionalLastName;
    }

    public String getOccupation() {
        return occupation;
    }

    public String getShareableCode() {
        return shareableCode;
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

    public String getZipCode() {
        return zipCode;
    }
}

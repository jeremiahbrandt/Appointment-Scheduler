package Models;

import android.widget.ArrayAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

import util.ApiEndpointProvider;

public class Appointment {
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String name;
    private String description;
    private String clientFirstName;
    private String clientLastName;

    public Appointment(JSONObject jsonObject) {
        try {
            String start = jsonObject.getString("startTime") + ", CST";
            String end = jsonObject.getString("endTime") + ", CST";

            this.startTime = LocalDateTime.parse(start, ApiEndpointProvider.format);
            this.endTime = LocalDateTime.parse(end, ApiEndpointProvider.format);
            this.name = jsonObject.getString("appointmentName");
            this.description = jsonObject.getString("appointmentDescription");
            this.clientFirstName = jsonObject.getString("clientFirstName");
            this.clientLastName = jsonObject.getString("clientLastName");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getClientFirstName() {
        return clientFirstName;
    }

    public String getClientLastName() {
        return clientLastName;
    }
}

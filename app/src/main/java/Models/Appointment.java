package Models;

import com.google.type.DateTime;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;

public class Appointment {
    private String name;
    private String description;
    private String location;
    private Account otherAccount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public Appointment(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.location = jsonObject.getString("location");
            this.otherAccount = new Account(jsonObject.getJSONObject("account"));
            this.startTime = LocalDateTime.parse(jsonObject.getString("startTime"));
            this.endTime = LocalDateTime.parse(jsonObject.getString("endTime"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getLocation() {
        return location;
    }

    public Account getOtherAccount() {
        return otherAccount;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}

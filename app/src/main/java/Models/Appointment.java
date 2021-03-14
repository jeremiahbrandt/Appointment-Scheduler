package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Appointment {

    private String name;
    private String description;
    private String location;

    public Appointment(JSONObject jsonObject) {
        try {
            this.name = jsonObject.getString("name");
            this.description = jsonObject.getString("description");
            this.location = jsonObject.getString("location");
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
}

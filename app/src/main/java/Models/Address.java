package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Address {
    private int streetNumber;
    private String streetName;
    private String city;
    private int zipCode;

    protected Address(JSONObject jsonObject) {
        try {
            this.streetNumber = jsonObject.getInt("streetNumber");
            this.streetName = jsonObject.getString("streetName");
            this.city = jsonObject.getString("city");
            this.zipCode = jsonObject.getInt("zipCode");
        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    public int getZipCode() {
        return zipCode;
    }
}

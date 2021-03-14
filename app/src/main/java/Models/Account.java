package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class Account {
    private String firstName;
    private String lastName;
    private String emailAddress;

    protected Account(JSONObject jsonObject) {
        try {
            this.firstName = jsonObject.getString("firstName");
            this.lastName = jsonObject.getString("lastName");
            this.emailAddress = jsonObject.getString("emailAddress");
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

    public String getEmailAddress() {
        return emailAddress;
    }
}

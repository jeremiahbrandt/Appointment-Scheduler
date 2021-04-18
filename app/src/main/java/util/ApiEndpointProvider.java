package util;

import java.time.format.DateTimeFormatter;

public class ApiEndpointProvider {
    public static DateTimeFormatter format = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss, z");
    public static String url = "https://se3910projectapi.azurewebsites.net/api";
}

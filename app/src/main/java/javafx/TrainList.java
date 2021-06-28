package javafx;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedList;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TrainList {
    String stationName;
    LinkedList<Train> trains;

    static JSONArray getHTTPData() throws IOException, InterruptedException {
        String urlString = "https://api.tfl.gov.uk/StopPoint/910GSTKNWNG/Arrivals";
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        JSONArray myObject = new JSONArray(response.body());
        return myObject;
    }

    public TrainList(String stationName) {
        this.stationName = stationName;
        this.trains = new LinkedList<Train>();
        try {
            JSONArray trainArray = getHTTPData();
            for (int i = 0; i < trainArray.length(); i++) {
                JSONObject singleTrain = trainArray.getJSONObject(i);
                String destination = singleTrain.getString("destinationName");
                int timeToStation = singleTrain.getInt("timeToStation");
                Train element = new Train(destination, timeToStation);

                this.trains.add(element);
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
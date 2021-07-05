package javafx;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.LinkedList;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;  

public class TrainList {
    String stationName;
    String stationID;
    LinkedList<Train> trains;

    static HttpResponse<String> getHTTPData(String urlString) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(urlString)).build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        return response;
    }

    private void parseTrainsArray(JSONArray trainArray) throws ParseException {
        this.trains.clear();
        for (int i = 0; i < trainArray.length(); i++) {
            JSONObject singleTrain = trainArray.getJSONObject(i);
            String destination = singleTrain.getString("destinationName");
            int timeToStation = singleTrain.getInt("timeToStation");
            String arrivalTimeStr = singleTrain.getString("expectedArrival");
            Date arrivalTime = new SimpleDateFormat("yyyy-MM-DD'T'HH:mm:ssZ").parse(arrivalTimeStr.replaceAll("Z$", "+0000"));
            String ID = singleTrain.getString("vehicleId");

            Train element = new Train(destination, timeToStation, arrivalTime, ID);

            if (i == 0) {
                this.trains.add(element);
                continue;
            }

            for (int j = 0; j < trains.size(); j++) {
                int curTimeToStation = trains.get(j).timeToStation;
                if (curTimeToStation > timeToStation) {
                    this.trains.add(j, element);
                    break;
                }
                if (j + 1 == trains.size()) {
                    this.trains.add(element);
                    break;
                }
            }
        }
    }

    public TrainList(String stationName) {
        this.stationName = stationName;
        this.trains = new LinkedList<Train>();
        try {
            String urlString = "https://api.tfl.gov.uk/StopPoint/910GSTKNWNG/Arrivals";
            HttpResponse<String> response = getHTTPData(urlString);
            JSONArray trainArray = new JSONArray(response.body());
            parseTrainsArray(trainArray);
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }

    public void updateStationName(String newName) {
        this.stationName = newName;
    }

    public void updateStationID() {
        try {
            String urlString = "https://api.tfl.gov.uk/StopPoint/Search/"+this.stationName.replace(" ", "%20");
            HttpResponse<String> response = getHTTPData(urlString);
            JSONObject trainObject = new JSONObject(response.body());
            if (trainObject.getInt("total") == 0) {
                return;
            }

            String stationID = trainObject.getJSONArray("matches").getJSONObject(0).getString("id");
            this.stationID = stationID;
            updateTrains();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void updateTrains() {
        try {
            String urlString = "https://api.tfl.gov.uk/StopPoint/"+this.stationID+"/Arrivals";
            HttpResponse<String> response = getHTTPData(urlString);
            JSONArray trainArray = new JSONArray(response.body());
            parseTrainsArray(trainArray);
        } catch (IOException | InterruptedException | ParseException e) {
            e.printStackTrace();
        }
    }
}
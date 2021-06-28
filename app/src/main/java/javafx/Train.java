package javafx;

import java.util.Date;

public class Train {
    String destination;
    int timeToStation;
    Date arrivalTime;

    public Train(String destination, int timeToStation, Date arrivalTime) {
        this.destination = destination;
        this.timeToStation = timeToStation;
        this.arrivalTime = arrivalTime;
    }
}
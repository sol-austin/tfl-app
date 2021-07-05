package javafx;

import java.util.Date;

public class Train {
    String destination;
    int timeToStation;
    Date arrivalTime;
    String ID;

    public Train(String destination, int timeToStation, Date arrivalTime, String ID) {
        this.destination = destination;
        this.timeToStation = timeToStation;
        this.arrivalTime = arrivalTime;
        this.ID = ID;
    }
}
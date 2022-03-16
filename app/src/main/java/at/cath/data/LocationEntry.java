package at.cath.data;

import android.location.Location;

import androidx.annotation.NonNull;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LocationEntry extends SelectableEntry {

    private String name;
    private final Location location;
    private final LocalDateTime dateTaken;

    public LocationEntry(Location location) {
        this.location = location;
        this.dateTaken = LocalDateTime.now();
    }

    @NonNull
    @Override
    public String toString() {
        return location.getLongitude() + ", " + location.getLatitude();
    }

    public String getDateTakenFormatted() {
        return dateTaken.format(DateTimeFormatter.ofPattern("dd/MM 'at' hh:mma"));
    }

    public Location getLocation() {
        return this.location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

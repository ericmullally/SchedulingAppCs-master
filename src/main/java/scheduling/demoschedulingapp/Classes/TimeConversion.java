package scheduling.demoschedulingapp.Classes;


import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * converts Times between zones.
 */
public class TimeConversion {

    public static ZonedDateTime convertTimes(ZonedDateTime timeToConvert, ZoneId convertToZone ){return timeToConvert.withZoneSameInstant(convertToZone);}

}

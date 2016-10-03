
package com.geoslab.tracking.persistence.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "LOCATIONS")
public class Location extends Measurement {

    private Double latitude;
    // private String nsIndicator;

    private Double longitude;
    // private String ewIndicator;

    @ManyToOne
    @JoinColumn(name = "node")
    private Node node;
    
    private static DateFormat df = new SimpleDateFormat("hhmmss.SSS");

    public Location(Double latitude, Double longitude, Node node, long time) {
        super();
        this.latitude = latitude;
        this.longitude = longitude;
        this.node = node;
        this.time = time;
    }

    /**********************/
    /** Constructor */
    /**********************/
    public Location() {
    }

    public Location(String latitude, String nsIndicator, String longitude,
            String ewIndicator, Node node, long time) {
        if (nsIndicator == null) {
            // por defecto norte
            nsIndicator = "N";

        } else
        if (nsIndicator.equalsIgnoreCase("")) {
            // por defecto norte
            nsIndicator = "N";
        }
        this.latitude = Location.nmeaLatOrLongToWGS84(latitude, nsIndicator);

        if (ewIndicator == null) {
            // por defecto oeste
            ewIndicator = "W";

        } else
        if (ewIndicator.equalsIgnoreCase("")) {
            // por defecto oeste
            ewIndicator = "W";

        }
        this.longitude = Location.nmeaLatOrLongToWGS84(longitude, ewIndicator);

        this.node = node;
        this.time = time;
    }

    public Location(String latitude, String nsIndicator, String longitude,
            String ewIndicator, Node node, String nmeaUtcTime) {

        this(latitude, nsIndicator, longitude,
            ewIndicator, node,Location.fromNmeaUtcToMilis(nmeaUtcTime));
        
    }
    
    
    /**********************/
    /** Getters & Setters */
    /**********************/

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }

    /**
     * Converts the String format used in NMEA messages to represent longitude
     * or latitude to a double value (degrees). NMEA uses e.g. 4916.45 to
     * represent 49 degrees, 16.45 minutes (latitude) or 02311.12 for 23 degrees
     * 11.12 minutes (longitude). So this method converts e.g. 4916.45 to the
     * WGS84 system representation (degrees only) which is 49.27416 degrees.
     * 
     * @param nmea_pos
     *            the NMEA string representation for global position.
     * @param bearing
     *            the letter identifying latitude or longitude and hemisphere,
     *            i.e. N or W
     * @exception NumberFormatException
     *                if the String could not be converted.
     */
    public static double nmeaLatOrLongToWGS84(String nmea_pos, String bearing)
            throws NumberFormatException {
        int comma_pos = nmea_pos.indexOf('.');
        if ((comma_pos != 4) && (comma_pos != 5))
            throw new NumberFormatException("Unknown NMEA position format: '"
                    + nmea_pos + "'");
        // convert to wgs84
        String wgs84_deg = nmea_pos.substring(0, comma_pos - 2);
        String wgs84_min = nmea_pos.substring(comma_pos - 2);
        double wgs84_pos = Double.parseDouble(wgs84_deg)
                + Double.parseDouble(wgs84_min) / 60.0;
        // sign and type of coordinate
        double sign = 1;
        boolean isLat = false;
        if ("N".equals(bearing) || "S".equals(bearing))
            isLat = true;
        if ("S".equals(bearing) || "W".equals(bearing))
            sign = -1;
        // check bounds
        if (isLat) {
            if (wgs84_pos > 90.0) {
                throw new NumberFormatException("Latitude out of bounds: "
                        + sign * wgs84_pos);
            }
        }
        else {
            if (wgs84_pos > 180.0) {
                throw new NumberFormatException("Longitude out of bounds: "
                        + sign * wgs84_pos);
            }
        }
        return (sign * wgs84_pos);
    }

    public String getNmeaLatitude() {
        return Location.formatLatitude(latitude);
    }

    public String getNmeaLongitude() {
        return Location.formatLongitude(longitude);
    }

    public String getNsIndicator() {
        if (this.latitude > 0)
            return "N";
        else
            return "S";
    }

    public String getEwIndicator() {
        if (this.longitude > 0)
            return "E";
        else
            return "W";
    }

    /**
     * @return the latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @return the longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    public static String formatLatitude(double degrees)
    {
        int d = (int) Math.floor(Math.abs(degrees));
        double m = 60 * (Math.abs(degrees) - d);
        // Format latitude as "DDMM.MMM[N|S]"
        return String.format("%02d%06.3f", d, m).replace(',','.');
    }

    public static String formatLongitude(double degrees)
    {
        int d = (int) Math.floor(Math.abs(degrees));
        double m = 60 * (Math.abs(degrees) - d);
        // Format longitude as "DDDMM.MMM[N|S]"
        return String.format("%03d%06.3f", d, m).replace(',','.');
    }

    public static long fromNmeaUtcToMilis(String utc) {
        Calendar c = Calendar.getInstance();
        try {
            Date date = df.parse(utc);
            c.set(Calendar.HOUR_OF_DAY, date.getHours());
            c.set(Calendar.MINUTE, date.getMinutes());
            c.set(Calendar.SECOND, date.getSeconds());
        }
        catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return c.getTimeInMillis();

    }

}

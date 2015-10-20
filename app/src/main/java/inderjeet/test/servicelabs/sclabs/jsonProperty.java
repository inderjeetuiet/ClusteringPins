package inderjeet.test.servicelabs.sclabs;

/**
 * Created by isingh on 8/30/15.
 */
public class jsonProperty {

    public String address = null;
    public String city = null;
    public String country = null;
    public String lastupdate = null;
    public String machine = null;
    public String note = null;
    public int number;
    public double latitute;
    public double longitude;
    public String type = null;
    public String place = null;
    public int stars;
    public String street = null;
    public int zipcode;

    /**
     * private constructer for applying factory design pattern
     * @param address : addres from the JSON object
     * @param city :  city from the JSON object
     * @param country :  country from the JSON object
     * @param lastupdate : lastupdated from the JSON object
     * @param machine : machine from the JSON object
     * @param note : node from the JSON object
     * @param latitute :latitude from the JSON object
     * @param longitude :longitude from the JSON object
     * @param type :type from the JSON object
     * @param place : place from the JSON object
     * @param stars : stars from the JSON object
     * @param street :street from the JSON object
     * @param zipcode : zipcode from the JSON object
     */
    private jsonProperty(String address, String city, String country, String lastupdate, String machine, String note,
                         double latitute, double longitude, String type, String place, int stars, String street, int zipcode)
    {
        this.address = address;
        this.city = city;
        this.country = country;
        this.lastupdate = lastupdate;
        this.machine = machine;
        this.note = note;
        this.number = number;
        this.latitute = latitute;
        this.longitude = longitude;
        this.type = type;
        this.place = place;
        this.stars = stars;
        this.street = street;
        this.zipcode = zipcode;
    }

    /**
     * Factory method to save the object
     * @param address : addres from the JSON object
     * @param city :  city from the JSON object
     * @param country :  country from the JSON object
     * @param lastupdate : lastupdated from the JSON object
     * @param machine : machine from the JSON object
     * @param note : node from the JSON object
     * @param latitute :latitute from the JSON object
     * @param longitude :longitude from the JSON object
     * @param type :type from the JSON object
     * @param place : place from the JSON object
     * @param stars : stars from the JSON object
     * @param street :street from the JSON object
     * @param zipcode : zipcode from the JSON object
     * @return : object of jsonProprty after creation
     */
    public static jsonProperty insertObject(String address, String city, String country, String lastupdate, String machine, String note,
                                            double latitute, double longitude, String type, String place, int stars, String street, int zipcode)
    {
        return new jsonProperty(address, city, country, lastupdate, machine, note, latitute, longitude, type, place, stars, street, zipcode);
    }

    private jsonProperty(Builder builder){
        this.latitute = latitute;
        this.longitude = longitude;
    }

    /**
     * Builder class for handling optional parameters
     */

    public static class Builder {

        //Mandatory
        private double latitute;
        private double longitude;

        //optional
        private String address = null;
        private String city = null;
        private String country = null;
        private String lastupdate = null;
        private String machine = null;
        private String note = null;
        private int number;
        private String type = null;
        private String place = null;
        private int stars;
        private String street = null;
        private int zipcode;

        public Builder(double latitute, double longitude){
            this.latitute = latitute;
            this.longitude = longitude;
        }
        public jsonProperty build(){
            return new jsonProperty(this);
        }
    }
}

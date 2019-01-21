package com.example.vmann.mapbox.model;

/**
 * Used by the profile endpoint and contains information about the Uber user that has authorized
 * with the application.
 */
public class Profile extends UberModel {

    /**
     * First name of the Uber user.
     */
    String first_name;
    /**
     * Last name of the Uber user.
     */
    String last_name;
    /**
     * Email address of the Uber user
     */
    String email;
    /**
     * Image URL of the Uber user.
     */
    String picture;
    /**
     * Promo code of the Uber user.
     */
    String promo_code;

    public String getFirstName() {
        return first_name;
    }

    public String getLastName() {
        return last_name;
    }

    public String getEmail() {
        return email;
    }

    public String getPicture() {
        return picture;
    }

    public String getPromoCode() {
        return promo_code;
    }

}

package com.mastersoft.steb.bymeapp.model;

//import com.google.gson.annotations.Expose;
//import com.google.gson.annotations.SerializedName;

public class Offer {

    private String serReqID;
    private String deliveryPlace;
    private String serviceDescr;
    private String notes;
    private String deliveryDateTime;
    private double propGain;
    private String userID;

    public String getServiceDescr() {
        return serviceDescr;
    }

    public void setServiceDescr(String serviceDescr) {
        this.serviceDescr = serviceDescr;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public Offer() {
    }

    /**
     * @param deliveryPlace
     * @param deliveryDateTime
     * @param serReqID
     * @param propGain
     */
    public Offer(String userID, String serReqID, String serviceDescr, String deliveryPlace, String deliveryDateTime, double propGain, String notes) {
        super();
        this.serReqID      = serReqID;
        this.deliveryPlace = deliveryPlace;
        this.deliveryDateTime  = deliveryDateTime;
        this.propGain      = propGain;
        this.notes         = notes;
        this.userID        = userID;
    }

    public String getSerReqID() {
        return serReqID;
    }

    public void setSerReqID(String serReqID) {
        this.serReqID = serReqID;
    }

    public String getDeliveryPlace() {
        return deliveryPlace;
    }

    public void setDeliveryPlace(String deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
    }

    public String getDeliveryTime() { return deliveryDateTime; }

    public void setDeliveryTime(long deliveryTime) {
        this.deliveryDateTime = deliveryDateTime;
    }

    public double getPropGain() {
        return propGain;
    }

    public void setPropGain(double propGain) {
        this.propGain = propGain;
    }

    public String getService_descr() {
        return serviceDescr;
    }

    public void setService_descr(String service_descr) {
        this.serviceDescr = service_descr;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
package com.mastersoft.steb.bymeapp.model;

public class ServiceReq {

    private String userID;
    private String contactInfos;
    private String deliveryPlace;
    private String deliveryDate;
    private String deliveryTime;
    private String description;
    private double propGain;
    private String shortDescr;
    private String perfPlace;
    private long   timestamp;

    public ServiceReq() {
    }

    /**
     *
     * @param deliveryPlace
     * @param userID
     * @param deliveryTime
     * @param description
     * @param contactInfos
     * @param propGain
     * @param shortDescr
     */

    public ServiceReq(String userID,String shortDescr, String description, String contactInfos, String deliveryPlace, String perfPlace, String deliveryDate, String deliveryTime, double propGain, long timestamp) {
        super();
        this.userID        = userID;
        this.contactInfos  = contactInfos;
        this.deliveryPlace = deliveryPlace;
        this.deliveryDate  = deliveryDate;
        this.deliveryTime  = deliveryTime;
        this.description   = description;
        this.propGain      = propGain;
        this.shortDescr    = shortDescr;
        this.timestamp     = timestamp;
        this.perfPlace     = perfPlace;
    }


    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getContactInfos() {
        return contactInfos;
    }

    public void setContactInfos(String contactInfos) {
        this.contactInfos = contactInfos;
    }

    public String getDeliveryPlace() {
        return deliveryPlace;
    }

    public void setDeliveryPlace(String deliveryPlace) {
        this.deliveryPlace = deliveryPlace;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getDeliveryDate() { return deliveryDate; }

    public void setDeliveryDate(String deliveryDate) { this.deliveryDate = deliveryDate; }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPropGain() {
        return propGain;
    }

    public void setPropGain(Integer propGain) {
        this.propGain = propGain;
    }

    public String getShortDescr() {
        return shortDescr;
    }

    public void setShortDescr(String shortDescr) {
        this.shortDescr = shortDescr;
    }

    public Long getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPerfPlace() {
        return perfPlace;
    }

    public void setPerfPlace(String perfPlace) {
        this.perfPlace = perfPlace;
    }

}
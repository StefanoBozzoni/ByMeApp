package com.mastersoft.steb.bymeapp.Controllers;

import com.mastersoft.steb.bymeapp.model.Offer;
import com.mastersoft.steb.bymeapp.model.ServiceReq;

public class OfferController {


    public static class Error {
        private int errorCode;
        private String errorDescription;
        Error(int code, String description) {this.errorCode=code;this.errorDescription=description;};
        public int getErrorCode() {
            return errorCode;
        }
        public String getErrorDescription() {
            return errorDescription;
        }
    }

    public OfferController() {};

    public static OfferController.Error validate(Offer of){
        if (of.getServiceDescr().trim().equals("")) {
            return new OfferController.Error(10,"short service description can't be void in offer data");
        }

        if (of.getUserID().trim().equals("")) {
            return new OfferController.Error(20,"user ID can't be void");
        }

        if (of.getPropGain()==0) {
            return new OfferController.Error(30,"proposed offer gain should be provided");
        }

        if (of.getSerReqID().trim().equals("")) {
            return new OfferController.Error(20,"service Id can't be void in offer data");
        }


        return new OfferController.Error(0,"OK");

    }


}

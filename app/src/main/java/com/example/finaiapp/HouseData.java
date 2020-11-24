package com.example.finaiapp;

public class HouseData {

    private int sale_yr;
    private int sale_month;
    private int sale_day;
    private int bedrooms;
    private float bathrooms;
    private int sqft_living;
    private int sqft_lot;
    private float floors;
    private int waterfront;
    private int view;
    private int condition;
    private int grade;
    private int sqft_above;
    private int sqft_basement;
    private int yr_built;
    private int yr_renovated;
    private int zipcode;
    private float lat;
    private float longt;
    private int sqft_living15;
    private int sqft_lot15;
    private float predictedPrice;

    public HouseData(){
    }

    public HouseData(int sale_yr, int sale_month, int sale_day, int bedrooms, float bathrooms, int sqft_living,
                     int sqft_lot, float floors, int waterfront, int view, int condition, int grade, int sqft_above,
                     int sqft_basement, int yr_built, int yr_renovated, int zipcode, float lat, float longt,
                     int sqft_living15, int sqft_lot15, float predictedPrice) {
        this.sale_yr = sale_yr;
        this.sale_month = sale_month;
        this.sale_day = sale_day;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.sqft_living = sqft_living;
        this.sqft_lot = sqft_lot;
        this.floors = floors;
        this.waterfront = waterfront;
        this.view = view;
        this.condition = condition;
        this.grade = grade;
        this.sqft_above = sqft_above;
        this.sqft_basement = sqft_basement;
        this.yr_built = yr_built;
        this.yr_renovated = yr_renovated;
        this.zipcode = zipcode;
        this.lat = lat;
        this.longt = longt;
        this.sqft_living15 = sqft_living15;
        this.sqft_lot15 = sqft_lot15;
        this.predictedPrice = predictedPrice;
    }
    public int getSale_yr() {
        return sale_yr;
    }

    public void setSale_yr(int sale_yr) {
        this.sale_yr = sale_yr;
    }

    public int getSale_month() {
        return sale_month;
    }

    public void setSale_month(int sale_month) {
        this.sale_month = sale_month;
    }

    public int getSale_day() {
        return sale_day;
    }

    public void setSale_day(int sale_day) {
        this.sale_day = sale_day;
    }

    public int getBedrooms() {
        return bedrooms;
    }

    public void setBedrooms(int bedrooms) {
        this.bedrooms = bedrooms;
    }

    public float getBathrooms() {
        return bathrooms;
    }

    public void setBathrooms(float bathrooms) {
        this.bathrooms = bathrooms;
    }

    public int getSqft_living() {
        return sqft_living;
    }

    public void setSqft_living(int sqft_living) {
        this.sqft_living = sqft_living;
    }

    public int getSqft_lot() {
        return sqft_lot;
    }

    public void setSqft_lot(int sqft_lot) {
        this.sqft_lot = sqft_lot;
    }

    public float getFloors() {
        return floors;
    }

    public void setFloors(float floors) {
        this.floors = floors;
    }

    public int getWaterfront() {
        return waterfront;
    }

    public void setWaterfront(int waterfront) {
        this.waterfront = waterfront;
    }

    public int getView() {
        return view;
    }

    public void setView(int view) {
        this.view = view;
    }

    public int getCondition() {
        return condition;
    }

    public void setCondition(int condition) {
        this.condition = condition;
    }

    public int getGrade() {
        return grade;
    }

    public void setGrade(int grade) {
        this.grade = grade;
    }

    public int getSqft_above() {
        return sqft_above;
    }

    public void setSqft_above(int sqft_above) {
        this.sqft_above = sqft_above;
    }

    public int getSqft_basement() {
        return sqft_basement;
    }

    public void setSqft_basement(int sqft_basement) {
        this.sqft_basement = sqft_basement;
    }

    public int getYr_built() {
        return yr_built;
    }

    public void setYr_built(int yr_built) {
        this.yr_built = yr_built;
    }

    public int getYr_renovated() {
        return yr_renovated;
    }

    public void setYr_renovated(int yr_renovated) {
        this.yr_renovated = yr_renovated;
    }

    public int getZipcode() {
        return zipcode;
    }

    public void setZipcode(int zipcode) {
        this.zipcode = zipcode;
    }

    public float getLat() {
        return lat;
    }

    public void setLat(float lat) {
        this.lat = lat;
    }

    public float getLongt() {
        return longt;
    }

    public void setLongt(float longt) {
        this.longt = longt;
    }

    public int getSqft_living15() {
        return sqft_living15;
    }

    public void setSqft_living15(int sqft_living15) {
        this.sqft_living15 = sqft_living15;
    }

    public int getSqft_lot15() {
        return sqft_lot15;
    }

    public void setSqft_lot15(int sqft_lot15) {
        this.sqft_lot15 = sqft_lot15;
    }

    public float getPredictedPrice() {
        return predictedPrice;
    }

    public void setPredictedPrice(float predictedPrice) {
        this.predictedPrice = predictedPrice;
    }
}


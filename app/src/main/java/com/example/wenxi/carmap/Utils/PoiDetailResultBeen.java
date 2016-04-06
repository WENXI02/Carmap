package com.example.wenxi.carmap.Utils;

import android.os.Parcelable;

import com.baidu.mapapi.model.LatLng;

/**
 * Created by wenxi on 16/3/24.
 */
public class PoiDetailResultBeen  {

    private String  Address;//详情的地址

    private int CheckinNum;//详情的签到数

    private int CommentNum;

    private String DetailUrl;

    private  double EnvironmentRating;

    private  double FacilityRating;

    private  int FavoriteNum;

    private  int GrouponNum;

    private  double HygieneRating;

    private  int ImageNum;

    private LatLng Location;

    private String name;

    private double OverallRating;

    private double Price;

    private double ServiceRating;

    private String ShopHours;

    private String tag;

    private String Telephone;

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public int getCheckinNum() {
        return CheckinNum;
    }

    public void setCheckinNum(int checkinNum) {
        CheckinNum = checkinNum;
    }

    public int getCommentNum() {
        return CommentNum;
    }

    public void setCommentNum(int commentNum) {
        CommentNum = commentNum;
    }

    public String getDetailUrl() {
        return DetailUrl;
    }

    public void setDetailUrl(String detailUrl) {
        DetailUrl = detailUrl;
    }

    public double getEnvironmentRating() {
        return EnvironmentRating;
    }

    public void setEnvironmentRating(double environmentRating) {
        EnvironmentRating = environmentRating;
    }

    public double getFacilityRating() {
        return FacilityRating;
    }

    public void setFacilityRating(double facilityRating) {
        FacilityRating = facilityRating;
    }

    public int getFavoriteNum() {
        return FavoriteNum;
    }

    public void setFavoriteNum(int favoriteNum) {
        FavoriteNum = favoriteNum;
    }

    public int getGrouponNum() {
        return GrouponNum;
    }

    public void setGrouponNum(int grouponNum) {
        GrouponNum = grouponNum;
    }

    public double getHygieneRating() {
        return HygieneRating;
    }

    public void setHygieneRating(double hygieneRating) {
        HygieneRating = hygieneRating;
    }

    public int getImageNum() {
        return ImageNum;
    }

    public void setImageNum(int imageNum) {
        ImageNum = imageNum;
    }

    public LatLng getLocation() {
        return Location;
    }

    public void setLocation(LatLng location) {
        Location = location;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getOverallRating() {
        return OverallRating;
    }

    public void setOverallRating(double overallRating) {
        OverallRating = overallRating;
    }

    public double getPrice() {
        return Price;
    }

    public void setPrice(double price) {
        Price = price;
    }

    public double getServiceRating() {
        return ServiceRating;
    }

    public void setServiceRating(double serviceRating) {
        ServiceRating = serviceRating;
    }

    public String getShopHours() {
        return ShopHours;
    }

    public void setShopHours(String shopHours) {
        ShopHours = shopHours;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTelephone() {
        return Telephone;
    }

    public void setTelephone(String telephone) {
        Telephone = telephone;
    }

    @Override
    public String toString() {
        return "PoiDetailResultBeen{" +
                "Address='" + Address + '\'' +
                ", CheckinNum=" + CheckinNum +
                ", CommentNum=" + CommentNum +
                ", DetailUrl='" + DetailUrl + '\'' +
                ", EnvironmentRating=" + EnvironmentRating +
                ", FacilityRating=" + FacilityRating +
                ", FavoriteNum=" + FavoriteNum +
                ", GrouponNum=" + GrouponNum +
                ", HygieneRating=" + HygieneRating +
                ", ImageNum=" + ImageNum +
                ", Location=" + Location +
                ", name='" + name + '\'' +
                ", OverallRating=" + OverallRating +
                ", Price=" + Price +
                ", ServiceRating=" + ServiceRating +
                ", ShopHours='" + ShopHours + '\'' +
                ", tag='" + tag + '\'' +
                ", Telephone='" + Telephone + '\'' +
                '}';
    }
}

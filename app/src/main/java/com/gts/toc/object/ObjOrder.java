package com.gts.toc.object;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by warsono on 11/04/16.
 */

public class ObjOrder {
    @SerializedName("ORDER_ID")
    private String OrderId;

    @SerializedName("ORDER_DATE")
    private String OrderDate;

    @SerializedName("CLIENT_NAME")
    private String ClientName;

    @SerializedName("TECH_ID")
    private String TechID;

    @SerializedName("TECH_NAME")
    private String TechName;

    @SerializedName("TECH_IMAGE")
    private String TechImage;

    @SerializedName("ORDER_TITLE")
    private String Title;

    @SerializedName("ORDER_DESC")
    private String Description;

    @SerializedName("PRICE")
    private String Price;

    @SerializedName("ADDRESS")
    private String Address;

    @SerializedName("POINT")
    private String Point;

    @SerializedName("SELECT_STATE")
    private String SelectState;

    @SerializedName("PICKUP_STATE")
    private String PickupState;

    @SerializedName("ESTIMASI_STATE")
    private String EstimasiState;

    @SerializedName("DELIVERY_STATE")
    private String DeliveryState;

    @SerializedName("STATE")
    private String State;

    @SerializedName("TYPE")
    private String Type;

    @SerializedName("WARANTY")
    private String Waranty;

    @SerializedName("FINISH")
    private String FinishTime;

    @SerializedName("REF")
    private String Reference;

    @SerializedName("ORDER_LOG")
    public List<ObjOrderDetail> OrderLog = new ArrayList<ObjOrderDetail>();

    public String getOrderId() {
        return OrderId;
    }

    public void setOrderId(String orderId) {
        OrderId = orderId;
    }

    public String getOrderDate() {
        return OrderDate;
    }

    public void setOrderDate(String orderDate) {
        OrderDate = orderDate;
    }

    public String getClientName() {
        return ClientName;
    }

    public void setClientName(String clientName) {
        ClientName = clientName;
    }

    public String getTechID() {
        return TechID;
    }

    public void setTechID(String techID) {
        TechID = techID;
    }

    public String getTechName() {
        return TechName;
    }

    public void setTechName(String techName) {
        TechName = techName;
    }

    public String getTechImage() {
        return TechImage;
    }

    public void setTechImage(String techImage) {
        TechImage = techImage;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getPrice() {
        return Price;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }

    public String getPoint() {
        return Point;
    }

    public void setPoint(String point) {
        Point = point;
    }

    public String getSelectState() {
        return SelectState;
    }

    public void setSelectState(String selectState) {
        SelectState = selectState;
    }

    public String getPickupState() {
        return PickupState;
    }

    public void setPickupState(String pickupState) {
        PickupState = pickupState;
    }

    public String getEstimasiState() {
        return EstimasiState;
    }

    public void setEstimasiState(String estimasiState) {
        EstimasiState = estimasiState;
    }

    public String getDeliveryState() {
        return DeliveryState;
    }

    public void setDeliveryState(String deliveryState) {
        DeliveryState = deliveryState;
    }

    public String getState() {
        return State;
    }

    public void setState(String state) {
        State = state;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        Type = type;
    }

    public String getWaranty() {
        return Waranty;
    }

    public void setWaranty(String waranty) {
        Waranty = waranty;
    }

    public String getFinishTime() {
        return FinishTime;
    }

    public void setFinishTime(String finishTime) {
        FinishTime = finishTime;
    }

    public String getReference() {
        return Reference;
    }

    public void setReference(String reference) {
        Reference = reference;
    }

    public List<ObjOrderDetail> getOrderLog() {
        return OrderLog;
    }

    public void setOrderLog(List<ObjOrderDetail> orderLog) {
        OrderLog = orderLog;
    }
}

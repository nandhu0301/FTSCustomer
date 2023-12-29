package com.smiligenceTestenv.techUser.bean;

import java.util.ArrayList;

public class OrderDetails implements Comparable<OrderDetails> {
    String orderDay;
    String orderStatus;
    String orderDeliverCodeForSeller;
    String orderDeliverCodeForDrliveryBoy;
    int totalAmount;
    int totalItem;
    String orderTime;
    String storeName;
    String paymentId;
    String orderIdfromPaymentGateway;
    String orderId;
    int paymentamount;
    String paymentDate;
    String paymentType;
    String fullName;
    String shippingPincode;
    String storePincode;
    String shippingaddress;
    ItemDetails itemDetails;
    String assignedTo;
    String phoneNumber;
    String customerName;
    String customerId;
    String customerPhoneNumber;
    String categoryType;
    String instructionsToDeliveryBoy;
    String orderDeliveryType;
    int tipAmount;
    String orderCreateDate;
    String deliverOtp;
    String notificationStatus;
    String notificationStatusForSeller;
    int deliveryFee;
    String courierPartnerName;
    String trackingId;
    String trackingImage;
    String docType;
    String orderNumberForFinancialYearCalculation;
    String orderType;
    String insideOrOutside;
    float cgstValue;
    float sgstValue;
    float totalTaxValue;
    float igstValue;


    public float getCgstValue() {
        return cgstValue;
    }

    public OrderDetails setCgstValue(float cgstValue) {
        this.cgstValue = cgstValue;
        return this;
    }

    public float getSgstValue() {
        return sgstValue;
    }

    public OrderDetails setSgstValue(float sgstValue) {
        this.sgstValue = sgstValue;
        return this;
    }

    public float getTotalTaxValue() {
        return totalTaxValue;
    }

    public OrderDetails setTotalTaxValue(float totalTaxValue) {
        this.totalTaxValue = totalTaxValue;
        return this;
    }

    public float getIgstValue() {
        return igstValue;
    }

    public OrderDetails setIgstValue(float igstValue) {
        this.igstValue = igstValue;
        return this;
    }

    public String getInsideOrOutside() {
        return insideOrOutside;
    }

    public OrderDetails setInsideOrOutside(String insideOrOutside) {
        this.insideOrOutside = insideOrOutside;
        return this;
    }

    public String getOrderType() {
        return orderType;
    }

    public OrderDetails setOrderType(String orderType) {
        this.orderType = orderType;
        return this;
    }

    public String getOrderNumberForFinancialYearCalculation() {
        return orderNumberForFinancialYearCalculation;
    }

    public OrderDetails setOrderNumberForFinancialYearCalculation(String orderNumberForFinancialYearCalculation) {
        this.orderNumberForFinancialYearCalculation = orderNumberForFinancialYearCalculation;
        return this;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getCourierPartnerName() {
        return courierPartnerName;
    }

    public void setCourierPartnerName(String courierPartnerName) {
        this.courierPartnerName = courierPartnerName;
    }

    public String getTrackingId() {
        return trackingId;
    }

    public void setTrackingId(String trackingId) {
        this.trackingId = trackingId;
    }

    public String getTrackingImage() {
        return trackingImage;
    }

    public void setTrackingImage(String trackingImage) {
        this.trackingImage = trackingImage;
    }

    public String getFormattedDate() {
        return formattedDate;
    }

    public void setFormattedDate(String formattedDate) {
        this.formattedDate = formattedDate;
    }

    String formattedDate;
    int totalDistanceTraveled;

    public String getNotificationStatusForCustomer() {
        return notificationStatusForCustomer;
    }

    public void setNotificationStatusForCustomer(String notificationStatusForCustomer) {
        this.notificationStatusForCustomer = notificationStatusForCustomer;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public int getDiscountAmount() {
        return discountAmount;
    }

    public void setDiscountAmount(int discountAmount) {
        this.discountAmount = discountAmount;
    }

    public String getDiscountGivenBy() {
        return discountGivenBy;
    }

    public void setDiscountGivenBy(String discountGivenBy) {
        this.discountGivenBy = discountGivenBy;
    }

    String notificationStatusForCustomer;
    String discountName;
    int discountAmount;
    String discountGivenBy;

    public int getDeliveryFee() {

        return deliveryFee;
    }

    public void setDeliveryFee(int deliveryFee) {
        this.deliveryFee = deliveryFee;
    }

    public int getTotalDistanceTraveled() {
        return totalDistanceTraveled;
    }

    public void setTotalDistanceTraveled(int totalDistanceTraveled) {
        this.totalDistanceTraveled = totalDistanceTraveled;
    }

    public String getNotificationStatusForSeller() {
        return notificationStatusForSeller;
    }

    public void setNotificationStatusForSeller(String notificationStatusForSeller) {
        this.notificationStatusForSeller = notificationStatusForSeller;
    }

    public String getNotificationStatus() {
        return notificationStatus;
    }

    public void setNotificationStatus(String notificationStatus) {
        this.notificationStatus = notificationStatus;
    }

    public String getDeliverOtp() {
        return deliverOtp;
    }

    public void setDeliverOtp(String deliverOtp) {
        this.deliverOtp = deliverOtp;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    String storeAddress;

    public String getCategoryTypeId() {
        return categoryTypeId;
    }

    public void setCategoryTypeId(String categoryTypeId) {
        this.categoryTypeId = categoryTypeId;
    }

    String categoryTypeId;

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getOrderCreateDate() {
        return orderCreateDate;
    }

    public void setOrderCreateDate(String orderCreateDate) {
        this.orderCreateDate = orderCreateDate;
    }

    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public String getOrderDeliveryType() {
        return orderDeliveryType;
    }

    public void setOrderDeliveryType(String orderDeliveryType) {
        this.orderDeliveryType = orderDeliveryType;
    }

    public String getInstructionsToDeliveryBoy() {
        return instructionsToDeliveryBoy;
    }

    public void setInstructionsToDeliveryBoy(String instructionsToDeliveryBoy) {
        this.instructionsToDeliveryBoy = instructionsToDeliveryBoy;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }


    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(String deliveryType) {
        this.deliveryType = deliveryType;
    }

    String deliveryType;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void setAssignedTo(String assignedTo) {
        this.assignedTo = assignedTo;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getShippingPincode() {
        return shippingPincode;
    }

    public void setShippingPincode(String shippingPincode) {
        this.shippingPincode = shippingPincode;
    }

    public String getStorePincode() {
        return storePincode;
    }

    public void setStorePincode(String storePincode) {
        this.storePincode = storePincode;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }


    ArrayList<ItemDetails> itemDetailList = new ArrayList<ItemDetails>();

    public String getOrderDay() {
        return orderDay;
    }

    public void setOrderDay(String orderDay) {
        this.orderDay = orderDay;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderDeliverCodeForSeller() {
        return orderDeliverCodeForSeller;
    }

    public void setOrderDeliverCodeForSeller(String orderDeliverCodeForSeller) {
        this.orderDeliverCodeForSeller = orderDeliverCodeForSeller;
    }

    public String getOrderDeliverCodeForDrliveryBoy() {
        return orderDeliverCodeForDrliveryBoy;
    }

    public void setOrderDeliverCodeForDrliveryBoy(String orderDeliverCodeForDrliveryBoy) {
        this.orderDeliverCodeForDrliveryBoy = orderDeliverCodeForDrliveryBoy;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(int totalAmount) {
        this.totalAmount = totalAmount;
    }

    public int getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(int totalItem) {
        this.totalItem = totalItem;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }

    public String getOrderIdfromPaymentGateway() {
        return orderIdfromPaymentGateway;
    }

    public void setOrderIdfromPaymentGateway(String orderIdfromPaymentGateway) {
        this.orderIdfromPaymentGateway = orderIdfromPaymentGateway;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public int getPaymentamount() {
        return paymentamount;
    }

    public void setPaymentamount(int paymentamount) {
        this.paymentamount = paymentamount;
    }

    public String getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(String paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getShippingaddress() {
        return shippingaddress;
    }

    public void setShippingaddress(String shippingaddress) {
        this.shippingaddress = shippingaddress;
    }

    public ItemDetails getItemDetails() {
        return itemDetails;
    }

    public void setItemDetails(ItemDetails itemDetails) {
        this.itemDetails = itemDetails;
    }

    public ArrayList<ItemDetails> getItemDetailList() {
        return itemDetailList;
    }

    public void setItemDetailList(ArrayList<ItemDetails> itemDetailList) {
        this.itemDetailList = itemDetailList;
    }

    @Override
    public int compareTo(OrderDetails o) {
        return 0;
    }
}
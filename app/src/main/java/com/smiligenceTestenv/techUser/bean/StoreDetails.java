package com.smiligenceTestenv.techUser.bean;

public class StoreDetails {
    private String storeName;
    private String storeAddress;
    private String zipCode;
    private String businessType;
    private String GstNumber;
    private String CGstNumber;
    private String SGstNumber;
    private int parcelCharge;
    private String storeContactNumber;
    private String storeLogo;
    private String customerSupportContactNumber;
    private String headerImage;
    private String createDate;
    String firstName;
    String lastName;
    String bankName;
    String accountNumber;
    int gst;

    public int getGst() {
        return gst;
    }

    public StoreDetails setGst(int gst) {
        this.gst = gst;
        return this;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getIFSCCode() {
        return IFSCCode;
    }

    public void setIFSCCode(String IFSCCode) {
        this.IFSCCode = IFSCCode;
    }

    public String getAadharNumber() {
        return aadharNumber;
    }

    public void setAadharNumber(String aadharNumber) {
        this.aadharNumber = aadharNumber;
    }

    String IFSCCode;
    String aadharNumber;


    public String getStoreContactNumber() {
        return storeContactNumber;
    }

    public void setStoreContactNumber(String storeContactNumber) {
        this.storeContactNumber = storeContactNumber;
    }

    public String getCustomerSupportContactNumber() {
        return customerSupportContactNumber;
    }

    public void setCustomerSupportContactNumber(String customerSupportContactNumber) {
        this.customerSupportContactNumber = customerSupportContactNumber;
    }

    public void setParcelCharge(int parcelCharge) {
        this.parcelCharge = parcelCharge;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getStoreLogo() {
        return storeLogo;
    }

    public void setStoreLogo(String storeLogo) {
        this.storeLogo = storeLogo;
    }

    public String getHeaderImage() {
        return headerImage;
    }

    public void setHeaderImage(String headerImage) {
        this.headerImage = headerImage;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public String getStoreAddress() {
        return storeAddress;
    }

    public void setStoreAddress(String storeAddress) {
        this.storeAddress = storeAddress;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getBusinessType() {
        return businessType;
    }

    public void setBusinessType(String businessType) {
        this.businessType = businessType;
    }

    public String getGstNumber() {
        return GstNumber;
    }

    public void setGstNumber(String gstNumber) {
        GstNumber = gstNumber;
    }

    public String getCGstNumber() {
        return CGstNumber;
    }

    public void setCGstNumber(String CGstNumber) {
        this.CGstNumber = CGstNumber;
    }

    public String getSGstNumber() {
        return SGstNumber;
    }

    public void setSGstNumber(String SGstNumber) {
        this.SGstNumber = SGstNumber;
    }

    public long getParcelCharge() {
        return parcelCharge;
    }

}
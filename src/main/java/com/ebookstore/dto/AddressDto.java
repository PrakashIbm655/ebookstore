package com.ebookstore.dto;

public class AddressDto {

    private Long id;
    private String line1;
    private String line2;
    private String city;
    private String state;
    private String pincode;
    private boolean isDefault;

    public AddressDto() {}

    public AddressDto(Long id, String line1, String line2, String city,
                      String state, String pincode, boolean isDefault) {
        this.id = id;
        this.line1 = line1;
        this.line2 = line2;
        this.city = city;
        this.state = state;
        this.pincode = pincode;
        this.isDefault = isDefault;
    }

    public Long getId() { return id; }

    public String getLine1() { return line1; }

    public String getLine2() { return line2; }

    public String getCity() { return city; }

    public String getState() { return state; }

    public String getPincode() { return pincode; }

    public boolean isDefault() { return isDefault; }
}

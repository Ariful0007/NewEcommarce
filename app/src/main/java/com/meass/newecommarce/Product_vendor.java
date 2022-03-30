package com.meass.newecommarce;

import java.io.Serializable;

public class Product_vendor  implements Serializable {
    String vendor_email;
    String proimage;
    String proname;
    String proprice;
    String desc ;
    String name,address,phone,delivary_date;
    String uuid;

    public Product_vendor() {
    }

    public Product_vendor(String vendor_email, String proimage, String proname, String proprice,
                          String desc, String name, String address, String phone, String delivary_date, String uuid) {
        this.vendor_email = vendor_email;
        this.proimage = proimage;
        this.proname = proname;
        this.proprice = proprice;
        this.desc = desc;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.delivary_date = delivary_date;
        this.uuid = uuid;
    }

    public String getVendor_email() {
        return vendor_email;
    }

    public void setVendor_email(String vendor_email) {
        this.vendor_email = vendor_email;
    }

    public String getProimage() {
        return proimage;
    }

    public void setProimage(String proimage) {
        this.proimage = proimage;
    }

    public String getProname() {
        return proname;
    }

    public void setProname(String proname) {
        this.proname = proname;
    }

    public String getProprice() {
        return proprice;
    }

    public void setProprice(String proprice) {
        this.proprice = proprice;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDelivary_date() {
        return delivary_date;
    }

    public void setDelivary_date(String delivary_date) {
        this.delivary_date = delivary_date;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}

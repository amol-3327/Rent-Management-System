package com.example.rentmgmt1;

public class payment_model {
    String id, flat, name,mob, c_id, rent, reading, total, month, p_status, paid, rem;

    public payment_model(String id, String flat, String name,String mob, String c_id, String rent, String reading,
                        String total, String month, String p_status, String paid, String rem) {
        this.id = id;
        this.flat = flat;
        this.name = name;
        this.mob = mob;
        this.c_id = c_id;
        this.rent = rent;
        this.reading = reading;
        this.total = total;
        this.month = month;
        this.p_status = p_status;
        this.paid = paid;
        this.rem = rem;
    }

    public String getId() { return id; }
    public String getFlat() { return flat; }
    public String getName() { return name; }
    public String getMob() { return mob; }
    public String getCId() { return c_id; }
    public String getRent() { return rent; }
    public String getReading() { return reading; }
    public String getTotal() { return total; }
    public String getMonth() { return month; }
    public String getPStatus() { return p_status; }
    public String getPaid() { return paid; }
    public String getRem() { return rem; }
}


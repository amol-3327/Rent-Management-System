package com.example.rentmgmt1;
public class rental_model {
    String id, flat, name,depo_out,d_paid,t_depo,rent_out,l_date,c_id,mob;

    public rental_model(String id, String flat, String name, String depo_out,String d_paid,String t_depo, String rent_out, String l_date,String c_id,String mob) {
        this.id = id;
        this.flat = flat;
        this.name = name;
        this.depo_out = depo_out;
        this.d_paid = d_paid;
        this.t_depo = t_depo;
        this.rent_out = rent_out;
        this.l_date = l_date;
        this.c_id = c_id;
        this.mob = mob;
    }

    public String getId() { return id; }
    public String getFlat() { return flat; }
    public String getName() { return name; }
    public String getDepo_out() {
        return depo_out;
    }
    public String getRent_out() {
        return rent_out;
    }
    public String getL_date() {
        return l_date;
    }
    public String getC_id() {
        return c_id;
    }
    public String getMob() {
        return mob;
    }
    public String getD_paid() {
        return d_paid;
    }
    public String getT_depo() {
        return t_depo;
    }
}


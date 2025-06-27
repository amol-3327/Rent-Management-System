package com.example.rentmgmt1.history;
public class rental_h_model {
    String id, c_id, flat, name, mob, s_date, l_date, t_month;

    public rental_h_model(String id, String c_id, String flat, String name, String mob, String s_date, String l_date, String t_month) {
        this.id = id;
        this.c_id = c_id;
        this.flat = flat;
        this.name = name;
        this.mob = mob;
        this.s_date = s_date;
        this.l_date = l_date;
        this.t_month = t_month;
    }

    public String getId() { return id; }
    public String getC_id() { return c_id; }
    public String getFlat() { return flat; }
    public String getName() { return name; }
    public String getMob() { return mob; }
    public String getS_date() { return s_date; }
    public String getL_date() { return l_date; }
    public String getT_month() { return t_month; }
}
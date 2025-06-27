package com.example.rentmgmt1;

public class book_model {
    String id,name,mob,deposit,town,b_date,s_date,flat,cnt,mode,type,t_depo;

    public book_model(String id,String name ,String mob,String deposit,String town,String b_date,String s_date,String flat,String cnt,String mode,String type,String t_depo) {
        this.id= id;
        this.name= name;
        this.mob= mob;
        this.deposit= deposit;
        this.town= town;
        this.b_date= b_date;
        this.s_date= s_date;
        this.flat= flat;
        this.cnt= cnt;
        this.mode= mode;
        this.type= type;
        this.t_depo= t_depo;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getMob() {
        return mob;
    }
    public String getDeposit() {
        return deposit;
    }
    public String getTown() {
        return town;
    }
    public String getB_date() {
        return b_date;
    }
    public String getS_date() {
        return s_date;
    }
    public String getFlat() {
        return flat;
    }
    public String getCnt() {
        return cnt;
    }
    public String getMode() {
        return mode;
    }
    public String getT_depo() {
        return t_depo;
    }
    public String getType() {
        return type;
    }
}

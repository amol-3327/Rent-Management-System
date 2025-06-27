package com.example.rentmgmt1;

public class meter_model {
    String id,flat_no,c_r,p_r,unit,rate,rent,f_status,n_month;

    public meter_model(String id ,String flat_no,String c_r,String p_r,String unit,String rate,String rent,String f_status,String n_month) {
        this.id= id;
        this.flat_no= flat_no;
        this.c_r= c_r;
        this.p_r= p_r;
        this.unit= unit;
        this.rate= rate;
        this.rent= rent;
        this.f_status= f_status;
        this.n_month= n_month;
    }

    public String getId(){
        return id;
    }
    public String getFlat_no(){
        return flat_no;
    }

    public String getC_r(){
        return c_r;
    }

    public String getP_r(){
        return p_r;
    }

    public String getUnit(){
        return unit;
    }
    public String getRate(){
        return rate;
    }
    public String getRent(){
        return rent;
    }
    public String getF_status(){
        return f_status;
    }public String getN_month(){
        return n_month;
    }
}

package com.example.rentmgmt1;

public class flat_model {
    String id,flat_no,flat_type,f_status;

    public flat_model(String id,String flat_no ,String flat_type,String f_status) {
        this.id= id;
        this.flat_no= flat_no;
        this.flat_type= flat_type;
        this.f_status= f_status;
    }

    public String getId() {
        return id;
    }
    public String getFlat_no() {
        return flat_no;
    }
    public String getFlat_type() {
        return flat_type;
    }
    public String getF_status() {
        return f_status;
    }
}

package com.example.rentmgmt1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class add_new_flat extends AppCompatActivity {

    private AlertDialog progressDialog;
    ArrayList<String> flat_array=new ArrayList<>();
    ArrayAdapter<String> flat_adapter;
    private Button cancel,register;
    private EditText flat,c_reading,unit_rate;
    private Spinner sp_flat_type;
    private String spin_selected="",st1_c_r,st1_c_d;
    private TextView tv_c_d,tv_c_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_new_flat);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, admin_login.class));
            return;
        }

        Toolbar toolbar = findViewById(R.id.n_f_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Add New Flat:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new AlertDialog.Builder(add_new_flat.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        sp_flat_type=findViewById(R.id.spin_flat_type);
        sp_flat_type.setPopupBackgroundResource(android.R.color.white);
        tv_c_d=findViewById(R.id.tv_c_d);
        tv_c_r=findViewById(R.id.tv_c_r);
        cancel = findViewById(R.id.bt_cancel);
        register = findViewById(R.id.bt_reg);
        flat = findViewById(R.id.ed_flat_no);
        c_reading = findViewById(R.id.ed_c_r);
        unit_rate = findViewById(R.id.ed_u_r);

        fetchFlatTypes();

        sp_flat_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spin_selected=parent.getSelectedItem().toString();

                if(!parent.getSelectedItem().toString().equals("Flat Types...")){
                    fetch_flat_type_depo_rent(spin_selected);
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String st_flat=flat.getText().toString().trim();
                String st_c_r=c_reading.getText().toString().trim();
                String st_rate=unit_rate.getText().toString().trim();

                if (st_flat.isEmpty() || st_flat.equals("0")){
                    flat.setError("Please enter flat no");
                }else if (st_c_r.isEmpty() ){
                    c_reading.setError("Please enter current reading");
                }else if (st_rate.isEmpty() || st_rate.equals("0")){
                    unit_rate.setError("Please enter unit rate");
                }else  if (spin_selected.equals("Flat Types...")) {
                    Toast.makeText(add_new_flat.this, "Please select flat type ", Toast.LENGTH_SHORT).show();
                }else if (spin_selected.isEmpty()) {
                    Toast.makeText(add_new_flat.this, "Please select flat type ", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builderDel = new AlertDialog.Builder(add_new_flat.this);
                    builderDel.setTitle("Add New Flat: F-"+st_flat);
                    builderDel.setMessage("Are you sure, You want to Register?");
                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_ADD_NEW_FLAT,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(add_new_flat.this);
                                                    builderDel.setTitle("F-"+st_flat);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface1, int i) {
                                                            dialogInterface1.dismiss();
                                                            finish();

                                                            flats.ft.checkServerAvail();
                                                        }
                                                    });
                                                    builderDel.create().show();
                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(add_new_flat.this);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface1, int i) {
                                                            dialogInterface1.dismiss();
                                                        }
                                                    });
                                                    builderDel.create().show();
                                                }
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    },
                                    new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            progressDialog.dismiss();
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(add_new_flat.this);
                                            builder1.setTitle("Server Not Available");
                                            builder1.setMessage("Please try again...");
                                            AlertDialog dialog1 = builder1.create();

                                            dialog1.show();
                                        }
                                    }
                            ) {
                                @Override
                                protected Map<String, String> getParams() throws AuthFailureError {
                                    Map<String, String> params = new HashMap<>();
                                    params.put("flat", st_flat);
                                    params.put("type", spin_selected);
                                    params.put("depo", st1_c_d.replaceAll(",", ""));
                                    params.put("rent", st1_c_r.replaceAll(",", ""));
                                    params.put("c_r", st_c_r);
                                    params.put("rate", st_rate);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(add_new_flat.this);
                            requestQueue.add(stringRequest);

                            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                @Override
                                public void onRequestFinished(Request<Object> request) {
                                    requestQueue.getCache().clear();
                                }
                            });
                        }
                    });

                    builderDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    builderDel.create().show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    public void fetch_flat_type_depo_rent(String st_txt){

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,Constants.URL_FETCH_FLAT_TYPE_DETAILS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject obj = new JSONObject(response);
                    if (!obj.getBoolean("error")) {

                        st1_c_d=obj.getString("c_d");
                        st1_c_r=obj.getString("c_r");

                        tv_c_d.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st1_c_d + "/-</font></b>"));
                        tv_c_r.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st1_c_r + "/-</font></b>"));
                    } else {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(add_new_flat.this);
                        builder1.setTitle(obj.getString("message"));
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_flat_type_depo_rent(st_txt);
                            }
                        });
                        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog = builder1.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alert));
                            }
                        });
                        dialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(add_new_flat.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_flat_type_depo_rent(st_txt);
                            }
                        });
                        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog dialog = builder1.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alert));
                            }
                        });
                        dialog.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("flat_type",st_txt );
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(add_new_flat.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void fetchFlatTypes(){

        flat_array.clear();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SPIN_ALL_FLAT_TYPE,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                flat_array.add("Flat Types...");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject o = jsonArray.getJSONObject(i);
                                    String flats =  o.getString("flat_t");

                                    flat_array.add(flats);

                                    flat_adapter = new ArrayAdapter<>(add_new_flat.this, R.layout.spinner_item, flat_array);
                                    flat_adapter.setDropDownViewResource(R.layout.spinner_item);
                                    sp_flat_type.setAdapter(flat_adapter);
                                }
                            }else{
                                flat_array.clear();

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(add_new_flat.this);
                                builder1.setTitle(jsonObject.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fetchFlatTypes();
                                    }
                                });
                                builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                    }
                                });
                                AlertDialog dialog = builder1.create();
                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface arg0) {
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
                                    }
                                });
                                dialog.show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(add_new_flat.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetchFlatTypes();
                            }
                        });
                        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        AlertDialog dialog = builder1.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alert));
                            }
                        });
                        dialog.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("flats_t", "flats");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(add_new_flat.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}
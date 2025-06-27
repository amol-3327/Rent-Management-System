package com.example.rentmgmt1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rentmgmt1.history.booking_h;
import com.example.rentmgmt1.history.rentals_h;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawer;
    private NavigationView navigationView;
    private View headerView;
    private CardView c_flat,c_meter,c_rental,c_booking,c_deposit,c_payment,c_book_h,c_rental_h;
    private AlertDialog progressDialog;
    ArrayList<String> flat_array=new ArrayList<>();
    ArrayAdapter<String> flat_adapter;
    private Spinner sp_flat_type;
    private String spin_selected="";
    private TextView tv_c_d,tv_c_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_dashboard);

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, admin_login.class));
            return;
        }

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Welcome to App");
        drawer = findViewById(R.id.drawer_layout);

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(dashboard.this);
        headerView = navigationView.getHeaderView(0);

        progressDialog = new AlertDialog.Builder(dashboard.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        c_flat = findViewById(R.id.c_flat);
        c_meter = findViewById(R.id.c_meter);
        c_rental = findViewById(R.id.c_rental);
        c_booking = findViewById(R.id.c_booking);
        c_deposit = findViewById(R.id.c_deposit);
        c_payment = findViewById(R.id.c_payment);
        c_book_h = findViewById(R.id.c_b_h);
        c_rental_h = findViewById(R.id.c_r_h);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        c_flat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, flats.class));
            }
        });

        c_meter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, meters.class));
            }
        });

        c_booking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, bookings.class));
            }
        });

        c_rental.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, rentals.class));
            }
        });

        c_payment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, payments.class));
            }
        });

        c_rental_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, rentals_h.class));
            }
        });

        c_book_h.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(dashboard.this, booking_h.class));
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_logout:
                a_logout();
                break;
            case R.id.nav_add_flat_t:
                addNewFlatType();
                break;
            case R.id.nav_upd_flat_t:
                editFlatTypeDetails();
                break;
            case R.id.nav_rm_flat_t:
                deleteFlatType();
                break;
            case R.id.nav_cpin:
//                startActivity(new Intent(this,u_change_l_pin.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setActionBarTitle(String title) { getSupportActionBar().setTitle(title);
    }
    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_dash_l, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_logout:
                a_logout();
                break;
        }
        return true;
    }

    public void a_logout() {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
        builder1.setTitle("Logout");
        builder1.setMessage("Are you sure, You want to logout?");
        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                SharedPrefManager.getInstance(dashboard.this).logout();
                finishAffinity();
                startActivity(new Intent(dashboard.this, admin_login.class));
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

    public void addNewFlatType(){

        LayoutInflater inflater = LayoutInflater.from(dashboard.this);
        View dialogView = inflater.inflate(R.layout.alert_add_flat_type, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(dashboard.this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog alertDialog12 = builder.create();
        alertDialog12.show();

        Button a_cancel=dialogView.findViewById(R.id.b_a_cancel);
        Button a_add=dialogView.findViewById(R.id.b_a_add);
        EditText f_type=dialogView.findViewById(R.id.ed_f_type);
        EditText f_depo=dialogView.findViewById(R.id.ed_f_depo);
        EditText f_rent=dialogView.findViewById(R.id.ed_f_rent);

        a_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog12.dismiss();
            }
        });

        a_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String st1_type=f_type.getText().toString().trim();
                String st1_depo=f_depo.getText().toString().trim();
                String st1_rent=f_rent.getText().toString().trim();

                if (st1_type.isEmpty()){
                    f_type.setError("Please enter type");
                }else if (st1_depo.isEmpty() || st1_depo.equals("0")) {
                    f_depo.setError("Please enter deposit");
                }else if (st1_rent.isEmpty() || st1_rent.equals("0")) {
                    f_rent.setError("Please enter deposit");
                }else{
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                        builderDel.setTitle("Flat Type: "+st1_type);
                        builderDel.setMessage("Are you sure, You want to Add ?");
                        builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();

                                progressDialog.show();
                                StringRequest stringRequest = new StringRequest(
                                        Request.Method.POST,
                                        Constants.URL_ADD_FLAT_TYPE,
                                        new Response.Listener<String>() {
                                            @Override
                                            public void onResponse(String response) {
                                                progressDialog.dismiss();
                                                try {
                                                    JSONObject obj = new JSONObject(response);
                                                    if (!obj.getBoolean("error")) {
                                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                                                        builderDel.setMessage(obj.getString("message"));
                                                        builderDel.setCancelable(false);
                                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.dismiss();
                                                                alertDialog12.dismiss();
                                                            }
                                                        });
                                                        builderDel.create().show();

                                                    } else {
                                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                                                        builderDel.setMessage(obj.getString("message"));
                                                        builderDel.setCancelable(false);
                                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                            @Override
                                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                                dialogInterface.dismiss();
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
                                                AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
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
                                        params.put("type", st1_type);
                                        params.put("depo", st1_depo);
                                        params.put("rent", st1_rent);
                                        return params;
                                    }
                                };
                                RequestQueue requestQueue = Volley.newRequestQueue(dashboard.this);
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
    }

    public void editFlatTypeDetails(){

        spin_selected="";

        LayoutInflater inflater = LayoutInflater.from(dashboard.this);
        View dialogView = inflater.inflate(R.layout.alert_edit_flat_type_details, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(dashboard.this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog alertDialog12 = builder.create();
        alertDialog12.show();

        Button a_cancel=dialogView.findViewById(R.id.b_cancel);
        Button a_update=dialogView.findViewById(R.id.b_update);
        sp_flat_type=dialogView.findViewById(R.id.spin_flat_type);
        sp_flat_type.setPopupBackgroundResource(android.R.color.white);
        tv_c_d=dialogView.findViewById(R.id.tv_c_d);
        tv_c_r=dialogView.findViewById(R.id.tv_c_r);
        EditText n_r=dialogView.findViewById(R.id.ed_n_r);


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

        a_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog12.dismiss();
            }
        });

        a_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String st1_n_r=n_r.getText().toString().trim();

                if (st1_n_r.isEmpty() || st1_n_r.equals("0")) {
                    n_r.setError("Please enter deposit");
                }else  if (spin_selected.equals("Flat Types...")) {
                    Toast.makeText(dashboard.this, "Please select flat type ", Toast.LENGTH_SHORT).show();
                }else if (spin_selected.isEmpty()) {
                    Toast.makeText(dashboard.this, "Please select flat type ", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                    builderDel.setTitle("Flat Type: "+spin_selected);
                    builderDel.setMessage("Are you sure, You want to Update ?");
                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_UPDATE_FLAT_TYPE_DETAILS,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                            alertDialog12.dismiss();
                                                        }
                                                    });
                                                    builderDel.create().show();

                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
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
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
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
                                    params.put("n_r", st1_n_r);
                                    params.put("f_type", spin_selected);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(dashboard.this);
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

                                    flat_adapter = new ArrayAdapter<>(dashboard.this, R.layout.spinner_item, flat_array);
                                    flat_adapter.setDropDownViewResource(R.layout.spinner_item);
                                    sp_flat_type.setAdapter(flat_adapter);
                                }
                            }else{
                                flat_array.clear();

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(dashboard.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
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
                                tv_c_d.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("c_d") + "/-</font></b>"));
                                tv_c_r.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("c_r") + "/-</font></b>"));
                            } else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
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
        RequestQueue requestQueue = Volley.newRequestQueue(dashboard.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void deleteFlatType(){

        spin_selected="";

        LayoutInflater inflater = LayoutInflater.from(dashboard.this);
        View dialogView = inflater.inflate(R.layout.alert_remove_flat_type, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(dashboard.this);
        builder.setView(dialogView);
        builder.setCancelable(false);
        AlertDialog alertDialog12 = builder.create();
        alertDialog12.show();

        Button a_cancel=dialogView.findViewById(R.id.b_cancel);
        Button a_remove=dialogView.findViewById(R.id.b_remove);
        sp_flat_type=dialogView.findViewById(R.id.spin_flat_type);
        sp_flat_type.setPopupBackgroundResource(android.R.color.white);

        sp_flat_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spin_selected=parent.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fetchFlatTypes();

        a_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog12.dismiss();
            }
        });

        a_remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (spin_selected.equals("Flat Types...")) {
                    Toast.makeText(dashboard.this, "Please select flat type ", Toast.LENGTH_SHORT).show();
                }else if (spin_selected.isEmpty()) {
                    Toast.makeText(dashboard.this, "Please select flat type ", Toast.LENGTH_SHORT).show();
                }else{
                    AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                    builderDel.setTitle("Flat Type: "+spin_selected);
                    builderDel.setMessage("Are you sure, You want to Remove ?");
                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_REMOVE_FLAT_TYPE,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                            alertDialog12.dismiss();
                                                        }
                                                    });
                                                    builderDel.create().show();

                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(dashboard.this);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
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
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(dashboard.this);
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
                                    params.put("f_type", spin_selected);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(dashboard.this);
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
    }
}
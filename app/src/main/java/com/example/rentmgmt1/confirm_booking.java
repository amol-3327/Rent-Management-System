package com.example.rentmgmt1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class confirm_booking extends AppCompatActivity {

    private AlertDialog progressDialog;
    private String st_id, st_name,st_mob,st_deposit,st_town,st_b_date,st_s_date,st_flat,st_cnt,st_c_r,st_p_mode,st_t_depo;
    private EditText name,mob,depo,town,ppl;
    private TextView flat_no,b_date,s_date;
    private Button confirm,cancel;
    public TextView c_r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_confirm_booking);
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

        Toolbar toolbar = findViewById(R.id.c_b_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Confirm Booking:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new AlertDialog.Builder(confirm_booking.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        st_id = getIntent().getStringExtra("id");
        st_name = getIntent().getStringExtra("name");
        st_mob = getIntent().getStringExtra("mob");
        st_deposit = getIntent().getStringExtra("deposit");
        st_town = getIntent().getStringExtra("town");
        st_b_date = getIntent().getStringExtra("b_date");
        st_s_date = getIntent().getStringExtra("s_date");
        st_flat = getIntent().getStringExtra("flat");
        st_cnt = getIntent().getStringExtra("cnt");
        st_p_mode = getIntent().getStringExtra("p_mode");
        st_t_depo = getIntent().getStringExtra("t_depo");

        name = findViewById(R.id.ed_name);
        mob = findViewById(R.id.ed_mob);
        depo = findViewById(R.id.ed_depo);
        town = findViewById(R.id.ed_town);
        ppl = findViewById(R.id.ed_cnt);
        s_date = findViewById(R.id.tv_s_date);
        flat_no = findViewById(R.id.tv_flat_no);
        b_date = findViewById(R.id.tv_b_date);
        c_r = findViewById(R.id.tv_cr);

        confirm = findViewById(R.id.bt_confirm);
        cancel = findViewById(R.id.bt_cancel);

        flat_no.setText("F-"+st_flat);
        name.setText(st_name);
        mob.setText(st_mob);
        depo.setText(st_deposit);
        town.setText(st_town);
        b_date.setText(st_b_date);
        s_date.setText(st_s_date);
        ppl.setText(st_cnt);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                bookings.book.checkServerAvail();
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String st1_name = name.getText().toString().trim();
                String st1_mob = mob.getText().toString().trim();
                String st1_depo = depo.getText().toString().trim();
                String st1_town = town.getText().toString().trim();
                String st1_ppl = ppl.getText().toString().trim();
                String st1_c_r = c_r.getText().toString().trim();

                if (st1_name.isEmpty()){
                    name.setError("Please enter name");
                }else if (st1_mob.isEmpty() || st1_mob.length() != 10) {
                    mob.setError("Please enter valid mobile number");
                }else if (st1_depo.isEmpty()){
                    depo.setError("Please enter deposit");
                }else if (st1_town.isEmpty()){
                    town.setError("Please enter home town");
                }else if (st1_ppl.isEmpty()){
                    ppl.setError("Please enter people count");
                }else{
                    String arr1[] = {st_b_date, st_s_date};
                    String[] arr = new String[2];

                    for (int i = 0; i < 2; i++) {

                            SimpleDateFormat inputFormatter = new SimpleDateFormat("dd-MMMM-yyyy");
                            SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");
                            Date date1 = null;

                            try {
                                date1 = inputFormatter.parse(arr1[i]);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            arr[i] = outputFormatter.format(date1);
                    }

                    String depo_replace = st1_depo.replace(",", "");

                    AlertDialog.Builder builderDel = new AlertDialog.Builder(confirm_booking.this);
                    builderDel.setTitle("Confirm Booking: "+st_name);
                    builderDel.setMessage("Are you sure, You want to Confirm?");
                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_CONFIRM_BOOKING,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(confirm_booking.this);
                                                    builderDel.setTitle("F-"+st_flat);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();

                                                            BigInteger remainingDeposit = new BigInteger(st_t_depo.replaceAll(",", ""))
                                                                    .subtract(new BigInteger(st1_depo.replaceAll(",", "")));

                                                            String message1 = "Billing Started for Flat No: F-" + st_flat + "\n\n" +
                                                                    "Name : " + st_name + "\n" +
                                                                    "Booking Date : " + Html.fromHtml("<font color=#000000><b> " + st_b_date + "</font>") + "\n" +
                                                                    "Starting Date : " + Html.fromHtml("<font color=#000000><b> " + st_s_date + "</font>") + "\n" +
                                                                    "Total Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + st_t_depo + "/-</b></font>") + "\n" +
                                                                    "Paid Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + st1_depo + "/-</b></font>") + "\n" +
                                                                    "Remaining Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + IND_money_format(String.valueOf(remainingDeposit)) + "/-</b></font>") + "\n" +
                                                                    "Starting Meter Reading : " + Html.fromHtml("<font color=#000000><b> " + st_c_r + "/-</b></font>") + "\n";

//                                                            String phoneNumber = "+91"+st_mob;
                                                            String phoneNumber = "+919764751241";
                                                            String encodedMessage = Uri.encode(message1);
                                                            String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                            startActivity(intent);

                                                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                                finish();
                                                                bookings.book.checkServerAvail();
                                                            }, 500);
                                                        }
                                                    });
                                                    builderDel.create().show();

                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(confirm_booking.this);
                                                    builderDel.setMessage(obj.getString("message"));
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
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(confirm_booking.this);
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
                                    params.put("id", st_id);
                                    params.put("flat_no", st_flat);
                                    params.put("name", st1_name);
                                    params.put("mob", st1_mob);
                                    params.put("depo", depo_replace);
                                    params.put("town", st1_town);
                                    params.put("ppl", st1_ppl);
                                    params.put("b_date", arr[0]);
                                    params.put("s_date", arr[1]);
                                    params.put("s_unit", st1_c_r);
                                    params.put("p_mode", st_p_mode);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(confirm_booking.this);
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

        AlertDialog.Builder builderDel = new AlertDialog.Builder(confirm_booking.this);
        builderDel.setMessage("Update meter reading for : F-"+st_flat+" before confirm...");
        builderDel.setCancelable(false);
        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builderDel.create().show();

        fetch_reading(st_flat);
    }
    public void fetch_reading(String f_id){

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_FETCH_METER_READINGS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                               st_c_r=obj.getString("c_r");
                               c_r.setText(st_c_r);
                            } else {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(confirm_booking.this);
                                builderDel.setMessage(obj.getString("message"));
                                builderDel.setCancelable(false);
                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        finish();
                                        bookings.book.checkServerAvail();
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(confirm_booking.this);
                        builderDel.setCancelable(false);
                        builderDel.setMessage("Network Error, Try Again Later.");
                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                                bookings.book.checkServerAvail();
                            }
                        });
                        builderDel.create().show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("f_id", f_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(confirm_booking.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public String IND_money_format(String money) {
        if (money.length() <= 3) {
            return money;
        }
        String lastThree = money.substring(money.length() - 3);
        String remaining = money.substring(0, money.length() - 3);

        StringBuilder formatted = new StringBuilder();
        for (int i = remaining.length(); i > 0; i -= 2) {
            if (i > 2) {
                formatted.insert(0, "," + remaining.substring(i - 2, i));
            } else {
                formatted.insert(0, remaining.substring(0, i));
            }
        }
        return formatted + "," + lastThree;
    }
}
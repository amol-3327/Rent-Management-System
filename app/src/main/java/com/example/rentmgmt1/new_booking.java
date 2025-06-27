package com.example.rentmgmt1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.datepicker.MaterialDatePicker;

import org.json.JSONArray;
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

public class new_booking extends AppCompatActivity {

    public static new_booking book;
    private AlertDialog progressDialog;
    private Spinner spin_flat,spin_payment;
    ArrayList<String> flat_array=new ArrayList<>();
    ArrayAdapter<String> flat_adapter;
    private String st_flat,st_p_mode,st_flat_replace,st_l_date,st_f_depo;
    private EditText name,mob,depo,town,ppl,s_date;
    private Button cancel,register;
    private flats fl=new flats();
    private bookings bo=new bookings();
    private LinearLayout l_date;
    private TextView t_l_date,f_depo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_new_booking);
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

        Toolbar toolbar = findViewById(R.id.n_b_toolbar);
        setSupportActionBar(toolbar);
        setTitle("New Booking:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new AlertDialog.Builder(new_booking.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        spin_flat = findViewById(R.id.spin_flat);
        spin_flat.setPopupBackgroundResource(android.R.color.white);
        spin_payment = findViewById(R.id.spin_payment);
        spin_payment.setPopupBackgroundResource(android.R.color.white);

        name = findViewById(R.id.ed_name);
        mob = findViewById(R.id.ed_mob);
        depo = findViewById(R.id.ed_depo);
        town = findViewById(R.id.ed_town);
        ppl = findViewById(R.id.ed_cnt);
        s_date = findViewById(R.id.ed_s_date);
        cancel = findViewById(R.id.bt_cancel);
        register = findViewById(R.id.bt_reg);
        l_date = findViewById(R.id.ll_l_date);
        t_l_date = findViewById(R.id.tv_l_date);
        f_depo = findViewById(R.id.tv_f_depo);

        String[] paymentModes = {"Select Mode","Cash","Online"};

        ArrayAdapter<String> pay_adapter = new ArrayAdapter<>(new_booking.this, R.layout.spinner_item, paymentModes);
        pay_adapter.setDropDownViewResource(R.layout.spinner_item);
        spin_payment.setAdapter(pay_adapter);

        spin_payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st_p_mode = parent.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spin_flat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                st_flat = parent.getSelectedItem().toString();

                st_flat_replace = st_flat.replaceAll("[^0-9]", "");

                TextView textView = (TextView) view;
                textView.setTextColor(Color.BLACK);
                textView.setTypeface(textView.getTypeface(), Typeface.BOLD);

                if (!st_flat.contains("(Vacated)")) {
                    l_date.setVisibility(GONE);
                } else {
                    l_date.setVisibility(VISIBLE);
                    fetch_l_date(st_flat_replace);
                }

                if(!st_flat.equals("Flat's...")){
                    fetch_flat_deposit();
                }

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        s_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDatePicker.Builder<Long> builder5 = MaterialDatePicker.Builder.datePicker();
                builder5.setTitleText("Select Date");
                MaterialDatePicker<Long> materialDatePicker = builder5.build();
                materialDatePicker.show(new_booking.this.getSupportFragmentManager(), "DATE_PICKER");
                materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                    String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selection);
                    s_date.setText(formattedDate);
                });
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String st_name=name.getText().toString().trim();
                String st_mob=mob.getText().toString().trim();
                String st_depo=depo.getText().toString().trim();
                String st_town=town.getText().toString().trim();
                String st_ppl=ppl.getText().toString().trim();
                String st_s_date=s_date.getText().toString().trim();

                if (st_name.isEmpty()){
                    name.setError("Please enter name");
                }else if (st_mob.isEmpty() || st_mob.length() != 10) {
                    mob.setError("Please enter valid mobile number");
                }else if (st_depo.isEmpty() || st_depo.equals("0")) {
                    depo.setError("Please enter deposit");
                }else if (st_town.isEmpty()){
                    town.setError("Please enter home town");
                }else if (st_ppl.isEmpty()){
                    ppl.setError("Please enter people count");
                }else if (st_s_date.isEmpty()){
                    s_date.setError("Please select starting date");
                }else if (st_flat.equals("Flat's...")){
                    Toast.makeText(new_booking.this, "Please select flat ", Toast.LENGTH_SHORT).show();
                }else if (st_p_mode.equals("Select Mode")){
                    Toast.makeText(new_booking.this, "Please select mode ", Toast.LENGTH_SHORT).show();
                }else{

                    BigInteger b_f_depo = new BigInteger(st_f_depo.replaceAll(",", ""));
                    BigInteger b_f_depo_input = new BigInteger(st_depo.replaceAll(",", ""));

                    if (b_f_depo_input.compareTo(b_f_depo) > 0) {
                        Toast.makeText(new_booking.this, "Invalid deposit amount...", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (st_flat.contains("(Vacated)")) {
                        Date s_Date = null;
                        Date l_date = null;
                        SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                        SimpleDateFormat inputFormat2 = new SimpleDateFormat("dd-MMMM-yyyy");

                        try {
                            s_Date = inputFormat1.parse(st_s_date);
                            l_date = inputFormat2.parse(st_l_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        if (s_Date.after(l_date)) {
                        } else {
                            AlertDialog.Builder builderDel = new AlertDialog.Builder(new_booking.this);
                            builderDel.setMessage("Starting date should be greater then Leaving date:");
                            builderDel.setCancelable(false);
                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface1, int i) {
                                    dialogInterface1.dismiss();
                                    return;
                                }
                            });
                            builderDel.create().show();
                            return;
                        }
                    }
                    String flat_replaced = st_flat.replaceAll("[^0-9]", "");

                    AlertDialog.Builder builderDel = new AlertDialog.Builder(new_booking.this);
                    builderDel.setTitle("New Booking: "+st_name);
                    builderDel.setMessage("Are you sure, You want to Register?");
                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                            SimpleDateFormat inputFormatter1 = new SimpleDateFormat("dd-MM-yy");
                            SimpleDateFormat outputFormatter1 = new SimpleDateFormat("yyyy-MM-dd");

                            Date date9 = null;
                            try {
                                date9 = inputFormatter1.parse(st_s_date);
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                            String formattedDate = outputFormatter1.format(date9);

                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_NEW_BOOKING,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(new_booking.this);
                                                    builderDel.setTitle(st_flat);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface1, int i) {
                                                            dialogInterface1.dismiss();
                                                            finish();
                                                            sendWhatsapp(st_name, st_mob, st_depo,st_s_date);

                                                            new Handler().postDelayed(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    startActivity(new Intent(new_booking.this, bookings.class));
                                                                }
                                                            }, 500);
                                                        }
                                                    });
                                                    builderDel.create().show();
                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(new_booking.this);
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
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(new_booking.this);
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
                                    params.put("name", st_name);
                                    params.put("mob", st_mob);
                                    params.put("depo", st_depo);
                                    params.put("town", st_town);
                                    params.put("ppl", st_ppl);
                                    params.put("flat", flat_replaced);
                                    params.put("s_date", formattedDate);
                                    params.put("p_mode", st_p_mode);
                                    params.put("b_type", st_flat);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(new_booking.this);
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

        fetch_flats();
    }

    public void fetch_flats(){
        flat_array.clear();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SPIN_FLATS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if (!jsonObject.getBoolean("error")) {
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                flat_array.add("Flat's...");

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject o = jsonArray.getJSONObject(i);
                                    String flats =  o.getString("flat_no");
                                    String flats_info =  o.getString("flat_info");

                                    if (flats_info.equals("Vacated")){
                                        flat_array.add("F-"+flats+" (Vacated)");
                                    }else{
                                        flat_array.add("F-"+flats);
                                    }

                                    flat_adapter = new ArrayAdapter<>(new_booking.this, R.layout.spinner_item, flat_array);
                                    flat_adapter.setDropDownViewResource(R.layout.spinner_item);
                                    spin_flat.setAdapter(flat_adapter);
                                }
                            }else{
                                flat_array.clear();

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(new_booking.this);
                                builder1.setTitle(jsonObject.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fetch_flats();
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(new_booking.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                //fetch_flats();
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
                params.put("flats", "flats");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(new_booking.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void fetch_l_date(String st_flat1){
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_CUST_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                st_l_date=obj.getString("l_date");
                                t_l_date.setText(st_l_date);
                            } else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(new_booking.this);
                                builder1.setTitle(obj.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fetch_l_date(st_flat_replace);
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(new_booking.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                finish();
                                fetch_l_date(st_flat_replace);
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
                params.put("flat",st_flat1 );
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(new_booking.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
    public void fetch_flat_deposit(){

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_FETCH_FLAT_DEPO,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {
                                st_f_depo=obj.getString("flat_depo");
                                f_depo.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +st_f_depo+"/-</font></b>"));
                            } else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(new_booking.this);
                                builder1.setTitle(obj.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fetch_flats();
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
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(new_booking.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_flats();
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
                params.put("flat", st_flat_replace);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(new_booking.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void sendWhatsapp(String st_s_name,String st_s_mob,String st_s_pd,String s_date){

        BigInteger t_d=new BigInteger(st_f_depo.replaceAll(",", ""));
        BigInteger p_d = new BigInteger(st_s_pd.replaceAll(",", ""));

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy"); // Format: DD-MM-YYYY
        String todayDate = sdf.format(new Date());

        String message = "Booking Confirmed for Flat No : F-" + st_flat.replaceAll("[^0-9]", "") +"\n\n" +
                "Name : " +  st_s_name + "\n" +
                "Total Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +st_f_depo+"/-</font></b>") + "\n" +
                "Paid Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +IND_money_format(st_s_pd)+"/-</font></b>") + "\n" +
                "Remaining Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +IND_money_format(String.valueOf(t_d.subtract(p_d)))+"/-</font></b>") + "\n" +
                "Payment Mode : " + st_p_mode + "\n"+
                "Payment/Booking Date : " + todayDate + "\n"+
                "Starting Date : " + s_date + "\n";

//        String phoneNumber="+91"+st_s_mob;
        String phoneNumber="+919764751241";

        String encodedMessage = Uri.encode(message);
        String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        startActivity(intent);
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
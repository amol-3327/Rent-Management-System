package com.example.rentmgmt1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class flat_details extends AppCompatActivity {

    private AlertDialog progressDialog;
    private String flat,st_c_id,st_td,st_pd,st_rd,st_p_mode,st_p_mode_c,st_name,st_l_date,st_pr,input,st_mob,c_formattedDate;
    private TextView flat_no,name,mob,town,ppl,depo,pd,pr,rd,b_date,s_date,p_depo,l_date,c_l_date;
    private DecimalFormat decimalFormat = new DecimalFormat("#");
    private Button r_history,d_history,c_account;
    public static flat_details fd;
    private LinearLayout l_l_date;
    private  BigInteger b_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_flat_details);
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

        flat = getIntent().getStringExtra("flat");

        Toolbar toolbar = findViewById(R.id.f_d_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Flat Details:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        flat_no = findViewById(R.id.tv_flat_no);
        name = findViewById(R.id.tv_name);
        mob = findViewById(R.id.tv_mob);
        town = findViewById(R.id.tv_town);
        ppl = findViewById(R.id.tv_ppl);
        depo = findViewById(R.id.tv_depo);
        pd = findViewById(R.id.tv_pd);
        rd = findViewById(R.id.tv_rd);
        pr = findViewById(R.id.tv_pr);
        b_date = findViewById(R.id.tv_b_date);
        s_date = findViewById(R.id.tv_s_date);
        p_depo = findViewById(R.id.tv_p_depo);
        r_history = findViewById(R.id.bt_r_history);
        d_history = findViewById(R.id.bt_d_hostory);
        c_account = findViewById(R.id.bt_c_account);
        l_l_date = findViewById(R.id.ll_l_date);
        l_date = findViewById(R.id.tv_l_date);
        c_l_date = findViewById(R.id.tv_c_l_date);

        progressDialog = new AlertDialog.Builder(flat_details.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        fd=this;

        flat_no.setText("F-"+flat);
        fetch_f_details(flat);

        c_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater inflater = LayoutInflater.from(flat_details.this);
                View dialogView = inflater.inflate(R.layout.alert_close_account, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(flat_details.this);
                builder.setView(dialogView);
                AlertDialog alertDialog12 = builder.create();
                alertDialog12.show();
                alertDialog12.setCancelable(false);

                TextView tv_a_flat = dialogView.findViewById(R.id.tv_flat);
                TextView tv_a_name = dialogView.findViewById(R.id.tv_name);
                TextView tv_a_td = dialogView.findViewById(R.id.tv_a_td);
                TextView tv_a_pd = dialogView.findViewById(R.id.tv_a_pd);
                TextView tv_a_ro = dialogView.findViewById(R.id.tv_a_ro);
                TextView tv_a_ra = dialogView.findViewById(R.id.tv_a_rm);
                EditText ed_char = dialogView.findViewById(R.id.ed_charges);
                EditText c_date = dialogView.findViewById(R.id.ed_c_date);
                Spinner spin_payment=dialogView.findViewById(R.id.spin_a_payment);
                Button cancel= dialogView.findViewById(R.id.b_cancel);
                Button close= dialogView.findViewById(R.id.b_close);

                tv_a_flat.setText("F-"+flat);
                tv_a_name.setText(st_name);
                tv_a_td.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_td + "/-</font></b>"));
                tv_a_pd.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_pd + "/-</font></b>"));
                tv_a_ro.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +st_pr+"/-</font></b>"));

                c_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialDatePicker.Builder<Long> builder5 = MaterialDatePicker.Builder.datePicker();
                        builder5.setTitleText("Select Date");
                        MaterialDatePicker<Long> materialDatePicker = builder5.build();
                        materialDatePicker.show(flat_details.this.getSupportFragmentManager(), "DATE_PICKER");
                        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                            String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selection);
                            c_date.setText(formattedDate);
                        });
                    }
                });

                BigInteger b_pd = new BigInteger(st_pd.replaceAll(",", ""));
                BigInteger b_ro = new BigInteger(st_pr.replaceAll(",", ""));

                ed_char.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {}

                    @Override
                    public void afterTextChanged(Editable editable) {
                        input = editable.toString();

                        BigInteger b_input = new BigInteger(input != null && !input.isEmpty() ? input.replaceAll(",", "") : "0");
                        BigInteger b_rem = b_pd.subtract(b_ro);
                        b_result = b_rem.subtract(b_input);

                        String message = b_result.compareTo(BigInteger.ZERO) > 0
                                ? "We have to pay: <b><b>&#8377; " +IND_money_format(String.valueOf(b_result)) + "/-</b>"
                                : b_result.compareTo(BigInteger.ZERO) < 0
                                ? "We have to receive: <b><b>&#8377; " + IND_money_format(String.valueOf(b_result.abs())) + "/-</b>"
                                : "<b>No action needed..</b>";

                        tv_a_ra.setText(Html.fromHtml(message));
                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        alertDialog12.dismiss();
                    }
                });

                String[] paymentModes = {"Select Mode","Cash","Online"};

                ArrayAdapter<String> pay_adapter = new ArrayAdapter<>(flat_details.this, R.layout.spinner_item, paymentModes);
                pay_adapter.setDropDownViewResource(R.layout.spinner_item);
                spin_payment.setAdapter(pay_adapter);
                spin_payment.setPopupBackgroundResource(android.R.color.white);

                spin_payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        st_p_mode_c = parent.getSelectedItem().toString();
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });

                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       String st_a_input=ed_char.getText().toString().trim();
                       String st_c_date=c_date.getText().toString().trim();

                       if(st_a_input.isEmpty()){
                           ed_char.setError("Please enter charges");
                       }else if (st_c_date.isEmpty()) {
                           c_date.setError("Please select date");
                       }else if (st_p_mode_c.equals("Select Mode")) {
                           Toast.makeText(flat_details.this, "Please select mode ", Toast.LENGTH_SHORT).show();
                       }else{
                           AlertDialog.Builder builder1 = new AlertDialog.Builder(flat_details.this);
                           builder1.setTitle("Close Account: "+st_name);
                           builder1.setMessage("Are you sure, You want to Close ?");
                           builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog, int which) {
                                   dialog.dismiss();

                                   SimpleDateFormat inputFormatter = new SimpleDateFormat("dd-MM-yy");
                                   SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");

                                   Date date9 = null;
                                   try {
                                       date9 = inputFormatter.parse(st_c_date);
                                   } catch (ParseException e) {
                                       throw new RuntimeException(e);
                                   }
                                   c_formattedDate = outputFormatter.format(date9);

                                   progressDialog.show();
                                    StringRequest stringRequest = new StringRequest(
                                            Request.Method.POST,
                                            Constants.URL_UPDATE_CLOSE_ACCOUNT,
                                            new Response.Listener<String>() {
                                                @Override
                                                public void onResponse(String response) {
                                                    progressDialog.dismiss();
                                                    try {
                                                        JSONObject obj = new JSONObject(response);
                                                        if (!obj.getBoolean("error")) {
                                                            alertDialog12.dismiss();
                                                            final_close_Account(st_a_input,tv_a_ra.getText().toString().trim(),st_c_date);
                                                        } else {
                                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
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
                                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(flat_details.this);
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
                                            params.put("id", st_c_id);
                                            params.put("td", st_td.replaceAll(",", ""));
                                            params.put("pd", st_pd.replaceAll(",", ""));
                                            params.put("rent_o", st_pr.replaceAll(",", ""));
                                            params.put("charge",st_a_input);
                                            params.put("mode",st_p_mode_c);
                                            params.put("rmk",tv_a_ra.getText().toString().trim());
                                            params.put("c_date",c_formattedDate);
                                            return params;
                                        }
                                    };
                                    RequestQueue requestQueue = Volley.newRequestQueue(flat_details.this);
                                    requestQueue.add(stringRequest);

                                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                        @Override
                                        public void onRequestFinished(Request<Object> request) {
                                            requestQueue.getCache().clear();
                                        }
                                    });


                               }
                           });
                           builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialogInterface, int i) {
                                   dialogInterface.dismiss();
                               }
                           });
                           AlertDialog dialog = builder1.create();
                           dialog.show();
                       }


                    }
                });
            }
        });
        r_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(flat_details.this, rent_history.class);
                intent.putExtra("flat", flat);
                intent.putExtra("c_id", st_c_id);
                v.getContext().startActivity(intent);
            }
        });

        d_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(flat_details.this, deposit_history.class);
                intent.putExtra("flat", flat);
                intent.putExtra("c_id", st_c_id);
                v.getContext().startActivity(intent);
            }
        });

        c_l_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
                builderDel.setTitle("Cancel Leaving: "+st_name);
                builderDel.setMessage("Are you sure, You want to Cancel?");
                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();

                        progressDialog.show();
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                Constants.URL_CANCEL_LEAVING,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            if (!obj.getBoolean("error")) {
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
                                                builderDel.setMessage(obj.getString("message"));
                                                builderDel.setCancelable(false);
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                        fetch_f_details(flat);

                                                        String message1 = "Leaving cancelled for Flat No: F-" + flat + "\n" ;

//                                                        String phoneNumber = "+91"+st_mob;
                                                        String phoneNumber = "+919764751241";
                                                        String encodedMessage = Uri.encode(message1);
                                                        String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                                        Intent intent11 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                        startActivity(intent11);

                                                    }
                                                });
                                                builderDel.create().show();

                                            } else {
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
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
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(flat_details.this);
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
                                params.put("id", st_c_id);
                                params.put("flat",flat);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(flat_details.this);
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
        });

        p_depo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LayoutInflater inflater = LayoutInflater.from(flat_details.this);
                View dialogView = inflater.inflate(R.layout.alert_pay_deposit, null);
                AlertDialog.Builder builder = new AlertDialog.Builder(flat_details.this);
                builder.setView(dialogView);
                builder.setCancelable(false);
                AlertDialog alertDialog12 = builder.create();
                alertDialog12.show();

                TextView a_flat_no = dialogView.findViewById(R.id.tv_a_flat_no);
                TextView a_td = dialogView.findViewById(R.id.tv_a_td);
                TextView a_pd = dialogView.findViewById(R.id.tv_a_pd);
                TextView a_rd = dialogView.findViewById(R.id.tv_a_rd);
                Spinner spin_payment=dialogView.findViewById(R.id.spin_a_payment);
                Button a_cancel=dialogView.findViewById(R.id.b_a_cancel);
                Button a_update=dialogView.findViewById(R.id.b_a_update);
                EditText a_depo=dialogView.findViewById(R.id.ed_a_depo);
                EditText a_date=dialogView.findViewById(R.id.ed_a_date);

                a_date.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MaterialDatePicker.Builder<Long> builder5 = MaterialDatePicker.Builder.datePicker();
                        builder5.setTitleText("Select Date");
                        MaterialDatePicker<Long> materialDatePicker = builder5.build();
                        materialDatePicker.show(flat_details.this.getSupportFragmentManager(), "DATE_PICKER");
                        materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                            String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selection);
                            a_date.setText(formattedDate);
                        });
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

                        String st1_a_date=a_date.getText().toString().trim();
                        String st1_a_depo=a_depo.getText().toString().trim();

                        if (st1_a_date.isEmpty()){
                            a_date.setError("Please select date");
                        }else if (st1_a_depo.isEmpty()){
                            a_depo.setError("Please enter deposit");
                        }else if (st_p_mode.equals("Select Mode")){
                            Toast.makeText(flat_details.this, "Please select mode ", Toast.LENGTH_SHORT).show();
                        }else{
                            Double d_rd = Double.parseDouble(st_rd.replaceAll(",", ""));
                            Double d_new = Double.parseDouble(st1_a_depo);

                            //decimalFormat.format(d_new)

                            if (d_new <= d_rd) {
                                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
                                builderDel.setTitle("Deposit Paying: "+st_name);
                                builderDel.setMessage("Are you sure, You want to Update?");
                                builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();

                                        SimpleDateFormat inputFormatter = new SimpleDateFormat("dd-MM-yy");
                                        SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");

                                        Date date9 = null;
                                        try {
                                            date9 = inputFormatter.parse(st1_a_date);
                                        } catch (ParseException e) {
                                            throw new RuntimeException(e);
                                        }
                                        String formattedDate = outputFormatter.format(date9);

                                        progressDialog.show();
                                        StringRequest stringRequest = new StringRequest(
                                                Request.Method.POST,
                                                Constants.URL_PAY_DEPOSIT,
                                                new Response.Listener<String>() {
                                                    @Override
                                                    public void onResponse(String response) {
                                                        progressDialog.dismiss();
                                                        try {
                                                            JSONObject obj = new JSONObject(response);
                                                            if (!obj.getBoolean("error")) {
                                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
                                                                builderDel.setTitle("F-"+flat);
                                                                builderDel.setMessage(obj.getString("message"));
                                                                builderDel.setCancelable(false);
                                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                    @Override
                                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                                        dialogInterface.dismiss();
                                                                        alertDialog12.dismiss();
                                                                        fetch_f_details(flat);

                                                                        sendWhatsapp(st_td,st1_a_depo,st_rd,st1_a_date);
                                                                    }
                                                                });
                                                                builderDel.create().show();

                                                            } else {
                                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
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
                                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(flat_details.this);
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
                                                params.put("c_id", st_c_id);
                                                params.put("depo", decimalFormat.format(d_new));
                                                params.put("p_date", formattedDate);
                                                params.put("p_mode", st_p_mode);
                                                return params;
                                            }
                                        };
                                        RequestQueue requestQueue = Volley.newRequestQueue(flat_details.this);
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
                            } else {
                                Toast.makeText(flat_details.this,  "Invalid Amount", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

                spin_payment.setPopupBackgroundResource(android.R.color.white);

                String[] paymentModes = {"Select Mode","Cash","Online"};

                ArrayAdapter<String> pay_adapter = new ArrayAdapter<>(flat_details.this, R.layout.spinner_item, paymentModes);
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

                a_flat_no.setText("F-"+flat);
                a_td.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_td + "/-</font></b>"));
                a_pd.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_pd + "/-</font></b>"));
                a_rd.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_rd + "/-</font></b>"));
            }
        });
    }

    public void fetch_f_details(String s_flat){

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
                                st_c_id=obj.getString("id");
                                st_name=obj.getString("name");
                                st_mob=obj.getString("mob");
                                mob.setText(st_mob);
                                town.setText(obj.getString("town"));
                                ppl.setText(obj.getString("ppl"));
                                st_pr=obj.getString("pr");

                                st_td=obj.getString("depo");
                                st_pd=obj.getString("pd");
                                st_rd=obj.getString("rd");

                                String flat_replaced = st_rd.replaceAll("[^0-9]", "");
                                if(flat_replaced.equals("0")){
                                    p_depo.setVisibility(GONE);
                                }else{
                                    p_depo.setVisibility(VISIBLE);
                                }
                                name.setText(st_name);
                                depo.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_td + "/-</font></b>"));
                                pd.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_pd + "/-</font></b>"));
                                rd.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + st_rd + "/-</font></b>"));

                                pr.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +st_pr+"/-</font></b>"));

                                b_date.setText(obj.getString("b_date"));
                                s_date.setText(obj.getString("s_date"));
                                st_l_date=obj.getString("l_date");

                                l_date.setText(st_l_date);

                                if(st_l_date.equals("null")){
                                    l_l_date.setVisibility(GONE);
                                    c_account.setVisibility(GONE);
                                }else{
                                    l_l_date.setVisibility(VISIBLE);
                                    c_account.setVisibility(VISIBLE);
                                }
                            } else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(flat_details.this);
                                builder1.setTitle(obj.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fetch_f_details(flat);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(flat_details.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_f_details(flat);
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
                params.put("flat", s_flat);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(flat_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    public void sendWhatsapp(String st_s_td,String st_s_pd,String st_s_rd,String st_date){

        BigInteger rem=new BigInteger(st_s_rd.replaceAll(",", ""));
        BigInteger p_rem = new BigInteger(st_s_pd.replaceAll(",", ""));

        String message = "Deposit Received for Flat No : F-" + flat +"\n\n" +
                "Name : " +  st_name + "\n" +
                "Total Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +st_s_td+"/-</font></b>") + "\n" +
                "Paid Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +IND_money_format(String.valueOf(p_rem))+"/-</font></b>") + "\n" +
                "Remaining Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +IND_money_format(String.valueOf(rem.subtract(p_rem)))+"/-</font></b>") + "\n" +
                "Payment Date : " + st_date + "\n"+
                "Payment Mode : " + st_p_mode + "\n";

//        String phoneNumber="+91"+st_mob;
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
    public void final_close_Account(String st_charge,String rmk, String st_c_date1){

                        progressDialog.show();
                        StringRequest stringRequest = new StringRequest(
                                Request.Method.POST,
                                Constants.URL_DELETE_ACCOUNT,
                                new Response.Listener<String>() {
                                    @Override
                                    public void onResponse(String response) {
                                        progressDialog.dismiss();
                                        try {
                                            JSONObject obj = new JSONObject(response);
                                            if (!obj.getBoolean("error")) {
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
                                                builderDel.setMessage(obj.getString("message"));
                                                builderDel.setCancelable(false);
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();

                                                        String numbers = (rmk != null && !rmk.isEmpty()) ? rmk.replaceAll("\\D+", "") : "0";
                                                        String formattedNumbers = IND_money_format(numbers);
                                                        formattedNumbers = (formattedNumbers == null || formattedNumbers.isEmpty()) ? "0" : formattedNumbers;

                                                        String st_text = rmk.contains("We have to pay") ? "You will receive amount: "
                                                                : rmk.contains("We have to receive") ? "You have to pay amount: "
                                                                : "No payment needed :";

                                                        String message1 = "Final bill for Flat No: F-" + flat + "\n\n" +
                                                                "Name : " + st_name + "\n" +
                                                                "Total Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + st_td + "/-</b></font>") + "\n" +
                                                                "Paid Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + st_pd + "/-</b></font>") + "\n" +
                                                                "Rent Outstanding : " + Html.fromHtml("<font color=#000000><b>&#8377; " + st_pr + "/-</b></font>") + "\n" +
                                                                "Any Charges : " + Html.fromHtml("<font color=#000000><b>&#8377; " + IND_money_format(st_charge) + "/-</b></font>") + "\n" +
                                                                "Closing Date : " + Html.fromHtml("<font color=#000000><b> " + st_c_date1 + "</b></font>") + "\n" +
                                                                "--------------------------------------------------------\n" +
                                                                st_text + Html.fromHtml("<font color=#000000><b>&#8377; " + formattedNumbers + "/-</b></font>") + "\n";

//                                                        String phoneNumber = "+91"+st_mob;
                                                        String phoneNumber = "+919764751241";
                                                        String encodedMessage = Uri.encode(message1);
                                                        String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                        startActivity(intent);

                                                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                                            finish();
                                                            startActivity(new Intent(flat_details.this, rentals.class));
                                                        }, 500);
                                                    }
                                                });
                                                builderDel.create().show();

                                            } else {
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(flat_details.this);
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
                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(flat_details.this);
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
                                params.put("id", st_c_id);
                                params.put("flat",flat);
                                return params;
                            }
                        };
                        RequestQueue requestQueue = Volley.newRequestQueue(flat_details.this);
                        requestQueue.add(stringRequest);

                        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                            @Override
                            public void onRequestFinished(Request<Object> request) {
                                requestQueue.getCache().clear();
                            }
                        });
    }
}
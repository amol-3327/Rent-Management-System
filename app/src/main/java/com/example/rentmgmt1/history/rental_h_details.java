package com.example.rentmgmt1.history;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import com.example.rentmgmt1.Constants;
import com.example.rentmgmt1.R;
import com.example.rentmgmt1.SharedPrefManager;
import com.example.rentmgmt1.admin_login;
import com.example.rentmgmt1.flat_details;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;

public class rental_h_details extends AppCompatActivity {

    private AlertDialog progressDialog;
    private TextView flat_no,name,mob,town,ppl,t_depo,pd,o_rent,b_date,s_date,l_date,l_charge,rmk;
    private Button r_history,d_history;
    private String flat,c_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rental_hdetails);
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
        c_id = getIntent().getStringExtra("c_id");

        Toolbar toolbar = findViewById(R.id.f_d_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Flat Details:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        flat_no = findViewById(R.id.tv_flat_no);
        name = findViewById(R.id.tv_name);
        mob = findViewById(R.id.tv_mob);
        town = findViewById(R.id.tv_town);
        ppl = findViewById(R.id.tv_ppl);
        t_depo = findViewById(R.id.tv_depo);
        pd = findViewById(R.id.tv_pd);
        o_rent = findViewById(R.id.tv_o_rent);
        b_date = findViewById(R.id.tv_b_date);
        s_date = findViewById(R.id.tv_s_date);
        l_date = findViewById(R.id.tv_l_date);
        l_charge = findViewById(R.id.tv_l_charge);
        rmk = findViewById(R.id.tv_rmk);
        r_history = findViewById(R.id.bt_r_history);
        d_history = findViewById(R.id.bt_d_history);

        progressDialog = new AlertDialog.Builder(rental_h_details.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        r_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rental_h_details.this, rent_deposit_history.class);
                intent.putExtra("flat", flat);
                intent.putExtra("c_id", c_id);
                intent.putExtra("txt", "r_h");
                startActivity(intent);
            }
        });
        d_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(rental_h_details.this, rent_deposit_history.class);
                intent.putExtra("flat", flat);
                intent.putExtra("c_id", c_id);
                intent.putExtra("txt", "d_h");
                startActivity(intent);
            }
        });

        fetch_f_details(flat,c_id);
    }
    public void fetch_f_details(String st_flat,String st_c_id){

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                H_Constants.URL_FETCH_CUSTOMER_DETAILS,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

                                flat_no.setText("F-"+st_flat );
                                name.setText(obj.getString("name"));
                                mob.setText(obj.getString("mob"));
                                town.setText(obj.getString("town"));
                                ppl.setText(obj.getString("ppl"));
                                t_depo.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("depo") + "/-</font></b>"));
                                pd.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("pd") + "/-</font></b>"));
                                o_rent.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("o_rent") + "/-</font></b>"));
                                b_date.setText(obj.getString("b_date"));
                                s_date.setText(obj.getString("s_date"));
                                l_date.setText(obj.getString("l_date"));
                                l_charge.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("l_charge") + "/-</font></b>"));
                                rmk.setText(obj.getString("rmk"));

                            } else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(rental_h_details.this);
                                builder1.setTitle(obj.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fetch_f_details(st_flat,st_c_id);
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(rental_h_details.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_f_details(st_flat,st_c_id);
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
                params.put("flat", st_flat);
                params.put("c_id", st_c_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(rental_h_details.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}
package com.example.rentmgmt1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class a_forgot_pass extends AppCompatActivity {

    private TextView m_text;
    private EditText email,mob;
    private Button forgot;
    private AlertDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_aforgot_pass);

        m_text = findViewById(R.id.m_t_text1);
        email=findViewById(R.id.a_user_email);
        mob=findViewById(R.id.a_user_mob);
        forgot=findViewById(R.id.a_forgot_pass);

        m_text.setPaintFlags(m_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        progressDialog = new AlertDialog.Builder(a_forgot_pass.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        forgot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String st_email=email.getText().toString().trim();
                final String st_mob=mob.getText().toString().trim();
                if (st_email.isEmpty()){
                    email.setError("Email is required");
                }else if (st_mob.isEmpty()){
                    mob.setError("Mobile number is required");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_A_FORGOT_PASS,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            String pass=obj.getString("message");

                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(a_forgot_pass.this);
                                            builder1.setTitle("Your Password is: " +pass);
                                            builder1.setMessage("Are you want to login again?");
                                            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    dialog.dismiss();
                                                    startActivity(new Intent(getApplicationContext(), admin_login.class));
                                                    finish();
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

                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(a_forgot_pass.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(a_forgot_pass.this);
                                    builderDel.setCancelable(false);
                                    builderDel.setMessage("Network Error, Try Again Later.");
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    builderDel.create().show();
                                }
                            }
                    ) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("email", st_email);
                            params.put("mob", st_mob);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(a_forgot_pass.this);
                    requestQueue.add(stringRequest);

                    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                        @Override
                        public void onRequestFinished(Request<Object> request) {
                            requestQueue.getCache().clear();
                        }
                    });
                }


            }
        });



    }
}
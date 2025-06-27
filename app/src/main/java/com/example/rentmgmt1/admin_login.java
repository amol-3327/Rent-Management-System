package com.example.rentmgmt1;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
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
import androidx.appcompat.app.ActionBar;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class admin_login extends AppCompatActivity {

    private TextView m_text,forgot_pass,a_sign,m_t_text;
    private EditText user_id,user_pass;
    private Button a_login, a_forgot;
    private AlertDialog progressDialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_login);

        if(SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, dashboard.class));
            return;
        }

        m_text = findViewById(R.id.m_text);
        m_t_text = findViewById(R.id.m_t_text);
        user_id = findViewById(R.id.a_user_id);
        user_pass = findViewById(R.id.a_user_pass);
        a_login = findViewById(R.id.a_login);
        forgot_pass=findViewById(R.id.a_forgot_pass);
        a_sign = findViewById(R.id.a_sign);

        a_sign.setText("Designed & Developed by Amol S");

        m_text.setPaintFlags(m_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        a_sign.setPaintFlags(a_sign.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        m_t_text.setPaintFlags(m_text.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        progressDialog = new AlertDialog.Builder(admin_login.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        a_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String st_id=user_id.getText().toString().trim();
                final String st_pass=user_pass.getText().toString().trim();
                if (st_id.isEmpty()){
                    user_id.setError("User ID is required");
                }else if (st_pass.isEmpty()){
                    user_pass.setError("Password is required");
                }else {
                    progressDialog.show();
                    StringRequest stringRequest = new StringRequest(
                            Request.Method.POST,
                            Constants.URL_ADMIN_LOGIN,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            SharedPrefManager.getInstance(getApplicationContext())
                                                    .userLogin(
                                                            obj.getString("id"),
                                                            obj.getString("email"),
                                                            obj.getString("user")
                                                    );
                                            Toast.makeText(admin_login.this, obj.getString("message"), Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), dashboard.class));
                                            finish();
                                        } else {
                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(admin_login.this);
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
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(admin_login.this);
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
                            params.put("u_id", st_id);
                            params.put("u_pass", st_pass);
                            return params;
                        }
                    };
                    RequestQueue requestQueue = Volley.newRequestQueue(admin_login.this);
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

        forgot_pass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(admin_login.this, a_forgot_pass.class));
            }
        });






    }
}
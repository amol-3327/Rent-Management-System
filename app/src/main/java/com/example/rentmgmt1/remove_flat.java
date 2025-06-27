package com.example.rentmgmt1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.text.Html;
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

import androidx.appcompat.app.AppCompatActivity;

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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class remove_flat {

    private Context context;
    private AlertDialog progressDialog;
    private ArrayList<String> flat_array=new ArrayList<>();
    private ArrayAdapter<String> flat_adapter;
    private Spinner spin_flat;
    private String st_flat,st_flat_replace;

    public remove_flat(Context context) {
        this.context = context;
    }

    public void remove_flat(){

        progressDialog = new AlertDialog.Builder(context)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.alert_remove_flat, null);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(dialogView);
        AlertDialog alertDialog12 = builder.create();
        alertDialog12.setCancelable(false);
        alertDialog12.show();

        spin_flat = dialogView.findViewById(R.id.spin_flat);
        spin_flat.setPopupBackgroundResource(android.R.color.white);
        Button cancel= dialogView.findViewById(R.id.b_cancel);
        Button remove= dialogView.findViewById(R.id.b_update);

        spin_flat.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                st_flat = parent.getSelectedItem().toString();

                st_flat_replace = st_flat.replaceAll("[^0-9]", "");
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fetchFlats();

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog12.dismiss();
            }
        });

        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (st_flat.equals("Flat's...")){
                    Toast.makeText(context, "Please select flat ", Toast.LENGTH_SHORT).show();
                }else {
                    AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
                    builderDel.setTitle("Remove Flat: " + st_flat);
                    builderDel.setMessage("Are you sure, You want to Delete?");
                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();

                            progressDialog.show();
                            StringRequest stringRequest = new StringRequest(
                                    Request.Method.POST,
                                    Constants.URL_REMOVE_FLAT,
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            progressDialog.dismiss();
                                            try {
                                                JSONObject obj = new JSONObject(response);
                                                if (!obj.getBoolean("error")) {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
                                                    builderDel.setMessage(obj.getString("message"));
                                                    builderDel.setCancelable(false);
                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();
                                                            alertDialog12.dismiss();
                                                            flats.ft.checkServerAvail();
                                                        }
                                                    });
                                                    builderDel.create().show();
                                                } else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
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
                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
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
                                    params.put("flat", st_flat_replace);
                                    return params;
                                }
                            };
                            RequestQueue requestQueue = Volley.newRequestQueue(context);
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

    public void fetchFlats() {
        flat_array.clear();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_SPIN_ALL_FLATS,
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
                                    String flats_status =  o.getString("flat_status");

                                    flat_array.add("F-"+flats+" ---> "+flats_status);

                                    flat_adapter = new ArrayAdapter<>(context, R.layout.spinner_item, flat_array);
                                    flat_adapter.setDropDownViewResource(R.layout.spinner_item);
                                    spin_flat.setAdapter(flat_adapter);
                                }
                            }else{
                                flat_array.clear();

                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setTitle(jsonObject.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        fetchFlats();
                                    }
                                });
                                builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                        ((Activity) context).finish();
                                    }
                                });
                                AlertDialog dialog = builder1.create();
                                dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                    @Override
                                    public void onShow(DialogInterface arg0) {
                                        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.black));
                                        dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.black));
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetchFlats();
                            }
                        });
                        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                ((Activity) context).finish();
                            }
                        });
                        AlertDialog dialog = builder1.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(context.getResources().getColor(R.color.alert));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(context.getResources().getColor(R.color.alert));
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
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}
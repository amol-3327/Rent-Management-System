package com.example.rentmgmt1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static androidx.core.content.ContextCompat.startActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class flat_adapter extends RecyclerView.Adapter<flat_adapter.ViewHolder> {

    private List<flat_model> listItems;
    private Context context;
    private String st_name,st_s_date,st_c_id;
    private AlertDialog progressDialog;

    public flat_adapter(List<flat_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void filterList(ArrayList<flat_model> filterlist) {

        listItems = filterlist;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id,flat_no,flat_status,flat_type;
        public CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            flat_no = itemView.findViewById(R.id.tv_flat_no);
            flat_type = itemView.findViewById(R.id.tv_flat_type);
            flat_status = itemView.findViewById(R.id.tv_flat_status);
            card_view =  itemView.findViewById(R.id.f_card_view);

            progressDialog = new AlertDialog.Builder(context)
                    .setMessage("Please wait...")
                    .setCancelable(false)
                    .create();
        }
    }

    @Override
    public flat_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.flat_list, parent, false);
        return new flat_adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(flat_adapter.ViewHolder holder, final int position) {
        final flat_model listItem = listItems.get(position);
        holder.id.setText(listItem.getId());
        holder.flat_no.setText("F-"+listItem.getFlat_no());
        holder.flat_type.setText(listItem.getFlat_type());
        holder.flat_status.setText(listItem.getF_status());

        if (listItem.getF_status().equals("Occupied")) {
            holder.flat_status.setTextColor(Color.parseColor("#3700B3"));
        } else if (listItem.getF_status().equals("Available")) {
            holder.flat_status.setTextColor(Color.parseColor("#FF0000"));
        } else if (listItem.getF_status().equals("Booked")) {
            holder.flat_status.setTextColor(Color.parseColor("#ffff8800"));
        }else if (listItem.getF_status().equals("Vacated") ||  listItem.getF_status().equals("Vacated B")) {
            holder.flat_status.setTextColor(Color.parseColor("#FF01AF"));
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View Details:","Book Flat:","Vacate Flat:","View Vacated Bookings:"};
                builder.setTitle("F-"+listItem.getFlat_no()+" :- "+listItem.getF_status());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                if (listItem.getF_status().equals("Occupied") || listItem.getF_status().equals("Vacated") || listItem.getF_status().equals("Vacated B")) {
                                    Intent intent = new Intent(view.getContext(), flat_details.class);
                                    intent.putExtra("flat", listItem.getFlat_no());
                                    view.getContext().startActivity(intent);
                                } else if(listItem.getF_status().equals("Available")){
                                    Toast.makeText(view.getContext(), "Flat is Available...", Toast.LENGTH_SHORT).show();
                                }else if(listItem.getF_status().equals("Booked") ){
                                    Intent intent = new Intent(view.getContext(), bookings.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;

                            case 1:
                                if (listItem.getF_status().equals("Booked") || listItem.getF_status().equals("Vacated B") || listItem.getF_status().equals("Occupied") ) {
                                    Toast.makeText(view.getContext(), "Flat already reserved...", Toast.LENGTH_SHORT).show();
                                } else if(listItem.getF_status().equals("Available") || listItem.getF_status().equals("Vacated")){
                                    Intent intent = new Intent(view.getContext(), new_booking.class);
                                    view.getContext().startActivity(intent);
                                }
                                break;

                            case 2:
                                if (listItem.getF_status().equals("Occupied") ) {

                                    AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                    builder1.setTitle("Vacate Flat: F-"+listItem.getFlat_no());
                                    builder1.setMessage("Are you sure, You want to Vacate?");
                                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                            fetch_details(listItem.getFlat_no());

                                            LayoutInflater inflater = LayoutInflater.from(context);
                                            View dialogView = inflater.inflate(R.layout.alert_vacate, null);
                                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                            builder.setView(dialogView);
                                            AlertDialog alertDialog12 = builder.create();
                                            alertDialog12.show();
                                            builder.setCancelable(false);

                                            EditText etDate1 = dialogView.findViewById(R.id.ed_l_date);
                                            TextView name = dialogView.findViewById(R.id.tv_a_name);
                                            TextView flat = dialogView.findViewById(R.id.tv_a_flat);
                                            TextView s_date = dialogView.findViewById(R.id.tv_s_date);
                                            Button cancel= dialogView.findViewById(R.id.b_cancel);
                                            Button update= dialogView.findViewById(R.id.b_update);

                                            new Handler().postDelayed(() -> {
                                                name.setText(st_name);
                                                s_date.setText(st_s_date);
                                                flat.setText("F-"+listItem.getFlat_no());
                                            }, 500);

                                            etDate1.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    MaterialDatePicker.Builder<Long> builder5 = MaterialDatePicker.Builder.datePicker();
                                                    builder5.setTitleText("Select a Date");
                                                    MaterialDatePicker<Long> materialDatePicker = builder5.build();
                                                    materialDatePicker.show(((AppCompatActivity) context).getSupportFragmentManager(), "DATE_PICKER");
                                                    materialDatePicker.addOnPositiveButtonClickListener(selection -> {
                                                        String formattedDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(selection);
                                                        etDate1.setText(formattedDate);
                                                    });
                                                }
                                            });

                                            cancel.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    alertDialog12.dismiss();
                                                }
                                            });

                                            update.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View view) {
                                                    String dateString=etDate1.getText().toString().trim();
                                                    String st_s_date1=s_date.getText().toString().trim();
                                                    String st_s_name1=name.getText().toString().trim();

                                                    if (dateString.isEmpty()){
                                                        etDate1.setError("Please select date");
                                                    }else if  (st_s_date1.isEmpty() || st_s_name1.isEmpty()){
                                                        Toast.makeText(view.getContext(), "Please try again...", Toast.LENGTH_SHORT).show();
                                                    }
                                                    else {

                                                        Date s_Date = null;
                                                        Date l_date = null;
                                                        SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                                                        SimpleDateFormat inputFormat2 = new SimpleDateFormat("dd-MMMM-yyyy");

                                                        try {
                                                            s_Date = inputFormat2.parse(st_s_date1);
                                                            l_date = inputFormat1.parse(dateString);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }

                                                        if (l_date.after(s_Date)) {
                                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
                                                            builderDel.setTitle("Update Leaving: "+st_name);
                                                            builderDel.setMessage("Are you sure, You want to Update?");
                                                            builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                                    dialogInterface.dismiss();
                                                                    SimpleDateFormat inputFormatter = new SimpleDateFormat("dd-MM-yy");
                                                                    SimpleDateFormat outputFormatter = new SimpleDateFormat("yyyy-MM-dd");

                                                                    Date date9 = null;
                                                                    try {
                                                                        date9 = inputFormatter.parse(dateString);
                                                                    } catch (ParseException e) {
                                                                        throw new RuntimeException(e);
                                                                    }
                                                                    String formattedDate = outputFormatter.format(date9);

                                                                    progressDialog.show();
                                                                    StringRequest stringRequest = new StringRequest(
                                                                            Request.Method.POST,
                                                                            Constants.URL_UPDATE_VACATE,
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

                                                                                                    sendWhatsapp(listItem.getFlat_no());
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
                                                                            params.put("l_date",formattedDate );
                                                                            params.put("flat",listItem.getFlat_no() );
                                                                            params.put("id", st_c_id);
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
                                                        else {
                                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                                            builderDel.setMessage("Leaving date should be greater then starting date:");
                                                            builderDel.setCancelable(false);
                                                            builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialogInterface1, int i) {
                                                                    dialogInterface1.dismiss();

                                                                }
                                                            });
                                                            builderDel.create().show();
                                                        }
                                                    }
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


                                }else{
                                    Toast.makeText(view.getContext(), "Flat should be Occupied...", Toast.LENGTH_SHORT).show();
                                }

                                break;

                            case 3:
                                if (listItem.getF_status().equals("Vacated B") ) {
                                    Intent intent = new Intent(view.getContext(), bookings.class);
                                    view.getContext().startActivity(intent);
                                } else if(listItem.getF_status().equals("Available") || listItem.getF_status().equals("Vacated")){
                                    Toast.makeText(view.getContext(), "Details not available...", Toast.LENGTH_SHORT).show();
                                }
                                break;
                        }
                    }
                });
                builder.create().show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public void fetch_details(String s_flat) {

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

                                st_name=obj.getString("name");
                                st_s_date=obj.getString("s_date");
                                st_c_id=obj.getString("id");

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

                        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        dialog1.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("flat",s_flat);
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

    public void sendWhatsapp(String st_flat){

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

                                String message1 = "Leaving details for Flat No: F-" + st_flat + "\n\n" +
                                        "Name : " + obj.getString("name") + "\n" +
                                        "Starting Date : " + Html.fromHtml("<font color=#000000><b> " + obj.getString("s_date") + "</font>") + "\n" +
                                        "Leaving Date : " + Html.fromHtml("<font color=#000000><b> " + obj.getString("l_date") + "</b></font>") + "\n" +
                                        "Total Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + obj.getString("depo") + "/-</b></font>") + "\n" +
                                        "Paid Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + obj.getString("pd") + "/-</b></font>") + "\n" +
                                        "Remaining Deposit : " + Html.fromHtml("<font color=#000000><b>&#8377; " + obj.getString("rd") + "/-</b></font>") + "\n" +
                                        "Outstanding Rent : " + Html.fromHtml("<font color=#000000><b>&#8377; " +obj.getString("pr")  + "/-</b></font>") + "\n" ;


//                                String phoneNumber = "+91"+obj.getString("mob");
                                String phoneNumber = "+919764751241";
                                String encodedMessage = Uri.encode(message1);
                                String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                Intent intent11 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                context.startActivity(intent11);

                            } else {
                                progressDialog.dismiss();
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                                builder1.setTitle(obj.getString("message"));
                                builder1.setMessage("Please refresh again...");
                                builder1.setCancelable(false);
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        sendWhatsapp(st_flat);
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
                                sendWhatsapp(st_flat);
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
                params.put("flat", st_flat);
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
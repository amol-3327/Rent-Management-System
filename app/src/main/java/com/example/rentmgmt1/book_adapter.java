package com.example.rentmgmt1;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class book_adapter extends RecyclerView.Adapter<book_adapter.ViewHolder> {

    private List<book_model> listItems;
    private Context context;
    private ProgressDialog dialog;
    private String re;
    private AlertDialog progressDialog;
    private SimpleDateFormat dateFormat;
    private String st_o_date;

    public book_adapter(List<book_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void filterList(ArrayList<book_model> filterlist) {

        listItems = filterlist;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id,name,mob,deposit,town,b_date,s_date,flat,cnt;
        public CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            name = itemView.findViewById(R.id.tv_name);
            mob = itemView.findViewById(R.id.tv_mob);
            deposit = itemView.findViewById(R.id.tv_depo);
            town = itemView.findViewById(R.id.tv_home);
            b_date = itemView.findViewById(R.id.tv_b_date);
            s_date = itemView.findViewById(R.id.tv_s_date);
            flat = itemView.findViewById(R.id.tv_flat);
            cnt = itemView.findViewById(R.id.tv_ppl);
            card_view =  itemView.findViewById(R.id.b_card_view);

            progressDialog = new AlertDialog.Builder(context)
                    .setMessage("Please wait...")
                    .setCancelable(false)
                    .create();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.booking_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final book_model listItem = listItems.get(position);
        holder.id.setText(listItem.getId());
        holder.name.setText(listItem.getName());
        holder.mob.setText(listItem.getMob());
        holder.deposit.setText(Html.fromHtml("<b>&#8377;" + listItem.getDeposit() + "/-</b>")+" ("+listItem.getMode()+")");
        holder.town.setText(listItem.getTown());
        holder.b_date.setText(listItem.getB_date());
        holder.s_date.setText(listItem.getS_date());

        String type=listItem.getType();
        if (type.contains("(Vacated)")) {
            holder.flat.setText(Html.fromHtml("<b><span style='color:#FF0000;'>&#127968; &nbsp;&nbsp;F-" + listItem.getFlat() + " V"+"</span></b>"));
        }else {
            holder.flat.setText(Html.fromHtml("<b><span style='color:#FF0000;'>&#127968; &nbsp;&nbsp;F-" + listItem.getFlat() + "</span></b>"));

        }

        holder.cnt.setText(Html.fromHtml("<b><span style='color:#FF0000;'>&#128101;</span>&nbsp;&nbsp;" + listItem.getCnt() + "</b>"));

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                dateFormat = new SimpleDateFormat("dd-MM-yyyy");

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"Confirm Booking:","Update New Date:","Cancel Booking:","Call the Person:"};
                builder.setTitle("F-"+listItem.getFlat()+":- "+listItem.getName());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(view.getContext(), confirm_booking.class);
                                intent.putExtra("id", listItem.getId());
                                intent.putExtra("name", listItem.getName());
                                intent.putExtra("mob", listItem.getMob());
                                intent.putExtra("deposit", listItem.getDeposit());
                                intent.putExtra("town", listItem.getTown());
                                intent.putExtra("b_date", listItem.getB_date());
                                intent.putExtra("s_date", listItem.getS_date());
                                intent.putExtra("flat", listItem.getFlat());
                                intent.putExtra("cnt", listItem.getCnt());
                                intent.putExtra("p_mode", listItem.getMode());
                                intent.putExtra("t_depo", listItem.getT_depo());
                                view.getContext().startActivity(intent);
                            break;

                            case 1:
                                LayoutInflater inflater = LayoutInflater.from(context);
                                View dialogView = inflater.inflate(R.layout.alert_edit_booking, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);
                                AlertDialog alertDialog12 = builder.create();
                                alertDialog12.show();
                                alertDialog12.setCancelable(false);

                                EditText etDate1 = dialogView.findViewById(R.id.ed_u_s_date1);
                                TextView flat = dialogView.findViewById(R.id.tv_flat);
                                TextView name = dialogView.findViewById(R.id.tv_name);
                                TextView o_date = dialogView.findViewById(R.id.tv_o_date);
                                Button cancel= dialogView.findViewById(R.id.b_cancel);
                                Button update= dialogView.findViewById(R.id.b_update);

                                name.setText(listItem.getName());
                                flat.setText("F-"+listItem.getFlat());
                                o_date.setText(listItem.getS_date());
                                st_o_date=listItem.getS_date();

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

                                        if (dateString.isEmpty()){
                                            etDate1.setError("Please select date");
                                        }else{

                                            Date o_Date = null;
                                            Date n_date = null;
                                            SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd-MM-yyyy");
                                            SimpleDateFormat inputFormat2 = new SimpleDateFormat("dd-MMMM-yyyy");

                                            try {
                                                o_Date = inputFormat2.parse(st_o_date);
                                                n_date = inputFormat1.parse(dateString);
                                            } catch (ParseException e) {
                                                e.printStackTrace();
                                            }

                                            if (n_date.after(o_Date)) {

                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
                                                builderDel.setTitle("Update Booking: "+listItem.getName());
                                                builderDel.setMessage("Are you sure, You want to update?");
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
                                                                Constants.URL_UPDATE_B_START_DATE,
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
                                                                                        bookings.book.checkServerAvail();

                                                                                        SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
                                                                                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MMMM-yyyy");
                                                                                        String formattedDate1="null";
                                                                                        try {
                                                                                            Date o_Date = inputFormat.parse(dateString); // Convert String to Date
                                                                                            formattedDate1 = outputFormat.format(o_Date); // Format the Date
                                                                                        } catch (ParseException e) {
                                                                                            e.printStackTrace();
                                                                                        }

                                                                                        String message1 = "Starting date updated for Flat No : F-" + listItem.getFlat() + "\n\n" +
                                                                                                "Name: " + listItem.getName() + "\n" +
                                                                                                "Old starting date: " + Html.fromHtml("<b><font color=#000000> " + st_o_date + "</font></b>") + "\n" +
                                                                                                "New starting date: " + Html.fromHtml("<b><font color=#000000> " +  formattedDate1 + "</font></b>") + "\n" ;

//                                                                                        String phoneNumber = "+91"+listItem.getMob();
                                                                                        String phoneNumber = "+919764751241";
                                                                                        String encodedMessage = Uri.encode(message1);
                                                                                        String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                                                                        Intent intent11 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                                                        context.startActivity(intent11);
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
                                                                params.put("date",formattedDate );
                                                                params.put("id", listItem.getId());
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

                                            }else{
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                                builderDel.setMessage("New date should be greater then old date:");
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
                            break;

                            case 2:
                                delete_b(listItem.getId(),listItem.getName(),listItem.getFlat(),listItem.getType(),listItem.getMob(),listItem.getDeposit());
                            break;

                            case 3:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                builder1.setTitle("Call the person:");
                                builder1.setMessage("Are you sure, You want to Call?");
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        String phoneNumber = listItem.getMob();
                                        Intent intent12 = new Intent(Intent.ACTION_DIAL);
                                        intent12.setData(Uri.parse("tel:" + phoneNumber));
                                        context.startActivity(intent12);
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

    public void delete_b(String id,String st_name,String s_flat,String type,String st_mob,String st_depo){

        AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
        builderDel.setTitle("Cancel Booking: "+st_name);
        builderDel.setMessage("Are you sure, You want to Cancel?");
        builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                progressDialog.show();
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        Constants.URL_DELETE_BOOKING,
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
                                                bookings.book.checkServerAvail();

                                                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
                                                String sysdate = sdf.format(new Date());

                                                String message1 = "Booking cancelled for Flat No : F-" + s_flat + "\n\n" +
                                                        "Name: " + st_name + "\n" +
                                                        "Deposit Paid: " + Html.fromHtml("<b><font color=#000000>&#8377; " + st_depo + "/-</font></b>") + "\n" +
                                                        "Cancellation Date: " + sysdate + "\n" +
                                                        "-------------------------------------------------\n" +
                                                        "You will receive a refund of : " + Html.fromHtml("<b><font color=#000000>&#8377; " + st_depo + "/-</font></b>") + "\n";

//                                                String phoneNumber = "+91"+st_mob;
                                                String phoneNumber = "+919764751241";
                                                String encodedMessage = Uri.encode(message1);
                                                String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                                Intent intent11 = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                                context.startActivity(intent11);

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
                        params.put("id", id);
                        params.put("flat",s_flat);
                        params.put("type",type);
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

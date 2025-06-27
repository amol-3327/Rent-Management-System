package com.example.rentmgmt1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
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
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class payment_adapter extends RecyclerView.Adapter<payment_adapter.ViewHolder> {

    private List<payment_model> listItems;
    private Context context;
    private AlertDialog progressDialog;
    public String st_p_mode,st_text,st_rem="0";
    private DecimalFormat decimalFormat = new DecimalFormat("#");

    public payment_adapter(List<payment_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void filterList(ArrayList<payment_model> filterlist) {

        listItems = filterlist;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id, flat, name, month, rent, unit, total;
        public CardView card_view;
        public ImageView p_status;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            flat = itemView.findViewById(R.id.tv_flat_no);
            name = itemView.findViewById(R.id.tv_name);
            month = itemView.findViewById(R.id.tv_month);
            rent = itemView.findViewById(R.id.tv_rent);
            unit = itemView.findViewById(R.id.m_reading);
            total = itemView.findViewById(R.id.tv_total);
            p_status = itemView.findViewById(R.id.i_p_status);

            card_view = itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_list, parent, false);
        return new payment_adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(payment_adapter.ViewHolder holder, final int position) {
        final payment_model listItem = listItems.get(position);
        holder.id.setText(listItem.getId());
        holder.flat.setText("F-"+listItem.getFlat());
        holder.name.setText(listItem.getName());
        holder.month.setText(listItem.getMonth());
        holder.rent.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getRent()+"/-</font></b>"));
        holder.unit.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getReading()+"/-</font></b>"));

        String htmlText = "<b><font color=#000000><b>&#8377; " + listItem.getTotal() + "/-</font></b><b><font color=#990000> (<b>&#8377; " + listItem.getPaid() + "/-)</font></b>";
        holder.total.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));

        if (listItem.getPStatus().equals("Pending")){
            holder.p_status.setImageResource(R.drawable.tic_p);
        }else{
            holder.p_status.setImageResource(R.drawable.tic_d);
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"Pay Rent:","Share Total Bill:","Share Reminder:"};
                builder.setTitle("F-" + listItem.getFlat() + ":- " + listItem.getName());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                if(listItem.getRem().equals("0")){
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                    builderDel.setMessage("Payment already received:");
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface1, int i) {
                                            dialogInterface1.dismiss();
                                        }
                                    });
                                    builderDel.create().show();
                                    return;
                                }

                                LayoutInflater inflater = LayoutInflater.from(view.getContext());
                                View dialogView = inflater.inflate(R.layout.alert_pay_rent, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                                builder.setView(dialogView);
                                AlertDialog alertDialog12 = builder.create();
                                alertDialog12.show();
                                alertDialog12.setCancelable(false);

                                TextView a_flat_no = dialogView.findViewById(R.id.tv_a_flat_no);
                                TextView a_name = dialogView.findViewById(R.id.tv_a_name);
                                TextView a_tr = dialogView.findViewById(R.id.tv_a_tr);
                                TextView a_pr = dialogView.findViewById(R.id.tv_a_pr);
                                TextView a_rr = dialogView.findViewById(R.id.tv_a_rr);
                                TextView a_month = dialogView.findViewById(R.id.tv_month);
                                Spinner spin_payment=dialogView.findViewById(R.id.spin_a_payment);
                                Button a_cancel=dialogView.findViewById(R.id.b_a_cancel);
                                Button a_update=dialogView.findViewById(R.id.b_a_update);
                                EditText a_rent=dialogView.findViewById(R.id.ed_a_rent);
                                EditText a_date = dialogView.findViewById(R.id.ed_a_date);

                                a_flat_no.setText("F-"+listItem.getFlat());
                                a_name.setText(listItem.getName());
                                a_tr.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getTotal()+"/-</font></b>"));
                                a_pr.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getPaid()+"/-</font></b>"));
                                a_rr.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getRem()+"/-</font></b>"));
                                a_month.setText(listItem.getMonth());

                                String[] paymentModes = {"Select Mode","Cash","Online"};

                                ArrayAdapter<String> pay_adapter = new ArrayAdapter<>(view.getContext(), R.layout.spinner_item, paymentModes);
                                pay_adapter.setDropDownViewResource(R.layout.spinner_item);
                                spin_payment.setAdapter(pay_adapter);
                                spin_payment.setPopupBackgroundResource(android.R.color.white);

                                spin_payment.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                    @Override
                                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                        st_p_mode = parent.getSelectedItem().toString();
                                    }
                                    @Override
                                    public void onNothingSelected(AdapterView<?> parent) {
                                    }
                                });

                                a_date.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        Calendar calendar = Calendar.getInstance();
                                        int year = calendar.get(Calendar.YEAR);
                                        int month = calendar.get(Calendar.MONTH);
                                        int day = calendar.get(Calendar.DAY_OF_MONTH);

                                        DatePickerDialog datePickerDialog = new DatePickerDialog(view.getContext(), new DatePickerDialog.OnDateSetListener() {
                                            @Override
                                            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                                                Calendar selectedDate = Calendar.getInstance();
                                                selectedDate.set(selectedYear, selectedMonth, selectedDay);
                                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                                String formattedDate = dateFormat.format(selectedDate.getTime());
                                                a_date.setText(formattedDate);
                                            }
                                        }, year, month, day);
                                        datePickerDialog.show();
                                    }
                                });

                                a_cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog12.dismiss();
                                        payments.pm.fetchPayments("");
                                    }
                                });

                                a_update.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {

                                        progressDialog = new AlertDialog.Builder(view.getContext())
                                                .setMessage("Please wait...")
                                                .setCancelable(false)
                                                .create();

                                        String st1_a_rent=a_rent.getText().toString().trim();
                                        String st1_a_date=a_date.getText().toString().trim();

                                        if (st1_a_rent.isEmpty()){
                                            a_rent.setError("Please rent amount");
                                        }else if (st1_a_date.isEmpty()){
                                            a_date.setError("Please select date");
                                        }else if (st_p_mode.equals("Select Mode")){
                                            Toast.makeText(context, "Please select mode ", Toast.LENGTH_SHORT).show();
                                        }else{
                                            Double d_rr = Double.parseDouble(listItem.getRem().replaceAll(",", ""));
                                            Double d_new = Double.parseDouble(st1_a_rent);

                                            if (d_new <= d_rr) {
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                                builderDel.setTitle("Rent Paying: "+listItem.getName());
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

                                                        double epsilon = 1e-6;
                                                        if (Math.abs(d_new - d_rr) < epsilon) {
                                                            st_text = "R";
                                                        } else {
                                                            st_text = "P";
                                                        }

                                                        String[] parts = listItem.getMonth().split("-");
                                                        String month = parts[0];
                                                        String year = parts[1];

                                                        Date o_Date = null;
                                                        Date n_date = null;

                                                        SimpleDateFormat inputFormat1 = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                                        SimpleDateFormat monthYearFormat = new SimpleDateFormat("MMMM-yyyy", Locale.getDefault());

                                                        try {
                                                            String monthYearString = month + "-" + year;
                                                            o_Date = monthYearFormat.parse(monthYearString);

                                                            n_date = inputFormat1.parse(st1_a_date);
                                                        } catch (ParseException e) {
                                                            e.printStackTrace();
                                                        }

                                                        Calendar calendar1 = Calendar.getInstance();
                                                        calendar1.setTime(o_Date);
                                                        int month_oDate = calendar1.get(Calendar.MONTH);
                                                        int year_oDate = calendar1.get(Calendar.YEAR);

                                                        Calendar calendar2 = Calendar.getInstance();
                                                        calendar2.setTime(n_date);
                                                        int month_nDate = calendar2.get(Calendar.MONTH);
                                                        int year_nDate = calendar2.get(Calendar.YEAR);

                                                        if (year_nDate > year_oDate || (year_nDate == year_oDate && month_nDate > month_oDate)) {

                                                            st_rem=listItem.getRem();

                                                            progressDialog.show();
                                                            StringRequest stringRequest = new StringRequest(
                                                                    Request.Method.POST,
                                                                    Constants.URL_UPDATE_RENT_PAYMENT,
                                                                    new Response.Listener<String>() {
                                                                        @Override
                                                                        public void onResponse(String response) {
                                                                            progressDialog.dismiss();
                                                                            try {
                                                                                JSONObject obj = new JSONObject(response);
                                                                                if (!obj.getBoolean("error")) {
                                                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                                                                    builderDel.setTitle("F-"+listItem.getFlat());
                                                                                    builderDel.setMessage(obj.getString("message"));
                                                                                    builderDel.setCancelable(false);
                                                                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                                                            dialogInterface.dismiss();
                                                                                            alertDialog12.dismiss();
                                                                                            payments.pm.fetchPayments("P");

                                                                                            sendWhatsapp(listItem.getMonth(),listItem.getFlat(),listItem.getName(),
                                                                                            listItem.getTotal(),IND_money_format(decimalFormat.format(d_new)),listItem.getPaid(),st1_a_date,st_p_mode,listItem.getMob());
                                                                                        }
                                                                                    });
                                                                                    builderDel.create().show();

                                                                                } else {
                                                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
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
                                                                            AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
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
                                                                    params.put("id", listItem.getId());
                                                                    params.put("c_id", listItem.getCId());
                                                                    params.put("rent", decimalFormat.format(d_new));
                                                                    params.put("p_date", formattedDate);
                                                                    params.put("p_mode", st_p_mode);
                                                                    params.put("p_type", st_text);
                                                                    return params;
                                                                }
                                                            };
                                                            RequestQueue requestQueue = Volley.newRequestQueue(view.getContext());
                                                            requestQueue.add(stringRequest);

                                                            requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
                                                                @Override
                                                                public void onRequestFinished(Request<Object> request) {
                                                                    requestQueue.getCache().clear();
                                                                }
                                                            });


                                                        } else {
                                                            AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                                            builderDel.setMessage("Payment date should be greater then Rent month:");
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
                                                });

                                                builderDel.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builderDel.create().show();
                                            } else {
                                                Toast.makeText(context,  "Invalid Amount", Toast.LENGTH_SHORT).show();
                                            }
                                        }

                                    }
                                });
                                break;
                            case 1:
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                builder1.setTitle("WhatsApp Share");
                                builder1.setMessage("Are you sure, You want to Share?");
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        sendWhatsapp_Case_Total_Bill(listItem.getFlat(),listItem.getMonth());
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

                                case 2:
                                    if(listItem.getRem().equals("0")){
                                        AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                        builderDel.setMessage("Rent already received:");
                                        builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface1, int i) {
                                                dialogInterface1.dismiss();
                                            }
                                        });
                                        builderDel.create().show();
                                        return;
                                    }

                                    AlertDialog.Builder builder2 = new AlertDialog.Builder(view.getContext());
                                    builder2.setTitle("WhatsApp Share");
                                    builder2.setMessage("Are you sure, You want to Share?");
                                    builder2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();

                                            String depositReminder = "Rent Reminder for Flat No: F-" + listItem.getFlat() + "\n\n" +
                                                    "Name: " + listItem.getName() + "\n" +
                                                    "Month: " + listItem.getMonth() + "\n" +
                                                    "Outstanding Rent: " + Html.fromHtml("<b><font color=#000000><b>&#8377; " + listItem.getTotal() + "/-</font></b>") + "\n" ;

                                            String phoneNumber="+91"+listItem.getMob();
//                                            String phoneNumber = "+919764751241";

                                            String encodedMessage1 = Uri.encode(depositReminder);
                                            String whatsappUrl = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage1 + "&type=phone_number&app_absent=0";

                                            Intent openWhatsApp = new Intent(Intent.ACTION_VIEW);
                                            openWhatsApp.setData(Uri.parse(whatsappUrl));
                                            context.startActivity(openWhatsApp);
                                        }
                                    });
                                    builder2.setNegativeButton("no", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    AlertDialog dialog1 = builder2.create();
                                    dialog1.show();
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

    public void sendWhatsapp(String st_month,String st_flat,String st_name, String st_total, String st_paid,String db_paid,String st_date, String st_mode, String st_mob){

        BigInteger rem=new BigInteger(st_rem.replaceAll(",", ""));
        BigInteger p_rem = new BigInteger(st_paid.replaceAll(",", ""));

        String message = "Payment Received for : "+ st_month +"\n\n" +
                "Flat No: F-" + st_flat + "\n" +
                "Name : " +  st_name + "\n" +
                "Total Bill : " +Html.fromHtml("<b><font color=#000000><b>&#8377; " +st_total+"/-</font></b>") + "\n" +
                "Paid Bill : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +st_paid+"/-</font></b>") + "\n" +
                "Remaining Bill : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +IND_money_format(String.valueOf(rem.subtract(p_rem)))+"/-</font></b>") + "\n" +
                "Payment Date : " + st_date + "\n"+
                "Payment Mode : " + st_mode + "\n";

//        String phoneNumber="+91"+st_mob;
        String phoneNumber="+919764751241";

        String encodedMessage = Uri.encode(message);
        String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
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

    public void sendWhatsapp_Case_Total_Bill(String st_flat,String st_month){

        progressDialog = new AlertDialog.Builder(context)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                Constants.URL_FETCH_T_PAYMENT_BY_FLAT,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject obj = new JSONObject(response);
                            if (!obj.getBoolean("error")) {

                                String message = "Payment Details for : " + st_month + "\n\n" +
                                        "Flat No: F-" + st_flat + "\n" +
                                        "Name : " + obj.getString("name") + "\n" +
                                        "Rent : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("rent") + "/-</font></b>") + "\n" +
                                        "Current Reading : " +  obj.getString("c_r")  + "\n" +
                                        "Previous Reading : " +   obj.getString("p_r")  + "\n" +
                                        "Unit : " +  obj.getString("unit") + "\n" +
                                        "Electricity Bill : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("reading") + "/-</font></b>") + "\n" +
                                        "--------------------------------------\n" + // Added separator line
                                        "Total Bill : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " + obj.getString("total") + "/-</font></b>") + "\n";

//                                String phoneNumber="+91"+obj.getString("mob");
                                String phoneNumber="+919764751241";

                                String encodedMessage = Uri.encode(message);
                                String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setData(Uri.parse(url));
                                context.startActivity(intent);
                            } else {
                                Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
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
                        AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
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
                params.put("flat", st_flat);
                params.put("month", st_month);
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
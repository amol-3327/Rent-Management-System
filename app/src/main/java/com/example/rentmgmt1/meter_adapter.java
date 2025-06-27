package com.example.rentmgmt1;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class meter_adapter extends RecyclerView.Adapter<meter_adapter.ViewHolder> {

    private List<meter_model> listItems;
    private Context context;
    private AlertDialog progressDialog;
    private String selectedMonth="null",selectedYear="null",selectedDays="null",nextMonthStr="hi";

    public meter_adapter(List<meter_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void filterList(ArrayList<meter_model> filterlist) {
        listItems = filterlist;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id,flat_no,c_r,p_r,unit,f_status;
        public CardView card_view;
        public ImageView month_status;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            flat_no =  itemView.findViewById(R.id.tv_flat_no);
            c_r = itemView.findViewById(R.id.tv_cr);
            p_r =  itemView.findViewById(R.id.tv_pr);
            unit =  itemView.findViewById(R.id.tv_unit);
            f_status =  itemView.findViewById(R.id.tv_m_status);
            month_status =  itemView.findViewById(R.id.iv_m_status);
            card_view =  itemView.findViewById(R.id.card_view);

            progressDialog = new AlertDialog.Builder(context)
                    .setMessage("Please wait...")
                    .setCancelable(false)
                    .create();
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.meter_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final meter_model listItem = listItems.get(position);
        holder.id.setText(listItem.getId());
        holder.flat_no.setText("F-"+listItem.getFlat_no());
        holder.c_r.setText(listItem.getC_r());
        holder.p_r.setText(listItem.getP_r());
        holder.unit.setText(listItem.getUnit());
        holder.f_status.setText(listItem.getF_status());

        if (listItem.getF_status().equals("Occupied")) {
            holder.f_status.setTextColor(Color.parseColor("#3700B3"));
        } else if (listItem.getF_status().equals("Available")) {
            holder.f_status.setTextColor(Color.parseColor("#FF0000"));
        } else if (listItem.getF_status().equals("Booked")) {
            holder.f_status.setTextColor(Color.parseColor("#ffff8800"));
        }

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);

        String currentMonth = monthFormat.format(calendar.getTime());

        if (listItem.getN_month().equals(currentMonth)){
            holder.month_status.setImageResource(R.drawable.tic_d);
        }else{
            holder.month_status.setImageResource(R.drawable.tic_p);
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"Update Reading"};
                builder.setTitle("F-"+listItem.getFlat_no());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                LayoutInflater inflater = LayoutInflater.from(context);
                                View dialogView = inflater.inflate(R.layout.alert_update_reading, null);
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setView(dialogView);
                                AlertDialog alertDialog12 = builder.create();
                                alertDialog12.show();
                                alertDialog12.setCancelable(false);

                                EditText a_c_read = dialogView.findViewById(R.id.ed_c_r);
                                TextView a_flat = dialogView.findViewById(R.id.tv_flat_no);
                                TextView a_p_read = dialogView.findViewById(R.id.tv_p_read);
                                Button cancel= dialogView.findViewById(R.id.b_cancel);
                                Button update= dialogView.findViewById(R.id.b_update);
                                LinearLayout l_s_dates= dialogView.findViewById(R.id.ll_s_date);

                                if(listItem.getF_status().equals("Occupied")){
                                    l_s_dates.setVisibility(VISIBLE);
                                }else {
                                    l_s_dates.setVisibility(GONE);
                                }

                                a_flat.setText("F-"+listItem.getFlat_no());
                                a_p_read.setText(listItem.getC_r());

                                Spinner spinnerMonth = dialogView.findViewById(R.id.spin_month);
                                String[] months = {"Select Month:","January", "February", "March", "April", "May", "June",
                                        "July", "August", "September", "October", "November", "December"};

                                ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, months);
                                monthAdapter.setDropDownViewResource(R.layout.spinner_item);
                                spinnerMonth.setAdapter(monthAdapter);
                                spinnerMonth.setPopupBackgroundResource(android.R.color.white);

                                Spinner spinnerYear = dialogView.findViewById(R.id.spin_year);
                                List<String> years = generateYearList();

                                ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(context, R.layout.spinner_item, years);
                                yearAdapter.setDropDownViewResource(R.layout.spinner_item);
                                spinnerYear.setAdapter(yearAdapter);
                                spinnerYear.setPopupBackgroundResource(android.R.color.white);

                                Calendar calendar = Calendar.getInstance();
                                int currentMonthIndex = calendar.get(Calendar.MONTH);
                                int previousMonthIndex = currentMonthIndex;
                                spinnerMonth.setSelection(previousMonthIndex);
                                int currentYear = calendar.get(Calendar.YEAR);
                                int currentYearPosition = years.indexOf(String.valueOf(currentYear));

                                if (currentYearPosition != -1) {
                                    spinnerYear.setSelection(currentYearPosition);
                                }

                                Spinner daysSpinner = dialogView.findViewById(R.id.spin_days);
                                String[] days = {"Select Days","10", "15", "20", "25", "30"};
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(context, R.layout.spinner_item, days);
                                adapter.setDropDownViewResource(R.layout.spinner_item);
                                daysSpinner.setAdapter(adapter);
                                daysSpinner.setPopupBackgroundResource(android.R.color.white);

                                int defaultPosition = adapter.getPosition("30");
                                daysSpinner.setSelection(defaultPosition);

                                cancel.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        alertDialog12.dismiss();
                                        meters.ms.checkServerAvail();
                                    }
                                });

                                update.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        String st_c_r=a_c_read.getText().toString().trim();

                                        selectedMonth = spinnerMonth.getSelectedItem().toString();
                                        selectedYear = spinnerYear.getSelectedItem().toString();
                                        selectedDays = daysSpinner.getSelectedItem().toString();

                                        try {
                                            try {
                                                Calendar calendar = Calendar.getInstance();
                                                SimpleDateFormat monthFormat = new SimpleDateFormat("MMMM", Locale.ENGLISH);
                                                calendar.set(Calendar.MONTH, monthFormat.parse(selectedMonth).getMonth());
                                                calendar.add(Calendar.MONTH, 1);
                                                nextMonthStr = monthFormat.format(calendar.getTime());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }

                                        if (st_c_r.isEmpty()){
                                            a_c_read.setError("Please enter reading");
                                        }else{

                                            if(listItem.getF_status().equals("Occupied")){
                                                if (selectedMonth.equals("Select Month:") || selectedYear.equals("Select Year:")) {
                                                    Toast.makeText(context, "Please select both month and year.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }else if (selectedDays.equals("Select Days")) {
                                                    Toast.makeText(context, "Please select rent days.", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }else{}
                                            }

                                            BigInteger rate = new BigInteger(listItem.getRate());
                                            BigInteger rent = new BigInteger(listItem.getRent());

                                            BigInteger s_days = BigInteger.ZERO;

                                            if (!selectedDays.equals("Select Days"))
                                            {
                                                s_days = new BigInteger(selectedDays);
                                            }

                                            BigInteger b_c_r = new BigInteger(st_c_r);
                                            BigInteger b_p_r = new BigInteger(listItem.getC_r());

                                            if (b_c_r.compareTo(b_p_r) < 0) {
                                                AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
                                                builderDel.setMessage("Current reading should be greater then previous reading");
                                                builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                builderDel.create().show();
                                            }else{
                                                BigInteger b_unit = b_c_r.subtract(b_p_r);
                                                BigInteger m_bill = b_unit.multiply(rate);

                                                BigInteger daily = rent.divide(BigInteger.valueOf(30));

                                                BigInteger monthly;

                                                if(selectedDays.equals("30")){
                                                    monthly =  rent;
                                                }
                                                else{
                                                    monthly =  daily.multiply(s_days);
                                                }
                                                BigInteger total_bill = m_bill.add(monthly);

                                                if (listItem.getFlat_no().equals("303") || listItem.getFlat_no().equals("304")) {
                                                    updateMeterForFlat_303_304(String.valueOf(b_c_r),String.valueOf(b_p_r),String.valueOf(b_unit),listItem.getFlat_no()
                                                            ,String.valueOf(m_bill),selectedMonth + "-" + selectedYear,String.valueOf(monthly),String.valueOf(total_bill),
                                                            nextMonthStr,alertDialog12);
                                                }else {
                                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
                                                    builderDel.setTitle("Update Booking: F-" + listItem.getFlat_no());
                                                    builderDel.setMessage("Are you sure, You want to Update ?");
                                                    builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.dismiss();

                                                            progressDialog.show();
                                                            StringRequest stringRequest = new StringRequest(
                                                                    Request.Method.POST,
                                                                    Constants.URL_UPDATE_M_READING,
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
                                                                                            meters.ms.checkServerAvail();

                                                                                            sendWhatsapp(listItem.getFlat_no(), selectedMonth + "-" + selectedYear);
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
                                                                            } catch (
                                                                                    JSONException e) {
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
                                                                    params.put("c_r", String.valueOf(b_c_r));
                                                                    params.put("p_r", String.valueOf(b_p_r));
                                                                    params.put("unit", String.valueOf(b_unit));
                                                                    params.put("flat", listItem.getFlat_no());
                                                                    params.put("m_bill", String.valueOf(m_bill));
                                                                    params.put("month_year", selectedMonth + "-" + selectedYear);
                                                                    params.put("rent", String.valueOf(monthly));
                                                                    params.put("total_bill", String.valueOf(total_bill));
                                                                    params.put("n_month", nextMonthStr);
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

                                        }
                                    }
                                });
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

    private List<String> generateYearList() {
        List<String> yearList = new ArrayList<>();
        int startYear = 2024;
        yearList.add("Select Year:");
        int endYear = Calendar.getInstance().get(Calendar.YEAR) + 20;
        for (int i = startYear; i <= endYear; i++) {
            yearList.add(String.valueOf(i));
        }
        return yearList;
    }

    public void sendWhatsapp(String st_flat,String st_month){

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
                                        "Units : " +  obj.getString("unit") + "\n" +
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

    public void updateMeterForFlat_303_304(String c_r, String p_r, String unit, String flat,
                                           String m_bill, String month_year, String rent,
                                           String total_bill, String n_month,AlertDialog alertDialog123) {

        AlertDialog.Builder builderDel = new AlertDialog.Builder(context);
        builderDel.setTitle("Update Booking: F-" + flat);
        builderDel.setMessage("Are you sure, You want to Update ?");
        builderDel.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();

                progressDialog.show();
                StringRequest stringRequest = new StringRequest(
                        Request.Method.POST,
                        Constants.URL_UPDATE_M_READING_303_304,
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
                                                meters.ms.checkServerAvail();
                                                alertDialog123.dismiss();
                                                sendWhatsapp(flat, selectedMonth + "-" + selectedYear);
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
                                } catch (
                                        JSONException e) {
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
                        params.put("c_r", c_r);
                        params.put("p_r", p_r);
                        params.put("unit", unit);
                        params.put("flat", flat);
                        params.put("m_bill", m_bill);
                        params.put("month_year", month_year);
                        params.put("rent", rent);
                        params.put("total_bill", total_bill);
                        params.put("n_month", n_month);
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

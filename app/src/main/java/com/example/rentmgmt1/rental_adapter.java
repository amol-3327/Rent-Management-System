package com.example.rentmgmt1;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Html;
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

public class rental_adapter extends RecyclerView.Adapter<rental_adapter.ViewHolder> {

    private List<rental_model> listItems;
    private Context context;
    private AlertDialog progressDialog;

    public rental_adapter(List<rental_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void filterList(ArrayList<rental_model> filterlist) {

        listItems = filterlist;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id, flat, name, rent_out, depo_out,l_date,mob;
        public CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            flat = itemView.findViewById(R.id.tv_flat_no);
            name = itemView.findViewById(R.id.tv_name);
            rent_out = itemView.findViewById(R.id.tv_r_out);
            depo_out = itemView.findViewById(R.id.tv_d_out);
            l_date = itemView.findViewById(R.id.tv_l_date);
            mob = itemView.findViewById(R.id.tv_mob);

            card_view = itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_list, parent, false);
        return new rental_adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(rental_adapter.ViewHolder holder, final int position) {
        final rental_model listItem = listItems.get(position);
        holder.id.setText(listItem.getId());
        holder.flat.setText("F-"+listItem.getFlat());
        holder.name.setText(listItem.getName());
        holder.rent_out.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getRent_out()+"/-</font></b>"));

        String htmlText = "<b><font color=#000000><b>&#8377; " + listItem.getDepo_out() + "/-</font></b><b><font color=#008000> (<b>&#8377; " + listItem.getD_paid() + "/-)</font></b>";
        holder.depo_out.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
        holder.l_date.setText(listItem.getL_date());
        holder.mob.setText(listItem.getMob());

        if (!listItem.getL_date().equals("NA")) {
            holder.l_date.setTextColor(Color.RED);
        }else{
            holder.l_date.setTextColor(Color.BLACK);
        }

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View Full Details:","Pay Deposit:","Share Deposit Reminder:","Pay Rent:","Share Rent Reminder:","Call the Person: "};
                builder.setTitle("F-" + listItem.getFlat() + ":- " + listItem.getName());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(view.getContext(), flat_details.class);
                                intent.putExtra("flat", listItem.getFlat());
                                view.getContext().startActivity(intent);
                                break;
                            case 1:
                                if(listItem.getDepo_out().equals("0")){
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                    builderDel.setMessage("Deposit already received:");
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface1, int i) {
                                            dialogInterface1.dismiss();
                                        }
                                    });
                                    builderDel.create().show();
                                    return;
                                }
                                Intent intent2 = new Intent(view.getContext(), flat_details.class);
                                intent2.putExtra("flat", listItem.getFlat());
                                view.getContext().startActivity(intent2);
                                break;

                            case 2:
                                if(listItem.getDepo_out().equals("0")){
                                    AlertDialog.Builder builderDel = new AlertDialog.Builder(view.getContext());
                                    builderDel.setMessage("Deposit already received:");
                                    builderDel.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface1, int i) {
                                            dialogInterface1.dismiss();
                                        }
                                    });
                                    builderDel.create().show();
                                    return;
                                }
                                AlertDialog.Builder builder1 = new AlertDialog.Builder(view.getContext());
                                builder1.setTitle("WhatsApp Share:");
                                builder1.setMessage("Are you sure, You want to Share?");
                                builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        String message = "Deposit Reminder for Flat No: F-"+ listItem.getFlat() +"\n\n" +
                                                "Name : " +  listItem.getName() + "\n" +
                                                "Total Deposit : " +Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getT_depo()+"/-</font></b>") + "\n" +
                                                "Paid Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getD_paid()+"/-</font></b>") + "\n" +
                                                "Remaining Deposit : " + Html.fromHtml("<b><font color=#000000><b>&#8377; " +listItem.getDepo_out()+"/-</font></b>") + "\n";

//                                        String phoneNumber="+91"+listItem.getMob();
                                        String phoneNumber="+919764751241";

                                        String encodedMessage = Uri.encode(message);
                                        String url = "https://api.whatsapp.com/send/?phone=" + phoneNumber + "&text=" + encodedMessage + "&type=phone_number&app_absent=0";

                                        Intent intent1 = new Intent(Intent.ACTION_VIEW);
                                        intent1.setData(Uri.parse(url));
                                        context.startActivity(intent1);
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

                            case 3:

                                if(listItem.getRent_out().equals("0")){
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

                                Intent intent3 = new Intent(view.getContext(), payments.class);
                                view.getContext().startActivity(intent3);
                                break;

                            case 4:
                                if(listItem.getRent_out().equals("0")){
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
                                                "Outstanding Rent: " + Html.fromHtml("<b><font color=#000000><b>&#8377; " + listItem.getRent_out() + "/-</font></b>") + "\n" ;

//                                        String phoneNumber="+91"+listItem.getMob();
                                        String phoneNumber = "+919764751241";

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
                            case 5:
                                AlertDialog.Builder builder12 = new AlertDialog.Builder(view.getContext());
                                builder12.setTitle("Call the person:");
                                builder12.setMessage("Are you sure, You want to Call?");
                                builder12.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();

                                        String phoneNumber = listItem.getMob();
                                        Intent intent12 = new Intent(Intent.ACTION_DIAL);
                                        intent12.setData(Uri.parse("tel:" + phoneNumber));
                                        context.startActivity(intent12);
                                    }
                                });
                                builder12.setNegativeButton("no", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                AlertDialog dialog12 = builder12.create();
                                dialog12.show();
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
}

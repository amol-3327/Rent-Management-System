package com.example.rentmgmt1;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class rent_h_adapter extends RecyclerView.Adapter<rent_h_adapter.ViewHolder> {

    private final List<Map<String, String>> listItems;
    private final Context context;

    public rent_h_adapter(List<Map<String, String>> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.rent_h_list, parent, false);
        return new ViewHolder(view);
    }

    public void filterList(List<Map<String, String>> filterlist) {
        if (filterlist == null || filterlist.isEmpty()) {
            listItems.clear();
            listItems.addAll(listItems); // Restore full list
        } else {
            listItems.clear();
            listItems.addAll(filterlist);
        }
        notifyDataSetChanged(); // Refresh RecyclerView
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> data = listItems.get(position);

        holder.month.setText( data.get("month"));

        holder.rent.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + data.get("rent") + "/-</font></b>"));
        holder.unit.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + data.get("reading") + "/-</font></b>"));
        String htmlText = "<b><font color=#000000><b>&#8377; " + data.get("total") + "/-</font></b><b><font color=#990000> (<b>&#8377; " + data.get("rem") + "/-)</font></b>";
        holder.total.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
        holder.p_date.setText(Html.fromHtml("<b><font color=#000000><b> " + data.get("p_date") + "</font></b>"));

        if (data.get("status").equals("Pending")) {
            holder.p_status.setImageResource(R.drawable.tic_p);
        } else {
            holder.p_status.setImageResource(R.drawable.tic_d);
        }
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView month, rent, unit, total,p_date;
        CardView card_view;
        ImageView p_status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            month = itemView.findViewById(R.id.tv_month);
            rent = itemView.findViewById(R.id.tv_rent);
            unit = itemView.findViewById(R.id.m_reading);
            total = itemView.findViewById(R.id.tv_total);
            p_date = itemView.findViewById(R.id.tv_p_date);
            p_status = itemView.findViewById(R.id.i_p_status);
            card_view = itemView.findViewById(R.id.card_view);
        }
    }
}
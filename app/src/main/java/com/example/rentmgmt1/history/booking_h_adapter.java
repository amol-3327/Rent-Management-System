package com.example.rentmgmt1.history;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import com.example.rentmgmt1.R;
import com.example.rentmgmt1.book_model;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class booking_h_adapter extends RecyclerView.Adapter<com.example.rentmgmt1.history.booking_h_adapter.ViewHolder> {

    private final List<Map<String, String>> listItems;
    private final Context context;
    public AlertDialog progressDialog;

    public booking_h_adapter(List<Map<String, String>> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public com.example.rentmgmt1.history.booking_h_adapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.booking_h_list, parent, false);
        return new com.example.rentmgmt1.history.booking_h_adapter.ViewHolder(view);
    }

    public void filterList(List<Map<String, String>> filterlist) {
        if (filterlist == null || filterlist.isEmpty()) {
            listItems.clear();
            listItems.addAll(listItems);
        } else {
            listItems.clear();
            listItems.addAll(filterlist);
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(@NonNull com.example.rentmgmt1.history.booking_h_adapter.ViewHolder holder, int position) {
        Map<String, String> data = listItems.get(position);

        holder.id.setText(data.get("id"));
        holder.name.setText(data.get("name"));
        holder.mob.setText(data.get("mob"));
        holder.deposit.setText(Html.fromHtml("<b>&#8377;" +data.get("deposit") + "/-</b>")+" ("+data.get("p_mode")+")");
        holder.town.setText(data.get("town"));
        holder.b_date.setText(data.get("b_date"));
        holder.s_date.setText(data.get("s_date"));
        holder.flat.setText(Html.fromHtml("<b><span style='color:#FF0000;'>&#127968; &nbsp;&nbsp;F-" + data.get("flat") + "</span></b>"));
        holder.cnt.setText(Html.fromHtml("<b><span style='color:#FF0000;'>&#128101;</span>&nbsp;&nbsp;" + data.get("cnt") + "</b>"));
        holder.c_date.setText(data.get("c_date"));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView id,name,mob,deposit,town,b_date,s_date,flat,cnt,c_date;
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
            c_date = itemView.findViewById(R.id.tv_c_date);
            card_view =  itemView.findViewById(R.id.b_card_view);
        }
    }
}

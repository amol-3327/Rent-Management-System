package com.example.rentmgmt1;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class deposit_h_adapter extends RecyclerView.Adapter<deposit_h_adapter.ViewHolder> {

    private List<Map<String, String>> listItems;
    private Context context;

    public deposit_h_adapter(List<Map<String, String>> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.deposit_h_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Map<String, String> data = listItems.get(position);
        holder.amo.setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + data.get("amo") + "/-</font></b>"));
        holder.mode.setText(data.get("mode"));
        holder.date.setText(data.get("date"));
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView amo,mode,date;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            amo = itemView.findViewById(R.id.tv_amo);
            mode = itemView.findViewById(R.id.tv_mode);
            date = itemView.findViewById(R.id.tv_date);

        }
    }
}


package com.example.rentmgmt1.history;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.rentmgmt1.R;
import com.example.rentmgmt1.flat_details;

import java.util.ArrayList;
import java.util.List;

public class rental_h_adapter extends RecyclerView.Adapter<rental_h_adapter.ViewHolder> {

    private List<rental_h_model> listItems;
    private Context context;

    public rental_h_adapter(List<rental_h_model> listItems, Context context) {
        this.listItems = listItems;
        this.context = context;
    }

    public void filterList(ArrayList<rental_h_model> filterlist) {

        listItems = filterlist;

        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView id, flat_no, name, mob, s_date,l_date,t_month;
        public CardView card_view;

        public ViewHolder(View itemView) {
            super(itemView);
            id = itemView.findViewById(R.id.tv_id);
            flat_no = itemView.findViewById(R.id.tv_flat_no);
            name = itemView.findViewById(R.id.tv_name);
            mob = itemView.findViewById(R.id.tv_mob);
            s_date = itemView.findViewById(R.id.tv_s_date);
            l_date = itemView.findViewById(R.id.tv_l_date);
            t_month = itemView.findViewById(R.id.tv_t_month);

            card_view = itemView.findViewById(R.id.card_view);
        }
    }

    @Override
    public rental_h_adapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rental_h_list, parent, false);
        return new rental_h_adapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(rental_h_adapter.ViewHolder holder, final int position) {
        final rental_h_model listItem = listItems.get(position);
        holder.id.setText(listItem.getId());
        holder.flat_no.setText("F-"+listItem.getFlat());
        holder.name.setText(listItem.getName());
        holder.mob.setText(listItem.getMob());
        holder.s_date.setText(listItem.getS_date());
        holder.l_date.setText(listItem.getL_date());
        holder.t_month.setText(listItem.getT_month());

        holder.card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                final CharSequence[] dialogitem = {"View Full Details:","Rent History:","Deposit History:"};
                builder.setTitle("F-" + listItem.getFlat() + ":- " + listItem.getName());
                builder.setItems(dialogitem, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(view.getContext(), rental_h_details.class);
                                intent.putExtra("flat", listItem.getFlat());
                                intent.putExtra("c_id", listItem.getC_id());
                                view.getContext().startActivity(intent);
                                break;
                            case 1:
                                Intent intent1 = new Intent(view.getContext(), rent_deposit_history.class);
                                intent1.putExtra("flat", listItem.getFlat());
                                intent1.putExtra("c_id", listItem.getC_id());
                                intent1.putExtra("txt", "r_h");
                                view.getContext().startActivity(intent1);
                                break;
                            case 2:
                                Intent intent3 = new Intent(view.getContext(), rent_deposit_history.class);
                                intent3.putExtra("flat", listItem.getFlat());
                                intent3.putExtra("c_id", listItem.getC_id());
                                intent3.putExtra("txt", "d_h");
                                view.getContext().startActivity(intent3);
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

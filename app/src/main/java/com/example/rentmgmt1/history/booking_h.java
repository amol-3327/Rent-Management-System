package com.example.rentmgmt1.history;

import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.rentmgmt1.Constants;
import com.example.rentmgmt1.R;
import com.example.rentmgmt1.book_adapter;
import com.example.rentmgmt1.book_model;
import com.example.rentmgmt1.bookings;
import com.example.rentmgmt1.flat_details;
import com.example.rentmgmt1.new_booking;
import com.example.rentmgmt1.rent_h_adapter;
import com.example.rentmgmt1.rent_history;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class booking_h extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog progressDialog;
    private booking_h_adapter adapter1;
    public List<Map<String, String>> listItems;
    public List<Map<String, String>> listItems_o;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_booking_h);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Toolbar toolbar = findViewById(R.id.b_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Cancelled Bookings:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new AlertDialog.Builder(booking_h.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        recyclerView = findViewById(R.id.list_booking);
        recyclerView.setLayoutManager(new LinearLayoutManager(booking_h.this));
        listItems = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.b_swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {

                fetch_booking_h();

                swipeRefreshLayout.setRefreshing(false);
            }, 200);
        });

        fetch_booking_h();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rentals_l, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setQueryHint("Flat/Mobile number...");

        searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                filter(query);
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                filter(newText);
                return false;
            }
        });
        return true;
    }

    public void filter(String text) {
        if (text.isEmpty()) {
            adapter1.filterList(listItems_o);
            return;
        }

        List<Map<String, String>> filteredList = new ArrayList<>();

        for (Map<String, String> item : listItems_o) {
            if ((item.get("flat") != null && item.get("flat").toLowerCase().contains(text.toLowerCase())) ||
                    (item.get("mob") != null && item.get("mob").toLowerCase().contains(text.toLowerCase()))) {
                filteredList.add(item);
            }
        }

        if (filteredList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(booking_h.this);
            builder.setMessage("Details Not Available...");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            adapter1.filterList(filteredList);
        }
    }


    public void fetch_booking_h() {

        listItems = new ArrayList<>();
        listItems_o = new ArrayList<>();
        listItems.clear();
        listItems_o.clear();

        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, H_Constants.URL_FETCH_BOOKING_H, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);

                            Map<String, String> map = new HashMap<>();
                            map.put("id", o.getString("id"));
                            map.put("name", o.getString("name"));
                            map.put("mob", o.getString("mob"));
                            map.put("deposit", o.getString("deposit"));
                            map.put("town", o.getString("town"));
                            map.put("b_date", o.getString("b_date"));
                            map.put("s_date", o.getString("s_date"));
                            map.put("flat", o.getString("flat"));
                            map.put("cnt", o.getString("cnt"));
                            map.put("p_mode", o.getString("p_mode"));
                            map.put("b_type", o.getString("b_type"));
                            map.put("c_date", o.getString("c_date"));

                            listItems.add(map);
                            listItems_o.add(map);
                        }
                        adapter = new booking_h_adapter(listItems, booking_h.this);
                        recyclerView.setAdapter(adapter);

                        adapter1 = new booking_h_adapter(listItems, booking_h.this);
                        recyclerView.setAdapter(adapter1);
                    } else {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(booking_h.this);
                        builder1.setTitle(jsonObject.getString("message"));
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_booking_h();
                            }
                        });
                        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        AlertDialog dialog = builder1.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alert));
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(booking_h.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_booking_h();
                            }
                        });
                        builder1.setNegativeButton("no", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                                finish();
                            }
                        });
                        AlertDialog dialog = builder1.create();
                        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
                            @Override
                            public void onShow(DialogInterface arg0) {
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.alert));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.alert));
                            }
                        });
                        dialog.show();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("b_h", "b_h");
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(booking_h.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}
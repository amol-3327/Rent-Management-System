package com.example.rentmgmt1.history;

import static android.view.View.VISIBLE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
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
import com.example.rentmgmt1.SharedPrefManager;
import com.example.rentmgmt1.admin_login;
import com.example.rentmgmt1.flat_details;
import com.example.rentmgmt1.rent_h_adapter;
import com.example.rentmgmt1.rent_history;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class rent_deposit_history extends AppCompatActivity {
    private String flat,c_id,txt;
    private AlertDialog progressDialog;
    private SwipeRefreshLayout r_swipeRefreshLayout,d_swipeRefreshLayout;
    private LinearLayout l_r_h,l_d_h;
    private LinearLayout r_customerListLayout,d_customerListLayout;
    private boolean showMenuItem = false;
    private List<View> allViews,allViews_1;
    private Toolbar r_toolbar,d_toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rent_deposit_history);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        if(!SharedPrefManager.getInstance(this).isLoggedIn()){
            finish();
            startActivity(new Intent(this, admin_login.class));
            return;
        }

        flat = getIntent().getStringExtra("flat");
        c_id = getIntent().getStringExtra("c_id");
        txt = getIntent().getStringExtra("txt");

        r_customerListLayout = findViewById(R.id.r_customerListLayout);
        d_customerListLayout = findViewById(R.id.d_customerListLayout);
        r_swipeRefreshLayout = findViewById(R.id.r_h_swipeRefreshLayout);
        d_swipeRefreshLayout = findViewById(R.id.d_h_swipeRefreshLayout);
        l_r_h = findViewById(R.id.ll_r_h);
        l_d_h = findViewById(R.id.ll_d_h);

        allViews = new ArrayList<>();
        allViews_1 = new ArrayList<>();

        progressDialog = new AlertDialog.Builder(rent_deposit_history.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        r_swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {

                if(txt.equals("r_h")){
                    fetch_r_history();
                }else{
                    fetch_d_history();
                }

                r_swipeRefreshLayout.setRefreshing(false);
            }, 200);
        });

        if(txt.equals("r_h")){
            fetch_r_history();
        }else{
            fetch_d_history();
        }
    }

    public void fetch_r_history(){
        showMenuItem = true;
        invalidateOptionsMenu();

        Toolbar toolbar = findViewById(R.id.r_h_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Rent History:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        r_customerListLayout.removeAllViews();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, H_Constants.URL_FETCH_RENT_HISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        l_r_h.setVisibility(VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);

                            View view = getLayoutInflater().inflate(R.layout.rent_h_list, r_customerListLayout, false);

                            ((TextView) view.findViewById(R.id.tv_month)).setText(o.getString("month"));
                            ((TextView) view.findViewById(R.id.tv_rent)).setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + o.getString("rent") + "/-</font></b>"));
                            ((TextView) view.findViewById(R.id.m_reading)).setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + o.getString("reading") + "/-</font></b>"));

                            String htmlText = "<b><font color=#000000><b>&#8377; " + o.getString("total") + "/-</font></b><b><font color=#990000> (<b>&#8377; " + o.getString("rem") + "/-)</font></b>";
                            ((TextView) view.findViewById(R.id.tv_total)).setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY));
                            ((TextView) view.findViewById(R.id.tv_p_date)).setText(Html.fromHtml("<b><font color=#000000><b> " + o.getString("p_date") + "</font></b>"));

                            ImageView p_status = view.findViewById(R.id.i_p_status);

                            if (o.getString("status").equals("Pending")) {
                                p_status.setImageResource(R.drawable.tic_p);
                            } else {
                                p_status.setImageResource(R.drawable.tic_d);
                            }
                            allViews.add(view);
                            r_customerListLayout.addView(view);
                        }

                    } else {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(rent_deposit_history.this);
                        builder1.setTitle(jsonObject.getString("message"));
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_r_history();
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(rent_deposit_history.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_r_history();
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
                params.put("flat", flat);
                params.put("c_id", c_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(rent_deposit_history.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_rentals_l, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        if (searchItem != null) {
            searchItem.setVisible(showMenuItem); // Toggle visibility dynamically

            if (showMenuItem) { // Only configure SearchView if it's visible
                androidx.appcompat.widget.SearchView searchView =
                        (androidx.appcompat.widget.SearchView) searchItem.getActionView();

                if (searchView != null) {
                    searchView.setQueryHint("Search Month...");
                    searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                        @Override
                        public boolean onQueryTextSubmit(String query) {
                            search(query); // Filter list by month
                            return false;
                        }
                        @Override
                        public boolean onQueryTextChange(String newText) {
                            search(newText); // Dynamically filter results
                            return false;
                        }
                    });
                }
            }
        }
        return true;
    }


    public void search(String query) {
        List<View> filteredList = new ArrayList<>();

        for (View view : allViews) {
            TextView textMonth = view.findViewById(R.id.tv_month);
            if (textMonth != null && textMonth.getText().toString().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(view);
            }
        }

        if (filteredList.isEmpty()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(rent_deposit_history.this);
            builder.setMessage("Details Not Available...");
            AlertDialog dialog = builder.create();
            dialog.show();
        } else {
            r_customerListLayout.removeAllViews();
            for (View view : filteredList) {
                r_customerListLayout.addView(view);
            }
        }
    }


    public void fetch_d_history() {
        showMenuItem = false;
        invalidateOptionsMenu();

        Toolbar toolbar = findViewById(R.id.d_h_toolbar);
        setSupportActionBar(toolbar);
        setTitle("Deposit History:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        d_customerListLayout.removeAllViews();

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, H_Constants.URL_FETCH_DEPOSIT_HISTORY, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        l_d_h.setVisibility(VISIBLE);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);

                            View view = getLayoutInflater().inflate(R.layout.deposit_h_list, d_customerListLayout, false);

                            ((TextView) view.findViewById(R.id.tv_amo)).setText(Html.fromHtml("<b><font color=#000000><b>&#8377; " + o.getString("amo") + "/-</font></b>"));
                            ((TextView) view.findViewById(R.id.tv_mode)).setText(o.getString("mode") );
                            ((TextView) view.findViewById(R.id.tv_date)).setText(o.getString("date") );

                            allViews_1.add(view);
                            d_customerListLayout.addView(view);
                        }

                    } else {
                        progressDialog.dismiss();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(rent_deposit_history.this);
                        builder1.setTitle(jsonObject.getString("message"));
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_d_history();
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(rent_deposit_history.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetch_r_history();
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
                params.put("flat", flat);
                params.put("c_id", c_id);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(rent_deposit_history.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }

}
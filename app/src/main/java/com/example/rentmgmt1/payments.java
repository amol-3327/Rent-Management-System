package com.example.rentmgmt1;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class payments extends AppCompatActivity {

    public static payments pm;
    List<payment_model> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter payment_adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog progressDialog;
    private payment_adapter adapter1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_payments);
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

        Toolbar toolbar = findViewById(R.id.p_toolbar);
        setSupportActionBar(toolbar);
        setTitle("All Payments:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new AlertDialog.Builder(payments.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        recyclerView = findViewById(R.id.list_payment);
        recyclerView.setLayoutManager(new LinearLayoutManager(payments.this));
        listItems = new ArrayList<>();

        swipeRefreshLayout = findViewById(R.id.p_swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {

                fetchPayments("P");

                swipeRefreshLayout.setRefreshing(false);
            }, 200);
        });

        pm=this;
        fetchPayments("P");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_payment_l, menu);
        getMenuInflater().inflate(R.menu.menu_payment_sort, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) searchItem.getActionView();
        searchView.setQueryHint("Flat No...");

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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.s_p:
                fetchPayments("P");
                return true;
            case R.id.s_r:
                fetchPayments("R");
                return true;
            case R.id.all:
                fetchPayments("");
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void filter(String text) {
        ArrayList<payment_model> filteredlist = new ArrayList<>();

        for (payment_model item : listItems) {
            if (item.getFlat().toLowerCase().contains(text.toLowerCase())) {
                filteredlist.add(item);
            }
        }

        if (filteredlist.isEmpty()) {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(payments.this);
            builder1.setMessage("Details Not Available...");
            AlertDialog dialog = builder1.create();
            dialog.show();
        } else {
            adapter1.filterList(filteredlist);
        }
    }

    public void fetchPayments(String st_text){
        listItems.clear();
        payment_adapter = new payment_adapter(listItems, getApplicationContext());
        recyclerView.setAdapter(payment_adapter);

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_FETCH_PAYMENT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (!jsonObject.getBoolean("error")) {
                        JSONArray jsonArray = jsonObject.getJSONArray("data");
                        Toast.makeText(payments.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject o = jsonArray.getJSONObject(i);
                            payment_model item = new  payment_model(
                                    o.getString("id"),
                                    o.getString("flat_no"),
                                    o.getString("name"),
                                    o.getString("mob"),
                                    o.getString("c_id"),
                                    o.getString("rent"),
                                    o.getString("reading"),
                                    o.getString("total"),
                                    o.getString("month"),
                                    o.getString("p_status"),
                                    o.getString("paid"),
                                    o.getString("remaining")
                            );
                            listItems.add(item);
                            payment_adapter = new  payment_adapter(listItems, getApplicationContext());
                            recyclerView.setAdapter( payment_adapter);

                            adapter1 = new payment_adapter(listItems, payments.this);
                            recyclerView.setAdapter(adapter1);
                        }
                    } else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(payments.this);
                        builder1.setTitle(jsonObject.getString("message"));
                        builder1.setMessage("Please refresh again...");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                fetchPayments(st_text);
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
                                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.black));
                                dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.black));
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
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(payments.this);
                        builder1.setTitle("Network Error");
                        builder1.setMessage("Please refresh again...");
                        builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
//                                fetchLeftRent();
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
                params.put("payments", "payments");
                params.put("text", st_text);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(payments.this);
        requestQueue.add(stringRequest);

        requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                requestQueue.getCache().clear();
            }
        });
    }
}
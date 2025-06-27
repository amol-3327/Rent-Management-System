package com.example.rentmgmt1;

import static android.view.View.GONE;
import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;
import static java.util.Locale.filter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class bookings extends AppCompatActivity {

    public static bookings book;
    List<book_model> listItems;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private AlertDialog progressDialog;
    private book_adapter adapter1;
    private CardView n_book;
    private LinearLayout l_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings);
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

        Toolbar toolbar = findViewById(R.id.b_toolbar);
        setSupportActionBar(toolbar);
        setTitle("All Bookings:");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new AlertDialog.Builder(bookings.this)
                .setMessage("Please wait...")
                .setCancelable(false)
                .create();

        recyclerView = findViewById(R.id.list_booking);
        recyclerView.setLayoutManager(new LinearLayoutManager(bookings.this));
        listItems = new ArrayList<>();

        n_book= findViewById(R.id.c_new_b);
        l_book= findViewById(R.id.l_book);
        swipeRefreshLayout = findViewById(R.id.b_swipeRefreshLayout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            new Handler().postDelayed(() -> {

                checkServerAvail();

                swipeRefreshLayout.setRefreshing(false);
            }, 200);
        });

        n_book.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), new_booking.class));
            }
        });

        book=this;
        checkServerAvail();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_book_l, menu);

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
            case R.id.action_add:
                startActivity(new Intent(this, new_booking.class));
                break;
        }
        return true;
    }


private void filter(String text) {
    ArrayList<book_model> filteredlist = new ArrayList<>();

    for (book_model item : listItems) {
        if (item.getFlat().toLowerCase().contains(text.toLowerCase())) {
            filteredlist.add(item);
        }
    }

    if (filteredlist.isEmpty()) {
        AlertDialog.Builder builder1 = new AlertDialog.Builder(bookings.this);
        builder1.setMessage("Details Not Available...");
        AlertDialog dialog = builder1.create();
        dialog.show();
    } else {
        adapter1.filterList(filteredlist);
    }
}
public void fetch_flats() {

    listItems.clear();
    adapter = new book_adapter(listItems, getApplicationContext());
    recyclerView.setAdapter(adapter);

    recyclerView.setItemAnimator(new DefaultItemAnimator());
    progressDialog.show();

    StringRequest stringRequest = new StringRequest(Request.Method.POST, Constants.URL_BOOKINGS, new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
            progressDialog.dismiss();
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (!jsonObject.getBoolean("error")) {
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    Toast.makeText(bookings.this, jsonObject.getString("message"), Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject o = jsonArray.getJSONObject(i);
                        book_model item = new book_model(
                                o.getString("id"),
                                o.getString("name"),
                                o.getString("mob"),
                                o.getString("deposit"),
                                o.getString("town"),
                                o.getString("b_date"),
                                o.getString("s_date"),
                                o.getString("flat"),
                                o.getString("cnt"),
                                o.getString("p_mode"),
                                o.getString("b_type"),
                                o.getString("t_depo")
                        );
                        listItems.add(item);
                        adapter = new book_adapter(listItems, getApplicationContext());
                        recyclerView.setAdapter(adapter);

                        adapter1 = new book_adapter(listItems, bookings.this);
                        recyclerView.setAdapter(adapter1);
                    }
                } else {
                    if (jsonObject.getString("message").equals("No Bookings Available..")){
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(bookings.this);
                        builder1.setTitle(jsonObject.getString("message"));
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                l_book.setVisibility(VISIBLE);
                            }
                        });
                        AlertDialog dialog = builder1.create();
                        dialog.show();
                    }else {
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(bookings.this);
                        builder1.setTitle(jsonObject.getString("message"));
                        builder1.setMessage("Please refresh again...");
                        builder1.setCancelable(false);
                        builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                        AlertDialog dialog = builder1.create();
                        dialog.show();

                    }
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
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(bookings.this);
                    builder1.setTitle("Network Error");
                    builder1.setMessage("Please refresh again...");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            fetch_flats();
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
            params.put("book", "book");
            return params;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(bookings.this);
    requestQueue.add(stringRequest);

    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
        @Override
        public void onRequestFinished(Request<Object> request) {
            requestQueue.getCache().clear();
        }
    });
}

public void checkServerAvail() {

    progressDialog.show();
    StringRequest stringRequest = new StringRequest(
            Request.Method.POST,
            Constants.URL_SERVER_STATUS,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (!obj.getBoolean("error")) {
                            fetch_flats();
                            l_book.setVisibility(GONE);

                        } else {
                            AlertDialog.Builder builder1 = new AlertDialog.Builder(bookings.this);
                            builder1.setTitle(obj.getString("message"));
                            builder1.setMessage("Please refresh again...");
                            builder1.setCancelable(false);
                            builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    checkServerAvail();
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
                    AlertDialog.Builder builder1 = new AlertDialog.Builder(bookings.this);
                    builder1.setTitle("Server Not Available");
                    builder1.setMessage("Please try again...");
                    builder1.setCancelable(false);
                    builder1.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            checkServerAvail();
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
            params.put("checkStatus", "checkServStatus");
            return params;
        }
    };
    RequestQueue requestQueue = Volley.newRequestQueue(bookings.this);
    requestQueue.add(stringRequest);

    requestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
        @Override
        public void onRequestFinished(Request<Object> request) {
            requestQueue.getCache().clear();
        }
    });
}

}
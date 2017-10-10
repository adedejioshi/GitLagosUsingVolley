package com.example.gsa.gitlagosusingvolley;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.gsa.gitlagosusingvolley.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    // variable declarations
    private ProgressDialog pDialog;
    private EmptyRecyclerView recyclerView;
    private List<DeveloperList> developerList;
    private  DevelopersAdapter mAdapter;
    private TextView emptyView;
    private SwipeRefreshLayout refreshLayout;
    private static final int PAGE_SIZE=100;
    private boolean isLastPage = false;
    private int currentPage = 1;
    private boolean isLoading = false;
    private LinearLayoutManager layoutManager;
    private ProgressBar progressbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create a new ArrayList of DeveloperList Object.
        developerList = new ArrayList<>();

        // Find references to declared variables.
        emptyView = (TextView) findViewById(R.id.empty);
        recyclerView = (EmptyRecyclerView) findViewById(R.id.recyclerView);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh_layout);
        progressbar = (ProgressBar) findViewById(R.id.loading_indicator);

        layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setHasFixedSize(true);
        recyclerView.setEmptyView(emptyView);
        recyclerView.addOnScrollListener(recyclerViewOnScrollListener);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshList();
            }
        });

        // Create a new DevelopersAdapter Object;
        mAdapter = new DevelopersAdapter(this,developerList);

        // Create a new ProgressDialog and display together with it"s message.
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        // set the recycler view adapter.
        recyclerView.setAdapter(mAdapter);

        // Make network request,
        // fetch th data,
        // parse the data,
        // and update the UI.
        parseUrlData();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        hidePDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh_menu:
                refreshLayout.setRefreshing(true);
                refreshList();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        // this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.refresh, menu);
        return true;
    }

    // A helper method which handles the whole network request to the given URL
    // and parse the data received using the {@Link MySingleton} class
    private void parseUrlData() {

        // The url to the required API endpoint.
        // Declared locally as a result of code implementation
        String urL = "https://api.github.com/search/users?q=location:lagos+language:java&per_page=100&page="+currentPage;

        // Volley JSONObject request
        JsonObjectRequest developersRequest = new JsonObjectRequest (Request.Method.GET, urL,null,
                new Response.Listener<JSONObject>() {
                    // A callback which gets triggered after getting a successful response from the server
                    // It extracts the required information from the stream of data received,
                    // creates a new {@Link DeveloperList} Object with the information
                    // and add each new object to the developers list.
                    @Override
                    public void onResponse(JSONObject response) {
                        isLoading = false;
                        hidePDialog();
                        hideProgressBar();
                        try {
                            JSONArray items = response.getJSONArray("items");
                            for (int i = 0; i < items.length(); i++) {
                                JSONObject object = items.getJSONObject(i);
                                String login = object.getString("login");
                                String html_url = object.getString("html_url");
                                String avatar_url = object.getString("avatar_url");
                                DeveloperList developer = new DeveloperList(login, html_url, avatar_url);
                                developerList.add(developer);

                                if(items.length() < PAGE_SIZE) {
                                    isLastPage = true;
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // Notifies the adapter of change in dataset.
                        mAdapter.notifyDataSetChanged();
                    }
                }, new Response.ErrorListener() {
            // A callback which gets triggered when an error occurs while connecting to the server
            // It handles all possible instances of error
            // and alert users of such error and possible solutions.
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePDialog();
                hideProgressBar();
                String message;
                if (error instanceof NetworkError || error instanceof TimeoutError) {
                    alertUI();
                } else if (error instanceof ServerError) {
                    message = "The server could not be found.Please try again after some time!!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } else if (error instanceof AuthFailureError) {
                    message = "Cannot connect to Internet...Please check your connection!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                } else if (error instanceof ParseError) {
                    message = "Parsing error! Please try again after some time!!";
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                }

            }
        });
        com.example.gsa.gitlagosusingvolley.MySingleton.getInstance(getApplicationContext()).addToRequestQueue(developersRequest);
    }

    // Hide the progress dialog if it isn't null.
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
        }
    }

    private void hideProgressBar() {
        if(progressbar !=null) {
            progressbar.setVisibility(View.GONE);
        }
    }

    // A helper method to display an alert dialog in case
    // of any error while connecting to the server especially Network Error.
    private void alertUI() {
        AlertDialog.Builder builder= new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(R.string.error)
                .setTitle("Warning:")
                .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        emptyView.setText(R.string.empty);
                    }
                })
                .setCancelable(true)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        emptyView.setText(R.string.empty);
                    }
                })
                .setNegativeButton("Open Settings", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity( new Intent(Settings.ACTION_SETTINGS));
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    // refresh the list of developers
    private void refreshList() {
        Intent intent = new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    // OnScrollListener with a callback which allows to fetch more data from the internet
    // as long as there are remaining untapped data from the API endpoint, and update the list.
    private RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

            if (!isLoading && !isLastPage) {
                if ((visibleItemCount + firstVisibleItemPosition) >=totalItemCount && firstVisibleItemPosition >= 0
                        && totalItemCount >= PAGE_SIZE){
                    loadMoreItems();
                }
            }
        }
    };

    // fetch more data from the server and update
    // the developers list.
    private void  loadMoreItems() {
        progressbar.setVisibility(View.VISIBLE);
        isLoading = true;
        currentPage++;
        parseUrlData();
    }
}

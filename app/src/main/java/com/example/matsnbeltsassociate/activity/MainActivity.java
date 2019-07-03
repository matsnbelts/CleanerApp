package com.example.matsnbeltsassociate.activity;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.model.Associate;
import com.example.matsnbeltsassociate.model.CustomerCar;
import com.example.matsnbeltsassociate.utils.CommonUtils;
import com.example.matsnbeltsassociate.utils.FireBaseHelper;
import com.example.matsnbeltsassociate.utils.InternalStorage;
import com.example.matsnbeltsassociate.utils.LocalDataFetcher;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivityLog";
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    CoordinatorLayout coordinatorLayout;
    ActionBar supportActionBar;
    private TextView associateNameTextView;
    private TextView associateRating;
    private boolean doubleBackToExitPressedOnce = false;
    private Context context;
    private String userId = "";

    public String getAssociateId() {
        return userId;
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return coordinatorLayout;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i("Stop: ", "Stopping");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Start: ", "Starting");
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Log.i("Resume: ", "Resuming");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("Pause: ", "Pausing");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Destroy: ", "Destroying");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        ///////////////////////////////////
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        ///////////////////////////////////
        context = getApplicationContext();
        View a = navigationView.getHeaderView(0);
        associateNameTextView = a.findViewById(R.id.nav_header_title);
        Log.i(TAG, "ddddddertewrtewr: " + associateNameTextView.getText().toString());
        associateRating = a.findViewById(R.id.star_rating);
//        final Toolbar mToolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(mToolbar);
        supportActionBar = this.getSupportActionBar();
        supportActionBar.setTitle(" ");
        coordinatorLayout = findViewById(R.id.constraintLayout);
        AppBarLayout appBarLayout = findViewById(R.id.app_bar);
        final CollapsingToolbarLayout collapseBarLayout = findViewById(R.id.toolbar_layout);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    isShow = true;
                    collapseBarLayout.setTitle(getResources().getString(R.string.app_name));

                } else if (isShow) {
                    isShow = false;
                    collapseBarLayout.setTitle(" ");
//                    TextView associateContactView = findViewById(R.id.associateContactextView);
//                    if(htmlText != null) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//                            associateContactView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
//                        } else {
//                            associateContactView.setText(Html.fromHtml(htmlText));
//                        }
//                    } else {
//                        associateContactView.setText("MATS N BELTS");
//                    }
                }
            }
        });

        recyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(false);

        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
//        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
//        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        populateAssociateDetailsInView();
    }


    private void populateAssociateDetailsInView() {
        Intent intent = getIntent();
        if (intent.getStringExtra(LauncherActivity.ASSOCIATE_ID) != null) {
            userId = intent.getStringExtra(LauncherActivity.ASSOCIATE_ID);
        } else if (intent.getStringExtra(LauncherActivity.EXTRA_MESSAGE) != null) {
            userId = intent.getStringExtra(LauncherActivity.EXTRA_MESSAGE);
        } else {
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Could not fetch associate details", Snackbar.LENGTH_LONG);
            snackbar.show();
        }
        //isnetWork - load data from db
        if (PhoneAuthActivity.isNetworkAvailable(context)) {
            FireBaseHelper.getInstance().fetchAssociateDetails(userId, this);
        } else if (InternalStorage.checkCacheFileForToday(context, context.getResources().getString(R.string.associate_file))) {
            LocalDataFetcher.getInstance().fetchAssociateDetails(this);
            Snackbar snackbar = Snackbar
                    .make(coordinatorLayout, "Connect to internet to get the latest updates", Snackbar.LENGTH_LONG);
            snackbar.show();
        } else {
            Intent network_intent = new Intent(context, NetworkConnectivity.class);
            Log.i(TAG, "Connect to internet");
            network_intent.putExtra(LauncherActivity.EXTRA_MESSAGE, NetworkConnectivity.main);
            network_intent.putExtra(LauncherActivity.ASSOCIATE_ID, userId);
            startActivity(network_intent);
            finish();
        }
    }

    public void setRecyclerView(AssociateAdapter associateAdapter) {
        recyclerView.setAdapter(associateAdapter);
        recyclerView.setVisibility(View.VISIBLE);
    }

    public void populateAssociateContactTextView(Associate associate) {
        TextView associateApartmentText = findViewById(R.id.associate_area);
        associateApartmentText.setText(associate.getServiceArea());
        TextView associateCarsAssignedText = findViewById(R.id.associate_cars_assigned);
        associateCarsAssignedText.setText("Cars Assigned: " + associate.getAssociateServiceCarMap().size() + "");
//        TextView associateMobileText = findViewById(R.id.associate_mobile);
//        associateMobileText.setText(associate.getMobile());
        //TextView associateNameTextView = findViewById(R.id.associate_name);
        associateNameTextView.setText(associate.getName());
        associateRating.setText(associate.getRating());
        TextView associateTodayText = findViewById(R.id.associate_today);
        associateTodayText.setText(CommonUtils.today());
    }


    public void inflatePopupInfo(CustomerCar customerCar) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        // Inflate the custom layout/view
        final PopupWindow popup;
        View customerInfoView;
        customerInfoView = inflater.inflate(R.layout.popup,null);
        popup = new PopupWindow(
                customerInfoView,
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            popup.setElevation(20);
        }
//        customerDetailsTextView.setText();
        // Get a reference for the custom view close button
        Button closeButton = customerInfoView.findViewById(R.id.btnClose);

        // Set a click listener for the popup window close button
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Dismiss the popup window
                popup.dismiss();
            }
        });
        popup.showAtLocation(coordinatorLayout, Gravity.FILL_HORIZONTAL, 0, 0);
        setCustomerDetailsInfoView(customerInfoView, customerCar);
    }

    private void setCustomerDetailsInfoView(View customerInfoView, CustomerCar customerCar) {
        ((TextView) customerInfoView.findViewById(R.id.customer_name)).setText(customerCar.getName());
        ((TextView) customerInfoView.findViewById(R.id.customer_number)).setText(customerCar.getCustomer_number());
        ((TextView) customerInfoView.findViewById(R.id.car_service_type)).setText(customerCar.getServiceType());
        ((TextView) customerInfoView.findViewById(R.id.car_model)).setText(customerCar.getModel());
        ((TextView) customerInfoView.findViewById(R.id.car_size)).setText(customerCar.getSize());
        ((TextView) customerInfoView.findViewById(R.id.car_area)).setText(customerCar.getArea());
        ((TextView) customerInfoView.findViewById(R.id.car_apartment)).setText(customerCar.getApartment());
        ((TextView) customerInfoView.findViewById(R.id.car_city)).setText(customerCar.getCity());
        customerInfoView.findViewById(R.id.loading_customer).setVisibility(View.GONE);
        customerInfoView.findViewById(R.id.customer_details).setVisibility(View.VISIBLE);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
//            Intent intent = new Intent(this, CloseAppActivity.class);
//            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            startActivity(intent);
            // finish();
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {

        } else if (id == R.id.nav_apply_leave) {

        } else if (id == R.id.nav_invite) {

        } else if (id == R.id.nav_supervisor) {

        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * RecyclerView item decoration - give equal margin around grid itemø
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }
}

package com.example.matsnbeltsassociate.activity;

import android.content.DialogInterface;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.matsnbeltsassociate.R;
import com.example.matsnbeltsassociate.model.Associate;
import com.example.matsnbeltsassociate.model.CustomerCar;
import com.example.matsnbeltsassociate.utils.CommonUtils;
import com.example.matsnbeltsassociate.utils.FireBaseHelper;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private PopupWindow popup;
    private View customerInfoView;
    CoordinatorLayout coordinatorLayout;
    ActionBar supportActionBar;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Toolbar mToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
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
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        //recyclerView.setItemAnimator(new DefaultItemAnimator());
        populateAssociateDetailsInView();

    }

    public void finishCleaning(final AssociateAdapter.MyViewHolder holder) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);

        alert.setTitle("Done Cleaning !!!!");
        alert.setMessage("Leave any comments below. Clicking OK will end the cleaning and cannot be undone");

// Set an EditText view to get user input
        final EditText input = new EditText(this);
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            holder.feedbackText = input.getText().toString();
            holder.cardView.setBackgroundColor(getResources().getColor(R.color.colorYellow));
                // Do something with value!
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                // Canceled.
            }
        });

        alert.show();
    }
    public void callPopup(String customerId, String carNo) {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(LAYOUT_INFLATER_SERVICE);

        // Inflate the custom layout/view
        customerInfoView = inflater.inflate(R.layout.popup,null);

                /*
                    public PopupWindow (View contentView, int width, int height)
                        Create a new non focusable popup window which can display the contentView.
                        The dimension of the window must be passed to this constructor.

                        The popup does not provide any background. This should be handled by
                        the content view.

                    Parameters
                        contentView : the popup's content
                        width : the popup's width
                        height : the popup's height
                */
        // Initialize a new instance of popup window
        popup = new PopupWindow(
                customerInfoView,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT
        );
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        // Set an elevation value for popup window
        // Call requires API level 21
        if(Build.VERSION.SDK_INT>=21){
            popup.setElevation(5.0f);
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
        FireBaseHelper.getInstance().fetchCustomerDetails(customerId, carNo, this);

                /*
                    public void showAtLocation (View parent, int gravity, int x, int y)
                        Display the content view in a popup window at the specified location. If the
                        popup window cannot fit on screen, it will be clipped.
                        Learn WindowManager.LayoutParams for more information on how gravity and the x
                        and y parameters are related. Specifying a gravity of NO_GRAVITY is similar
                        to specifying Gravity.LEFT | Gravity.TOP.

                    Parameters
                        parent : a parent view to get the getWindowToken() token from
                        gravity : the gravity which controls the placement of the popup window
                        x : the popup's x location offset
                        y : the popup's y location offset
                */
        // Finally, show the popup window at the center location of root relative layout
        popup.showAtLocation(coordinatorLayout, Gravity.CENTER,0,0);

    }

    public void setCustomerDetailsInfoView(CustomerCar customerCar) {
        final TextView customerDetailsTextView = customerInfoView.findViewById(R.id.customerDetails);
        customerDetailsTextView.setText(customerCar.toString());
    }
    private void populateAssociateDetailsInView() {
        Intent intent = getIntent();
        String userId = intent.getStringExtra(LauncherActivity.EXTRA_MESSAGE);
        Log.i("MainActivity:::", "Received " + userId);

        FireBaseHelper.getInstance().fetchAssociateDetails(userId, this);
    }

    public void setRecyclerView(AssociateAdapter associateAdapter) {
        recyclerView.setAdapter(associateAdapter);
    }

    public void populateAssociateContactTextView(Associate associate) {
        TextView associateApartmentText = findViewById(R.id.associate_area);
        associateApartmentText.setText(associate.getServiceArea());
        TextView associateCarsAssignedText = findViewById(R.id.associate_cars_assigned);
        associateCarsAssignedText.setText("Cars Assigned: " + associate.getAssociateServiceCarMap().size() + "");
//        TextView associateMobileText = findViewById(R.id.associate_mobile);
//        associateMobileText.setText(associate.getMobile());
        TextView associateNameText = findViewById(R.id.associate_name);
        associateNameText.setText("Welcome " + associate.getName());
        TextView associateTodayText = findViewById(R.id.associate_today);
        associateTodayText.setText(CommonUtils.today());
    }
    /**
     * RecyclerView item decoration - give equal margin around grid item
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

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            Intent intent = new Intent(this, CloseAppActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
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
}

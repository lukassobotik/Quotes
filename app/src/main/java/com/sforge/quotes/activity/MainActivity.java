package com.sforge.quotes.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sforge.quotes.R;
import com.sforge.quotes.dialog.CollectionsDialog;

public class MainActivity extends AppCompatActivity implements CollectionsDialog.CollectionsDialogListener {
    BottomNavigationView bottomNavigationView;

    private HomeFragment homeFragment;
    private UserProfileFragment userProfileFragment;
    private CreateQuotesFragment createQuotesFragment;
    private SearchFragment searchFragment;
    private CollectionsFragment collectionsActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        userProfileFragment = new UserProfileFragment();
        createQuotesFragment = new CreateQuotesFragment();
        searchFragment = new SearchFragment();
        collectionsActivityFragment = new CollectionsFragment();

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_search) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
            }
        });
    }

    public Fragment getCurrentFragment() {
        return getSupportFragmentManager().findFragmentById(R.id.fragment_container);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.navigation_home:
                            selectedFragment = homeFragment;
                            break;
                        case R.id.navigation_search:
                            selectedFragment = searchFragment;
                            break;
                        case R.id.navigation_create:
                            selectedFragment = createQuotesFragment;
                            break;
                        case R.id.navigation_collections:
                            selectedFragment = collectionsActivityFragment;
                            break;
                        case R.id.navigation_account:
                            selectedFragment = userProfileFragment;
                            break;
                    }

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();

                    return true;
                }
            };

    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onPause() {
        super.onPause();
//        //Store the Recyclerview scroll position
//        lastFirstVisiblePosition =
//                ((LinearLayoutManager) Objects.requireNonNull(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        //Restore the last stored Recyclerview scroll position
//        recyclerView.getLayoutManager().scrollToPosition(lastFirstVisiblePosition);
//        if (quoteAdapter.getItemCount() != 0) {
//            setCurrentQuoteCreatorInfo(lastFirstVisiblePosition);
//            collectionsAdapter.setQuote(quoteAdapter.getQuoteFromPosition(lastFirstVisiblePosition));
//        }
//
//        //#5 Issue Fix
//        if (FirebaseAuth.getInstance().getCurrentUser() != null && !isLoggedIn) {
//            finish();
//            startActivity(new Intent(MainActivity.this, MainActivity.class));
//        }
    }

    @Override
    public void getData(int option) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
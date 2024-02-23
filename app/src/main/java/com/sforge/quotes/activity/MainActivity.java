package com.sforge.quotes.activity;

import android.os.Bundle;
import android.transition.Fade;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.sforge.quotes.R;
import com.sforge.quotes.dialog.CollectionsDialog;
import com.sforge.quotes.fragment.CollectionsFragment;
import com.sforge.quotes.fragment.CreateQuotesFragment;
import com.sforge.quotes.fragment.HomeFragment;
import com.sforge.quotes.fragment.ExploreFragment;
import com.sforge.quotes.fragment.UserProfileFragment;

public class MainActivity extends AppCompatActivity implements CollectionsDialog.CollectionsDialogListener {
    BottomNavigationView bottomNavigationView;

    public HomeFragment homeFragment;
    public UserProfileFragment userProfileFragment;
    public CreateQuotesFragment createQuotesFragment;
    public ExploreFragment exploreFragment;
    public CollectionsFragment collectionsActivityFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottom_navigation);

        homeFragment = new HomeFragment();
        homeFragment.setEnterTransition(new Fade());
        userProfileFragment = new UserProfileFragment();
        userProfileFragment.setEnterTransition(new Fade());
        createQuotesFragment = new CreateQuotesFragment();
        createQuotesFragment.setEnterTransition(new Fade());
        exploreFragment = new ExploreFragment();
        exploreFragment.setEnterTransition(new Fade());
        collectionsActivityFragment = new CollectionsFragment();
        collectionsActivityFragment.setEnterTransition(new Fade());

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setOnNavigationItemReselectedListener(item -> {
            if (item.getItemId() == R.id.navigation_explore) {
                exploreFragment.expandSearch();
            } else if (item.getItemId() == R.id.navigation_account) {
                userProfileFragment.openSettingsSheet();
            }
        });
    }

    public void loadFragment(Fragment fragment) {
        if (fragment instanceof HomeFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_home);
        } else if (fragment instanceof ExploreFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_explore);
        } else if (fragment instanceof CreateQuotesFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_create);
        } else if (fragment instanceof CollectionsFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_collections);
        } else if (fragment instanceof UserProfileFragment) {
            bottomNavigationView.setSelectedItemId(R.id.navigation_account);
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
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
                        case R.id.navigation_explore:
                            selectedFragment = exploreFragment;
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
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void getData(int option) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
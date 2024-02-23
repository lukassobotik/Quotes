package com.sforge.quotes.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.materialswitch.MaterialSwitch;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.activity.LoginActivity;
import com.sforge.quotes.activity.MainActivity;
import com.sforge.quotes.adapter.ImageAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.entity.Background;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.entity.User;
import com.sforge.quotes.entity.UserPreferences;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserPreferencesRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {
    private static final String ARG_USERNAME = "username";

    private String usernameParam;
    
    private Button profileSettings, changeBackground, layoutBackButton, backButton, aboutButton, dataRequestButton, deleteAccountButton, loginButton, logoutButton;
    private MaterialSwitch useDynamicBackgroundButton;

    private String email = "";

    private final int PREFETCH_DISTANCE = 10;
    private boolean isUserLoggedIn = false;
    private boolean useDynamicBackground = false;
    List<Quote> usrQuotes = new ArrayList<>();

    QuoteRepository quoteRepository;
    UserQuoteAdapter usrAdapter;
    private RecyclerView usrQuotesRV, changeBackgroundRV;
    LinearLayout changeBackgroundLinearLayout;
    ConstraintLayout userProfileLayout;
    EditText deleteButtonEditText;
    FrameLayout settingsBottomSheet;
    BottomSheetBehavior<FrameLayout> bottomSheetBehavior;
    UserPreferencesRepository userPreferencesRepository;
    private final BiFunction<TextView, TextView, ValueEventListener> onDataChangeListener =
            (emailTV, usernameTV) -> new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        String userEmail = userProfile.getEmail();
                        String username = userProfile.getUsername();
                        email = userEmail;
                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append("@").append(username);

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if (user != null) {
                            for (UserInfo profile : user.getProviderData()) {
                                String uEmail = profile.getEmail();
                                emailTV.setText(uEmail);
                            }
                        }
                        usernameTV.setText(stringBuilder);
                        usernameParam = String.valueOf(stringBuilder);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error){
                    Toast.makeText(getActivity(), "Couldn't Retrieve the User Info. " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            };

    public UserProfileFragment() {
        // Required empty public constructor
    }

    public static UserProfileFragment newInstance(String userEmailArg) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_USERNAME, userEmailArg);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            usernameParam = getArguments().getString(ARG_USERNAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            Toast.makeText(getActivity(), "Please Log In to View Your Profile.", Toast.LENGTH_SHORT).show();
        }

        View fragmentView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        TextView emailTV = fragmentView.findViewById(R.id.profileEmail);
        TextView usernameTV = fragmentView.findViewById(R.id.username);
        defineViews(fragmentView);

        if (usernameParam != null) {
            usernameTV.setText(usernameParam);
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            isUserLoggedIn = true;
            String userID = user.getUid();
            new UserRepository().getDatabaseReference()
                    .child(userID)
                    .addListenerForSingleValueEvent(onDataChangeListener.apply(emailTV, usernameTV));
            userPreferencesRepository = new UserPreferencesRepository(userID);
            loadBackgroundPreferences();
        }

        if (isUserLoggedIn) {
            loginButton.setVisibility(View.GONE);
            logoutButton.setVisibility(View.VISIBLE);
        } else {
            loginButton.setVisibility(View.VISIBLE);
            logoutButton.setVisibility(View.GONE);
        }

        loginButton.setOnClickListener(view -> {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        });

        logoutButton.setOnClickListener(view -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
        });

        createSettingsMenu();

        if (isUserLoggedIn) {
            loadUserQuotes(null, FirebaseAuth.getInstance().getCurrentUser().getUid());
        }

        aboutButton.setOnClickListener(view -> {
            Uri webpage = Uri.parse("https://github.com/lukassobotik/Quotes/blob/master/About.md");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(intent);
        });

        changeBackground.setOnClickListener(view -> {
            changeBackgroundLinearLayout.setVisibility(View.VISIBLE);
            backButton.setVisibility(View.VISIBLE);
        });

        backButton.setOnClickListener(view -> {
            changeBackgroundLinearLayout.setVisibility(View.GONE);
            backButton.setVisibility(View.GONE);
        });

        dataRequestButton.setOnClickListener(view -> {
            Uri webpage = Uri.parse("https://myquotes.account.lukassobotik.dev/stored-data/");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(intent);
        });

        deleteAccountButton.setOnClickListener(view -> {
            Uri webpage = Uri.parse("https://myquotes.account.lukassobotik.dev/delete-account/");
            Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
            startActivity(intent);
        });

        return fragmentView;
    }

    private void loadBackgroundPreferences() {
        userPreferencesRepository.getDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    return;
                }

                List<UserPreferences> usrPrefs = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    UserPreferences userPreferences = data.getValue(UserPreferences.class);
                    usrPrefs.add(userPreferences);
                }
                if (!usrPrefs.isEmpty()) {
                    String background = usrPrefs.get(0).getBgId();
                    useDynamicBackground = background.equals(new Background().DYNAMIC);
                    useDynamicBackgroundButton.setChecked(useDynamicBackground);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserProfile", error.getMessage());
            }
        });
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void createSettingsMenu() {
        final AtomicBoolean isSettingMenuOpen = new AtomicBoolean(false);
        final AtomicBoolean isBackgroundSettingShown = new AtomicBoolean(false);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                    isSettingMenuOpen.set(false);
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });

        profileSettings.setOnClickListener(view -> {
            if (!isSettingMenuOpen.get()) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                isSettingMenuOpen.set(true);
            } else {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
                isBackgroundSettingShown.set(false);
                isSettingMenuOpen.set(false);
            }
        });

        useDynamicBackgroundButton.setOnClickListener(view -> {
            useDynamicBackground = !useDynamicBackground;
            if (useDynamicBackground) {
                UserPreferences userPreferences = new UserPreferences(new Background().DYNAMIC, "high");
                userPreferencesRepository.addWithKey("Background", userPreferences).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext().getApplicationContext(), "Successfully changed the background to use Dynamic Color", Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                UserPreferences userPreferences = new UserPreferences(new Background().RSZ_GREY, "low");
                userPreferencesRepository.addWithKey("Background", userPreferences).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(requireContext().getApplicationContext(), "Successfully changed the background to Gray", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        ImageAdapter adapter = new ImageAdapter(getActivity());
        List<Drawable> images = new ArrayList<>();
        images.add(getResources().getDrawable(R.drawable.rsz_blue_sky_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_bridge_in_forest_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_night_city_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_dark_mountains_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_bridge_in_forest_2, null));
        images.add(getResources().getDrawable(R.drawable.rsz_forest_1, null));
        images.add(getResources().getDrawable(R.drawable.rsz_white_gradient, null));
        images.add(getResources().getDrawable(R.drawable.rsz_grey, null));
        images.add(getResources().getDrawable(R.drawable.rsz_orange_purple_gradient, null));
        adapter.setItems(images);
        changeBackgroundRV.setLayoutManager(manager);
        changeBackgroundRV.setAdapter(adapter);
    }

    public void loadUserQuotes(String nodeId, String userId) {
        Query query;

        if (nodeId == null) {
            query = new UserQuoteRepository(userId)
                    .getDatabaseReference()
                    .orderByKey()
                    .limitToFirst(PREFETCH_DISTANCE);
        } else {
            query = new UserQuoteRepository(userId)
                    .getDatabaseReference()
                    .orderByKey()
                    .startAfter(nodeId)
                    .limitToFirst(PREFETCH_DISTANCE);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Quote> quotes = new ArrayList<>();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Quote quote = data.getValue(Quote.class);
                    if (quote != null) {
                        Quote quoteWithKey =
                                new Quote(quote.getQuote(), quote.getAuthor(), quote.getUser(), data.getKey());
                        quotes.add(quoteWithKey);
                    }
                }

                usrAdapter.addItems(new ArrayList<>(quotes));
                usrAdapter.notifyItemRangeInserted(usrAdapter.getItemCount(), quotes.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "" + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void openSettingsSheet() {
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
    }

    private void defineViews(View view) {
        profileSettings = view.findViewById(R.id.profileSettingsButton);
        aboutButton = view.findViewById(R.id.profileAbout);
        dataRequestButton = view.findViewById(R.id.profileRequestUserData);
        deleteAccountButton = view.findViewById(R.id.profileDeleteAccount);
        backButton = view.findViewById(R.id.settings_back_button);
        backButton.setVisibility(View.GONE);
        useDynamicBackgroundButton = view.findViewById(R.id.changeBackgroundDynamicColorSwitch);
        loginButton = view.findViewById(R.id.profileLogin);
        logoutButton = view.findViewById(R.id.profileLogout);
        userProfileLayout = view.findViewById(R.id.user_profile_layout);
        LinearLayoutManager usrManager = new LinearLayoutManager(getActivity());
        usrAdapter = new UserQuoteAdapter(getActivity());
        usrQuotesRV = view.findViewById(R.id.usrQuotes);
        usrQuotesRV.setAdapter(usrAdapter);
        usrQuotesRV.setLayoutManager(usrManager);
        quoteRepository = new QuoteRepository();
        layoutBackButton = view.findViewById(R.id.mainBackButton);
        layoutBackButton.setVisibility(View.GONE);
        changeBackground = view.findViewById(R.id.profileChangeBackground);
        changeBackgroundRV = view.findViewById(R.id.changeBackgroundRecyclerView);
        changeBackgroundLinearLayout = view.findViewById(R.id.settings_background_layout);
        changeBackgroundLinearLayout.setVisibility(View.GONE);
        settingsBottomSheet = view.findViewById(R.id.settings_bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(settingsBottomSheet);
        bottomSheetBehavior.setHideable(true);
        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);
    }
}
package com.sforge.quotes.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.sforge.quotes.dialog.CollectionsDialog;

public class UserProfile extends AppCompatActivity implements CollectionsDialog.CollectionsDialogListener {

//    private Button profileSettings, changeBackground, backButton, layoutBackButton, aboutButton, dataRequestButton, userDataBackButton, aboutBackButton, deleteAccountButton, deleteButtonSubmit, deleteButtonCancel;
//
//    private String email = "";
//
//    private final int PREFETCH_DISTANCE = 10;
//
//    TextView userDataInfoTV, userDataTV, userDataPrefsTV, userDataQuotesTV, userDataUsernameTV, aboutAndroidStudioTV, aboutFirebaseTV, aboutSwipeLayoutTV, aboutGithub, quoteSource, deleteAccountText, privacyPolicy;
//
//    List<Quote> usrQuotes = new ArrayList<>();
//
//    QuoteRepository quoteRepository;
//    UserQuoteAdapter usrAdapter;
//    private RecyclerView usrQuotesRV, changeBackgroundRV;
//    LinearLayout backgroundRVLinearLayout, userDataLayout, aboutLayout;
//    ConstraintLayout deleteLayout, userProfileLayout;
//    EditText deleteButtonEditText;
//
//    private final BiFunction<TextView, TextView, ValueEventListener> onDataChangeListener =
//            (emailTV, usernameTV) -> new ValueEventListener() {
//                @Override
//                public void onDataChange(@NonNull DataSnapshot snapshot) {
//                    User userProfile = snapshot.getValue(User.class);
//                    if (userProfile != null) {
//                        String userEmail = userProfile.getEmail();
//                        String username = userProfile.getUsername();
//                        email = userEmail;
//                        StringBuilder stringBuilder = new StringBuilder();
//                        stringBuilder.append("@").append(username);
//
//                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//                        if (user != null) {
//                            for (UserInfo profile : user.getProviderData()) {
//                                String uEmail = profile.getEmail();
//                                emailTV.setText(uEmail);
//                            }
//                        }
//                        usernameTV.setText(stringBuilder);
//                    }
//                }
//
//                @Override
//                public void onCancelled(@NonNull DatabaseError error){
//                    Toast.makeText(UserProfile.this, "Couldn't Retrieve the User Info. " + error.getMessage(), Toast.LENGTH_SHORT).show();
//                }
//            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_user_profile);
//
//        TextView emailTV = findViewById(R.id.profileEmail);
//        TextView usernameTV = findViewById(R.id.username);
//
//        profileSettings = findViewById(R.id.profileSettingsButton);
//        aboutButton = findViewById(R.id.profileAbout);
//        aboutButton.setVisibility(View.GONE);
//        dataRequestButton = findViewById(R.id.profileRequestUserData);
//        dataRequestButton.setVisibility(View.GONE);
//        userDataInfoTV = findViewById(R.id.user_data_info_tv);
//        userDataTV = findViewById(R.id.user_data_bookmarks_tv);
//        userDataPrefsTV = findViewById(R.id.user_data_preferences_tv);
//        userDataQuotesTV = findViewById(R.id.user_data_quotes_tv);
//        userDataUsernameTV = findViewById(R.id.user_data_username_tv);
//        userDataLayout = findViewById(R.id.user_data_layout);
//        userDataBackButton = findViewById(R.id.user_data_back_button);
//        aboutLayout = findViewById(R.id.about_layout);
//        aboutBackButton = findViewById(R.id.about_back_button);
//        aboutAndroidStudioTV = findViewById(R.id.about_android_studio_tv);
//        aboutFirebaseTV = findViewById(R.id.about_firebase_tv);
//        aboutSwipeLayoutTV = findViewById(R.id.about_swipe_reveal_layout_tv);
//        aboutGithub = findViewById(R.id.about_txt_github);
//        quoteSource = findViewById(R.id.about_txt_goodreads);
//        deleteAccountButton = findViewById(R.id.profileDeleteAccount);
//        deleteAccountButton.setVisibility(View.GONE);
//        deleteLayout = findViewById(R.id.delete_account_layout);
//        deleteButtonSubmit = findViewById(R.id.delete_account_delete);
//        deleteButtonCancel = findViewById(R.id.delete_account_cancel);
//        deleteButtonEditText = findViewById(R.id.delete_account_edit_text);
//        deleteAccountText = findViewById(R.id.delete_account_message_confirmation);
//        privacyPolicy = findViewById(R.id.about_privacy_policy);
//        userProfileLayout = findViewById(R.id.user_profile_layout);
//        LinearLayoutManager usrManager = new LinearLayoutManager(this);
//        usrAdapter = new UserQuoteAdapter(this);
//        usrQuotesRV = findViewById(R.id.usrQuotes);
//        usrQuotesRV.setAdapter(usrAdapter);
//        usrQuotesRV.setLayoutManager(usrManager);
//        quoteRepository = new QuoteRepository();
//        backButton = findViewById(R.id.profileBackButton);
//        backButton.setOnClickListener(view -> finish());
//        layoutBackButton = findViewById(R.id.mainBackButton);
//        layoutBackButton.setVisibility(View.GONE);
//        changeBackground = findViewById(R.id.profileChangeBackground);
//        changeBackground.setVisibility(View.GONE);
//        changeBackgroundRV = findViewById(R.id.changeBackgroundRecyclerView);
//        changeBackgroundRV.setVisibility(View.GONE);
//        backgroundRVLinearLayout = findViewById(R.id.changeBackgroundRVLinearLayout);
//        backgroundRVLinearLayout.setVisibility(View.GONE);
//
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user != null) {
//            String userID = user.getUid();
//            new UserRepository().getDatabaseReference()
//                    .child(userID)
//                    .addListenerForSingleValueEvent(onDataChangeListener.apply(emailTV, usernameTV));
//        }
//
//        createSettingsMenu();
//
//        loadUserQuotes(null, FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        aboutBackButton.setOnClickListener(view -> aboutLayout.setVisibility(View.GONE));
//
//        aboutButton.setOnClickListener(view -> {
//            aboutLayout.setVisibility(View.VISIBLE);
//        });
//
//        userDataBackButton.setOnClickListener(view -> userDataLayout.setVisibility(View.GONE));
//
//        dataRequestButton.setOnClickListener(view -> {
//            userDataLayout.setVisibility(View.VISIBLE);
//
//
//            if (user != null) {
//                String userID = user.getUid();
//                new UserRepository().getDatabaseReference()
//                        .child(userID)
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                StringBuilder bookmarksBuilder = new StringBuilder();
//                                StringBuilder preferencesBuilder = new StringBuilder();
//                                StringBuilder quotesBuilder = new StringBuilder();
//                                StringBuilder usernameBuilder = new StringBuilder();
//                                for (DataSnapshot data : snapshot.getChildren()) {
//                                    if (data.getValue() instanceof HashMap && Objects.equals(data.getKey(), "Bookmarks")) {
//                                        for (DataSnapshot ds : data.getChildren()) {
//                                            bookmarksBuilder.append(ds.getKey()).append(": ").append("\n");
//                                            for (DataSnapshot ds2 : ds.getChildren()) {
//                                                bookmarksBuilder.append(ds2.getKey()).append(": ").append(ds2.getValue()).append("\n").append("\n");
//                                            }
//                                            bookmarksBuilder.append("\n").append("\n").append("\n");
//                                        }
//                                    }
//                                    if (data.getValue() instanceof HashMap && Objects.equals(data.getKey(), "User Preferences")) {
//                                        for (DataSnapshot ds : data.getChildren()) {
//                                            preferencesBuilder.append(ds.getKey()).append(": ").append("\n");
//                                            for (DataSnapshot ds2 : ds.getChildren()) {
//                                                preferencesBuilder.append(ds2.getKey()).append(": ").append(ds2.getValue()).append("\n").append("\n");
//                                            }
//                                            preferencesBuilder.append("\n").append("\n").append("\n");
//                                        }
//
//                                    }
//                                    if (data.getValue() instanceof HashMap && Objects.equals(data.getKey(), "User Quotes")) {
//                                        for (DataSnapshot ds : data.getChildren()) {
//                                            quotesBuilder.append(ds.getKey()).append(": ").append("\n").append(ds.getValue());
//                                            quotesBuilder.append("\n").append("\n").append("\n");
//                                        }
//                                    }
//                                    if (data.getValue() instanceof String) {
//                                        String s = data.getValue(String.class);
//                                        usernameBuilder.append(data.getKey()).append(": ").append("\n")
//                                                .append(s).append("\n").append("\n").append("\n");
//                                    }
//                                }
//                                userDataTV.setText(bookmarksBuilder.toString());
//                                userDataPrefsTV.setText(preferencesBuilder.toString());
//                                userDataQuotesTV.setText(quotesBuilder.toString());
//                                userDataUsernameTV.setText(usernameBuilder.toString());
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//                                Log.e("UserProfile", error.getMessage());
//                            }
//                        });
//            }
//        });
//
//        userDataInfoTV.setMovementMethod(LinkMovementMethod.getInstance());
//        aboutAndroidStudioTV.setMovementMethod(LinkMovementMethod.getInstance());
//        aboutFirebaseTV.setMovementMethod(LinkMovementMethod.getInstance());
//        aboutSwipeLayoutTV.setMovementMethod(LinkMovementMethod.getInstance());
//        aboutGithub.setMovementMethod(LinkMovementMethod.getInstance());
//        quoteSource.setMovementMethod(LinkMovementMethod.getInstance());
//        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());
//
//        deleteAccountButton.setOnClickListener(view -> {
//            userProfileLayout.setVisibility(View.GONE);
//            deleteLayout.setVisibility(View.VISIBLE);
//            if (user != null) {
//                new UsernameRepository(user.getUid()).getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String userProfile = snapshot.getValue(String.class);
//                        if (userProfile != null) {
//                            StringBuilder stringBuilder = new StringBuilder();
//                            stringBuilder.append("Please Type \"").append(userProfile).append("\" to Delete Your Account");
//                            deleteAccountText.setText(stringBuilder.toString());
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("UserProfile", error.getMessage());
//                    }
//                });
//            }
//        });
//
//        deleteButtonCancel.setOnClickListener(view -> {
//            userProfileLayout.setVisibility(View.VISIBLE);
//            deleteLayout.setVisibility(View.GONE);
//        });
//
//        deleteButtonSubmit.setOnClickListener(view -> {
//            if (user != null) {
//                new UsernameRepository(user.getUid()).getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        String userProfile = snapshot.getValue(String.class);
//                        if (userProfile != null) {
//                            if (deleteButtonEditText.getText().toString().trim().equals(userProfile)) {
//
//                                new UserRepository().remove(user.getUid()).addOnCompleteListener(delTask -> {
//                                    if (delTask.isSuccessful()) {
//                                        Toast.makeText(UserProfile.this, "Successfully Deleted The Account Data", Toast.LENGTH_SHORT).show();
//                                        user.delete().addOnCompleteListener(task -> {
//                                            if (task.isSuccessful()) {
//                                                Toast.makeText(UserProfile.this, "Successfully Deleted The Account", Toast.LENGTH_SHORT).show();
//                                                finish();
//                                                startActivity(new Intent(UserProfile.this ,MainActivity.class));
//                                            } else {
//                                                Toast.makeText(UserProfile.this, "Couldn't delete The Account. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                                            }
//                                        });
//                                    } else {
//                                        Toast.makeText(UserProfile.this, "Couldn't delete The Account Data. " + Objects.requireNonNull(delTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
//                                    }
//                                });
//                            }
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("UserProfile", error.getMessage());
//                    }
//                });
//            }
//        });
    }

//    @SuppressLint("UseCompatLoadingForDrawables")
//    public void createSettingsMenu() {
//        final AtomicBoolean isSettingMenuOpen = new AtomicBoolean(false);
//        final AtomicBoolean isBackgroundSettingShown = new AtomicBoolean(false);
//        profileSettings.setOnClickListener(view -> {
//            if (!isSettingMenuOpen.get()) {
//                changeBackground.setVisibility(View.VISIBLE);
//                changeBackground.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
//                dataRequestButton.setVisibility(View.VISIBLE);
//                dataRequestButton.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
//                aboutButton.setVisibility(View.VISIBLE);
//                aboutButton.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
//                deleteAccountButton.setVisibility(View.VISIBLE);
//                deleteAccountButton.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
//                isSettingMenuOpen.set(true);
//            } else {
//                changeBackground.setVisibility(View.GONE);
//                changeBackground.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
//                if (isBackgroundSettingShown.get()) {
//                    backgroundRVLinearLayout.setVisibility(View.GONE);
//                    backgroundRVLinearLayout.startAnimation(
//                            AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
//                }
//                changeBackgroundRV.setVisibility(View.GONE);
//                changeBackgroundRV.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
//                dataRequestButton.setVisibility(View.GONE);
//                dataRequestButton.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
//                aboutButton.setVisibility(View.GONE);
//                aboutButton.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
//                deleteAccountButton.setVisibility(View.GONE);
//                deleteAccountButton.startAnimation(
//                        AnimationUtils.loadAnimation(getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
//                isBackgroundSettingShown.set(false);
//                isSettingMenuOpen.set(false);
//            }
//        });
//
//        StaggeredGridLayoutManager manager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
//        ImageAdapter adapter = new ImageAdapter(this);
//        List<Drawable> images = new ArrayList<>();
//        images.add(getResources().getDrawable(R.drawable.rsz_blue_sky_1, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_bridge_in_forest_1, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_night_city_1, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_dark_mountains_1, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_bridge_in_forest_2, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_forest_1, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_white_gradient, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_grey, null));
//        images.add(getResources().getDrawable(R.drawable.rsz_orange_purple_gradient, null));
//        adapter.setItems(images);
//        changeBackgroundRV.setLayoutManager(manager);
//        changeBackgroundRV.setAdapter(adapter);
//
//        changeBackground.setOnClickListener(view -> {
//            if (!isBackgroundSettingShown.get()) {
//                backgroundRVLinearLayout.setVisibility(View.VISIBLE);
//                changeBackgroundRV.setVisibility(View.VISIBLE);
//                isBackgroundSettingShown.set(true);
//            } else {
//                backgroundRVLinearLayout.setVisibility(View.GONE);
//                changeBackgroundRV.setVisibility(View.GONE);
//                isBackgroundSettingShown.set(false);
//            }
//        });
//    }
//
//    public void loadUserQuotes(String nodeId, String userId) {
//        Query query;
//
//        if (nodeId == null) {
//            query = new UserQuoteRepository(userId)
//                    .getDatabaseReference()
//                    .orderByKey()
//                    .limitToFirst(PREFETCH_DISTANCE);
//        } else {
//            query = new UserQuoteRepository(userId)
//                    .getDatabaseReference()
//                    .orderByKey()
//                    .startAfter(nodeId)
//                    .limitToFirst(PREFETCH_DISTANCE);
//        }
//
//        query.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                List<Quote> quotes = new ArrayList<>();
//                for (DataSnapshot data : snapshot.getChildren()) {
//                    Quote quote = data.getValue(Quote.class);
//                    if (quote != null) {
//                        Quote quoteWithKey = new Quote(quote.getQuote(), quote.getAuthor(), quote.getUser(), data.getKey());
//                        quotes.add(quoteWithKey);
//                    }
//                }
//
//                usrAdapter.addItems(new ArrayList<>(quotes));
//                usrAdapter.notifyItemRangeInserted(usrAdapter.getItemCount(), quotes.size());
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(UserProfile.this, "" + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }

    @Override
    protected void onStart() {
        super.onStart();

//        usrQuotesRV.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
//                super.onScrollStateChanged(recyclerView, newState);
//                if (!recyclerView.canScrollVertically(1)) {
//                    loadUserQuotes(usrAdapter.getLastItemId(), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid());
//                }
//            }
//        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void getData(int option) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}

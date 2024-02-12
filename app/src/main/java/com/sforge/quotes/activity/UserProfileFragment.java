package com.sforge.quotes.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.sforge.quotes.R;
import com.sforge.quotes.adapter.ImageAdapter;
import com.sforge.quotes.adapter.UserQuoteAdapter;
import com.sforge.quotes.entity.Quote;
import com.sforge.quotes.entity.User;
import com.sforge.quotes.repository.QuoteRepository;
import com.sforge.quotes.repository.UserQuoteRepository;
import com.sforge.quotes.repository.UserRepository;
import com.sforge.quotes.repository.UsernameRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiFunction;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link UserProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class UserProfileFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    
    
    
    private Button profileSettings, changeBackground, backButton, layoutBackButton, aboutButton, dataRequestButton, userDataBackButton, aboutBackButton, deleteAccountButton, deleteButtonSubmit, deleteButtonCancel;

    private String email = "";

    private final int PREFETCH_DISTANCE = 10;

    TextView userDataInfoTV, userDataTV, userDataPrefsTV, userDataQuotesTV, userDataUsernameTV, aboutAndroidStudioTV, aboutFirebaseTV, aboutSwipeLayoutTV, aboutGithub, quoteSource, deleteAccountText, privacyPolicy;

    List<Quote> usrQuotes = new ArrayList<>();

    QuoteRepository quoteRepository;
    UserQuoteAdapter usrAdapter;
    private RecyclerView usrQuotesRV, changeBackgroundRV;
    LinearLayout backgroundRVLinearLayout, userDataLayout, aboutLayout;
    ConstraintLayout deleteLayout, userProfileLayout;
    EditText deleteButtonEditText;

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
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error){
                    Toast.makeText(getActivity(), "Couldn't Retrieve the User Info. " + error.getMessage(),
                                   Toast.LENGTH_SHORT).show();
                }
            };

    public UserProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment UserProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static UserProfileFragment newInstance(String param1, String param2) {
        UserProfileFragment fragment = new UserProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_user_profile, container, false);

        TextView emailTV = fragmentView.findViewById(R.id.profileEmail);
        TextView usernameTV = fragmentView.findViewById(R.id.username);

        profileSettings = fragmentView.findViewById(R.id.profileSettingsButton);
        aboutButton = fragmentView.findViewById(R.id.profileAbout);
        aboutButton.setVisibility(View.GONE);
        dataRequestButton = fragmentView.findViewById(R.id.profileRequestUserData);
        dataRequestButton.setVisibility(View.GONE);
        userDataInfoTV = fragmentView.findViewById(R.id.user_data_info_tv);
        userDataTV = fragmentView.findViewById(R.id.user_data_bookmarks_tv);
        userDataPrefsTV = fragmentView.findViewById(R.id.user_data_preferences_tv);
        userDataQuotesTV = fragmentView.findViewById(R.id.user_data_quotes_tv);
        userDataUsernameTV = fragmentView.findViewById(R.id.user_data_username_tv);
        userDataLayout = fragmentView.findViewById(R.id.user_data_layout);
        userDataBackButton = fragmentView.findViewById(R.id.user_data_back_button);
        aboutLayout = fragmentView.findViewById(R.id.about_layout);
        aboutBackButton = fragmentView.findViewById(R.id.about_back_button);
        aboutAndroidStudioTV = fragmentView.findViewById(R.id.about_android_studio_tv);
        aboutFirebaseTV = fragmentView.findViewById(R.id.about_firebase_tv);
        aboutSwipeLayoutTV = fragmentView.findViewById(R.id.about_swipe_reveal_layout_tv);
        aboutGithub = fragmentView.findViewById(R.id.about_txt_github);
        quoteSource = fragmentView.findViewById(R.id.about_txt_goodreads);
        deleteAccountButton = fragmentView.findViewById(R.id.profileDeleteAccount);
        deleteAccountButton.setVisibility(View.GONE);
        deleteLayout = fragmentView.findViewById(R.id.delete_account_layout);
        deleteButtonSubmit = fragmentView.findViewById(R.id.delete_account_delete);
        deleteButtonCancel = fragmentView.findViewById(R.id.delete_account_cancel);
        deleteButtonEditText = fragmentView.findViewById(R.id.delete_account_edit_text);
        deleteAccountText = fragmentView.findViewById(R.id.delete_account_message_confirmation);
        privacyPolicy = fragmentView.findViewById(R.id.about_privacy_policy);
        userProfileLayout = fragmentView.findViewById(R.id.user_profile_layout);
        LinearLayoutManager usrManager = new LinearLayoutManager(getActivity());
        usrAdapter = new UserQuoteAdapter(getActivity());
        usrQuotesRV = fragmentView.findViewById(R.id.usrQuotes);
        usrQuotesRV.setAdapter(usrAdapter);
        usrQuotesRV.setLayoutManager(usrManager);
        quoteRepository = new QuoteRepository();
        backButton = fragmentView.findViewById(R.id.profileBackButton);
        // Todo: implement finish() method
        //        backButton.setOnClickListener(view -> finish());
        layoutBackButton = fragmentView.findViewById(R.id.mainBackButton);
        layoutBackButton.setVisibility(View.GONE);
        changeBackground = fragmentView.findViewById(R.id.profileChangeBackground);
        changeBackground.setVisibility(View.GONE);
        changeBackgroundRV = fragmentView.findViewById(R.id.changeBackgroundRecyclerView);
        changeBackgroundRV.setVisibility(View.GONE);
        backgroundRVLinearLayout = fragmentView.findViewById(R.id.changeBackgroundRVLinearLayout);
        backgroundRVLinearLayout.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            String userID = user.getUid();
            new UserRepository().getDatabaseReference()
                    .child(userID)
                    .addListenerForSingleValueEvent(onDataChangeListener.apply(emailTV, usernameTV));
        }

        createSettingsMenu();

        loadUserQuotes(null, FirebaseAuth.getInstance().getCurrentUser().getUid());

        aboutBackButton.setOnClickListener(view -> aboutLayout.setVisibility(View.GONE));

        aboutButton.setOnClickListener(view -> {
            aboutLayout.setVisibility(View.VISIBLE);
        });

        userDataBackButton.setOnClickListener(view -> userDataLayout.setVisibility(View.GONE));

        dataRequestButton.setOnClickListener(view -> {
            userDataLayout.setVisibility(View.VISIBLE);


            if (user != null) {
                String userID = user.getUid();
                new UserRepository().getDatabaseReference()
                        .child(userID)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                StringBuilder bookmarksBuilder = new StringBuilder();
                                StringBuilder preferencesBuilder = new StringBuilder();
                                StringBuilder quotesBuilder = new StringBuilder();
                                StringBuilder usernameBuilder = new StringBuilder();
                                for (DataSnapshot data : snapshot.getChildren()) {
                                    if (data.getValue() instanceof HashMap && Objects.equals(data.getKey(), "Bookmarks")) {
                                        for (DataSnapshot ds : data.getChildren()) {
                                            bookmarksBuilder.append(ds.getKey()).append(": ").append("\n");
                                            for (DataSnapshot ds2 : ds.getChildren()) {
                                                bookmarksBuilder.append(ds2.getKey()).append(": ").append(ds2.getValue()).append("\n").append("\n");
                                            }
                                            bookmarksBuilder.append("\n").append("\n").append("\n");
                                        }
                                    }
                                    if (data.getValue() instanceof HashMap && Objects.equals(data.getKey(), "User Preferences")) {
                                        for (DataSnapshot ds : data.getChildren()) {
                                            preferencesBuilder.append(ds.getKey()).append(": ").append("\n");
                                            for (DataSnapshot ds2 : ds.getChildren()) {
                                                preferencesBuilder.append(ds2.getKey()).append(": ").append(ds2.getValue()).append("\n").append("\n");
                                            }
                                            preferencesBuilder.append("\n").append("\n").append("\n");
                                        }

                                    }
                                    if (data.getValue() instanceof HashMap && Objects.equals(data.getKey(), "User Quotes")) {
                                        for (DataSnapshot ds : data.getChildren()) {
                                            quotesBuilder.append(ds.getKey()).append(": ").append("\n").append(ds.getValue());
                                            quotesBuilder.append("\n").append("\n").append("\n");
                                        }
                                    }
                                    if (data.getValue() instanceof String) {
                                        String s = data.getValue(String.class);
                                        usernameBuilder.append(data.getKey()).append(": ").append("\n")
                                                .append(s).append("\n").append("\n").append("\n");
                                    }
                                }
                                userDataTV.setText(bookmarksBuilder.toString());
                                userDataPrefsTV.setText(preferencesBuilder.toString());
                                userDataQuotesTV.setText(quotesBuilder.toString());
                                userDataUsernameTV.setText(usernameBuilder.toString());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("UserProfile", error.getMessage());
                            }
                        });
            }
        });

        userDataInfoTV.setMovementMethod(LinkMovementMethod.getInstance());
        aboutAndroidStudioTV.setMovementMethod(LinkMovementMethod.getInstance());
        aboutFirebaseTV.setMovementMethod(LinkMovementMethod.getInstance());
        aboutSwipeLayoutTV.setMovementMethod(LinkMovementMethod.getInstance());
        aboutGithub.setMovementMethod(LinkMovementMethod.getInstance());
        quoteSource.setMovementMethod(LinkMovementMethod.getInstance());
        privacyPolicy.setMovementMethod(LinkMovementMethod.getInstance());

        deleteAccountButton.setOnClickListener(view -> {
            userProfileLayout.setVisibility(View.GONE);
            deleteLayout.setVisibility(View.VISIBLE);
            if (user != null) {
                new UsernameRepository(user.getUid()).getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userProfile = snapshot.getValue(String.class);
                        if (userProfile != null) {
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("Please Type \"").append(userProfile).append("\" to Delete Your Account");
                            deleteAccountText.setText(stringBuilder.toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfile", error.getMessage());
                    }
                });
            }
        });

        deleteButtonCancel.setOnClickListener(view -> {
            userProfileLayout.setVisibility(View.VISIBLE);
            deleteLayout.setVisibility(View.GONE);
        });

        deleteButtonSubmit.setOnClickListener(view -> {
            if (user != null) {
                new UsernameRepository(user.getUid()).getDatabaseReference().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String userProfile = snapshot.getValue(String.class);
                        if (userProfile != null) {
                            if (deleteButtonEditText.getText().toString().trim().equals(userProfile)) {

                                new UserRepository().remove(user.getUid()).addOnCompleteListener(delTask -> {
                                    if (delTask.isSuccessful()) {
                                        Toast.makeText(getActivity(), "Successfully Deleted The Account Data", Toast.LENGTH_SHORT).show();
                                        user.delete().addOnCompleteListener(task -> {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(getActivity(), "Successfully Deleted The Account", Toast.LENGTH_SHORT).show();
                                                // Todo: implement finish() method
                                                // finish();
                                                startActivity(new Intent(getActivity() , MainActivity.class));
                                            } else {
                                                Toast.makeText(getActivity(), "Couldn't delete The Account. " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    } else {
                                        Toast.makeText(getActivity(), "Couldn't delete The Account Data. " + Objects.requireNonNull(delTask.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("UserProfile", error.getMessage());
                    }
                });
            }
        });
        
        return fragmentView;
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void createSettingsMenu() {
        final AtomicBoolean isSettingMenuOpen = new AtomicBoolean(false);
        final AtomicBoolean isBackgroundSettingShown = new AtomicBoolean(false);
        profileSettings.setOnClickListener(view -> {
            if (!isSettingMenuOpen.get()) {
                changeBackground.setVisibility(View.VISIBLE);
                changeBackground.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
                dataRequestButton.setVisibility(View.VISIBLE);
                dataRequestButton.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
                aboutButton.setVisibility(View.VISIBLE);
                aboutButton.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
                deleteAccountButton.setVisibility(View.VISIBLE);
                deleteAccountButton.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_bottom_to_top));
                isSettingMenuOpen.set(true);
            } else {
                changeBackground.setVisibility(View.GONE);
                changeBackground.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                if (isBackgroundSettingShown.get()) {
                    backgroundRVLinearLayout.setVisibility(View.GONE);
                    backgroundRVLinearLayout.startAnimation(
                            AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                }
                changeBackgroundRV.setVisibility(View.GONE);
                changeBackgroundRV.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                dataRequestButton.setVisibility(View.GONE);
                dataRequestButton.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                aboutButton.setVisibility(View.GONE);
                aboutButton.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                deleteAccountButton.setVisibility(View.GONE);
                deleteAccountButton.startAnimation(
                        AnimationUtils.loadAnimation(requireActivity().getApplicationContext(), R.anim.settings_button_slide_top_to_bottom));
                isBackgroundSettingShown.set(false);
                isSettingMenuOpen.set(false);
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

        changeBackground.setOnClickListener(view -> {
            if (!isBackgroundSettingShown.get()) {
                backgroundRVLinearLayout.setVisibility(View.VISIBLE);
                changeBackgroundRV.setVisibility(View.VISIBLE);
                isBackgroundSettingShown.set(true);
            } else {
                backgroundRVLinearLayout.setVisibility(View.GONE);
                changeBackgroundRV.setVisibility(View.GONE);
                isBackgroundSettingShown.set(false);
            }
        });
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
}
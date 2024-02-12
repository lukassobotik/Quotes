package com.sforge.quotes.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.sforge.quotes.R;
import com.sforge.quotes.dialog.CollectionsDialog;

public class SearchActivity extends AppCompatActivity implements CollectionsDialog.CollectionsDialogListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
    }

    @Override
    public void getData(int option) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
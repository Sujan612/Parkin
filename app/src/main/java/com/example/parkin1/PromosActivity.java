package com.example.parkin1;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class PromosActivity extends BaseActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_promos);

        Button applyPromoButton = findViewById(R.id.button9);
        applyPromoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });

        // Find the back button
        ImageView backButton = findViewById(R.id.imageView2);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Perform back action
                finish();
            }
        });


    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(PromosActivity.this);
        View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

        EditText promoCodeEditText = bottomSheetView.findViewById(R.id.promoCodeEditText);
        Button applyPromoCodeButton = bottomSheetView.findViewById(R.id.applyPromoCodeButton);

        applyPromoCodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String promoCode = promoCodeEditText.getText().toString().trim();
                // Handle promo code application logic here

                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);

        // Set the custom background
        if (bottomSheetView.getParent() != null) {
            ((ViewGroup) bottomSheetView.getParent()).setBackgroundResource(R.drawable.bottom_sheet_background);
        } else {
            bottomSheetView.setBackgroundResource(R.drawable.bottom_sheet_background);
        }

        bottomSheetDialog.show();
    }

}

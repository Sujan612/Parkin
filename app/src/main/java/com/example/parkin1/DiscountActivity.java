package com.example.parkin1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class DiscountActivity extends BaseActivity {

    private Button shareButton;
    private TextView promoCodeTextView;
    private String promoCode = "######"; // Replace with your actual promo code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discount);

        shareButton = findViewById(R.id.button10);
        promoCodeTextView = findViewById(R.id.textView10);

        promoCodeTextView.setText(promoCode);

        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePromoCode();
            }
        });
    }

    private void sharePromoCode() {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Use this promo code to get a discount: " + promoCode);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }
}

package com.example.water_consumption_project;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the TextView
        TextView welcomeText = findViewById(R.id.welcome_user_text);
        TextView monitoringConsumptionText = findViewById(R.id.monitoring_consumption_text);

        applyTextBold(welcomeText, R.string.welcome_user, 8, 12);
        applyTextBold(monitoringConsumptionText, R.string.monitoring_of_consumption, 14, 25);
    }

    private void applyTextBold(TextView textView, int textResId, int start, int end) {
        SpannableString spannableString = new SpannableString(getString(textResId));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface customTypeface = ResourcesCompat.getFont(this, R.font.raleway_bold);
            if (customTypeface != null) {
                spannableString.setSpan(new TypefaceSpan(customTypeface), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(this, R.color.blue)),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        textView.setText(spannableString);
    }
}
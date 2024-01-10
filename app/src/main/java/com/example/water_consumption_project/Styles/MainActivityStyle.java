package com.example.water_consumption_project.Styles;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Typeface;
import android.os.Build;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.TypefaceSpan;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.water_consumption_project.MainActivity;
import com.example.water_consumption_project.R;

public class MainActivityStyle {

    MainActivity mainActivity;

    public MainActivityStyle(MainActivity mainActivity){
        this.mainActivity = mainActivity;
    }
    public void stylizingApp () {
        TextView welcomeText = mainActivity.findViewById(R.id.welcome_user_text);
        TextView monitoringConsumptionText = mainActivity.findViewById(R.id.monitoring_consumption_text);

        Configuration config = mainActivity.getApplicationContext().getResources().getConfiguration();
        if(config.getLocales().get(0).getLanguage().equals("fr")){
            applyTextBold(welcomeText, R.string.welcome_user, 10, 14);
            applyTextBold(monitoringConsumptionText, R.string.monitoring_of_consumption, 12, 25);
        }else {
            applyTextBold(welcomeText, R.string.welcome_user, 8, 12);
            applyTextBold(monitoringConsumptionText, R.string.monitoring_of_consumption, 14, 25);
        }


    }

    private void applyTextBold(TextView textView, int textResId, int start, int end) {
        SpannableString spannableString = new SpannableString(mainActivity.getString(textResId));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Typeface customTypeface = ResourcesCompat.getFont(mainActivity, R.font.raleway_bold);
            if (customTypeface != null) {
                spannableString.setSpan(new TypefaceSpan(customTypeface), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mainActivity, R.color.blue)),
                        start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        textView.setText(spannableString);
    }

    public void refreshCurrentConsumptionTextAnimation(TextView currentConsumptionText, int current, int target) {
        ValueAnimator animator = ValueAnimator.ofInt(current, target);
        animator.setDuration(1000);

        animator.addUpdateListener(animation -> {
            int animatedValue = (int) animation.getAnimatedValue();
            String newText = String.format(mainActivity.getString(R.string.consumption), animatedValue);
            currentConsumptionText.setText(newText);
        });

        animator.start();
    }
}

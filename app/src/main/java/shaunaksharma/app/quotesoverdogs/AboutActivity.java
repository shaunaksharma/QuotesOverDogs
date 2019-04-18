package shaunaksharma.app.quotesoverdogs;

import android.app.Activity;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import org.w3c.dom.Text;

public class AboutActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Typeface merriweather = Typeface.createFromAsset(getAssets(),  "font/merriweather.ttf");

        final TextView about = findViewById(R.id.about_main);
        final TextView about_one = findViewById(R.id.about_one);
        final TextView about_two = findViewById(R.id.about_two);
        final TextView about_three = findViewById(R.id.about_three);
        final TextView about_four = findViewById(R.id.about_four);
        final TextView about_five = findViewById(R.id.about_five);
        final TextView about_six = findViewById(R.id.about_six);

        about.setTypeface(merriweather);
        about_one.setTypeface(merriweather);
        about_two.setTypeface(merriweather);
        about_three.setTypeface(merriweather);
        about_four.setTypeface(merriweather);
        about_five.setTypeface(merriweather);
        about_six.setTypeface(merriweather);
    }
}

package shaunaksharma.app.quotesoverdogs;

import android.Manifest;
import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.os.AsyncTask;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.acra.ACRA;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    final int REQUEST_WRITE_STORAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Button mainButton = findViewById(R.id.mainbutton);
        Button randomQuote = findViewById(R.id.randomQuote);
        Button randomFont = findViewById(R.id.randomFont);
        Button randomImage = findViewById(R.id.randomImage);

        Typeface amatic = Typeface.createFromAsset(getAssets(),  "font/amaticsc.ttf");
        Typeface courgette = Typeface.createFromAsset(getAssets(),  "font/courgette.ttf");
        Typeface laila = Typeface.createFromAsset(getAssets(),  "font/laila.ttf");
        Typeface merriweather = Typeface.createFromAsset(getAssets(),  "font/merriweather.ttf");
        Typeface dosis = Typeface.createFromAsset(getAssets(),  "font/dosis.ttf");
        Typeface opensans = Typeface.createFromAsset(getAssets(),  "font/opensans.ttf");
        Typeface cinzel = Typeface.createFromAsset(getAssets(),  "font/cinzel.ttf");
        Typeface ptserif = Typeface.createFromAsset(getAssets(),  "font/ptserif.ttf");

        final Typeface[] fonts = {amatic, courgette, laila, merriweather, dosis, opensans, cinzel, ptserif};

        //Initialize retrofit for image API and make the initial request.
        Retrofit.Builder imageBuilder = new Retrofit.Builder()
                .baseUrl("https://random.dog/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retroImage = imageBuilder.build();
        final ImageClient mainImageClient = retroImage.create(ImageClient.class);

        //Initial image setup done.
        requestNewImageLink(mainImageClient);

        //Initialize retrofit for quotes API and make the initial request.
        Retrofit.Builder quotesBuilder = new Retrofit.Builder()
                .baseUrl("https://api.forismatic.com/api/1.0/")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retroQuote = quotesBuilder.build();
        final QuoteClient mainQuoteClient = retroQuote.create(QuoteClient.class);

        //initial quote setup done.
        requestNewQuoteLink(mainQuoteClient);

        //Initial font setup done.
        randomizeFont(fonts);


        //Set listener fot 'Refresh All' button. Randomize image, quote, font.
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestNewImageLink(mainImageClient);
                requestNewQuoteLink(mainQuoteClient);
                randomizeFont(fonts);
            }
        });

        randomQuote.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                requestNewQuoteLink(mainQuoteClient);
            }
        });

        randomImage.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                requestNewImageLink(mainImageClient);
            }
        });

        randomFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomizeFont(fonts);
            }
        });

    }

    //AsyncTask in order to retrieve image from link. Way simpler with Glide, but I wanted to try AsyncTask.
    class setImage extends AsyncTask<String, Void, Bitmap>
    {
        @Override
        protected void onPreExecute()
        {
            ProgressBar progress = findViewById(R.id.progressBar);
            progress.setVisibility(View.VISIBLE);
            ImageView mainImage = findViewById(R.id.mainimage);
            mainImage.setForeground(new ColorDrawable(0x80000000));

            TextView mainText = findViewById(R.id.mainTextView);
            mainText.setForeground(new ColorDrawable(0x80000000));
        }

        @Override
        protected Bitmap doInBackground(String... links)//gets image, converts to bitmap, crops to a square and returns the bitmap
        {
            Bitmap mainViewBMP = null;
            try
            {
                URL link = new URL(links[0]);
                mainViewBMP = BitmapFactory.decodeStream(link.openConnection().getInputStream());
                mainViewBMP = cropToSquare(mainViewBMP);
            }
            catch(MalformedURLException e)
            {
                //Log.d("MalformedURLException", e.getMessage());
                handleBug(e);
            }
            catch(IOException e)
            {
                //Log.d("IOException", e.getMessage());
                handleBug(e);
            }
            return mainViewBMP;
        }

        @Override
        protected void onPostExecute(Bitmap mainviewbmp)//image bitmap is ready, set image
        {
            ProgressBar progress = findViewById(R.id.progressBar);
            progress.setVisibility(View.INVISIBLE);
            ImageView mainImage = findViewById(R.id.mainimage);
            mainImage.setImageBitmap(mainviewbmp);
            mainImage.setForeground(null);

            mainImage.setDrawingCacheEnabled(true);
            Palette main_palette = Palette.from(mainImage.getDrawingCache()).generate();//palette generated, ready to examine for colors
            mainImage.setDrawingCacheEnabled(false);

            int darkvibe = main_palette.getDarkMutedColor(99);
            if(darkvibe == 99) { darkvibe = main_palette.getDarkVibrantColor(99); }
            if(darkvibe == 99) { darkvibe = main_palette.getDominantColor(99); }//failsafe method since DarkMuted and DarkVibrant are not always available

            TextView mainText = findViewById(R.id.mainTextView);
            mainText.setBackgroundColor(darkvibe);//set the color on quote area background
            mainText.setForeground(null);

            transitionBackground(darkvibe);

            TextView watermark = findViewById(R.id.watermark);
            watermark.setBackgroundColor(darkvibe);//set the color on watermark background(watermark remains hidden unless image is exported)
        }
    }

    public Bitmap cropToSquare(Bitmap bitmap)//crops bitmaps to squares
    {
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();

        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;

        int cropW = (width - height) / 2;
        cropW = (cropW < 0)? 0: cropW;
        int cropH = (height - width) / 2;
        cropH = (cropH < 0)? 0: cropH;

        Bitmap cropImg = Bitmap.createBitmap(bitmap, cropW, cropH, newWidth, newHeight);
        return cropImg;
    }

    public void requestNewImageLink(final ImageClient inputImageClient)//makes a request for a new image link, then calls setImage on the link
    {
        Call<ImageResponseContent> call = inputImageClient.getImage();
        call.enqueue(new Callback<ImageResponseContent>() {
            @Override
            public void onResponse(Call<ImageResponseContent> call, Response<ImageResponseContent> response) {
                String imageUrl = response.body().getUrl();
                if(!(imageUrl.substring(imageUrl.length() - 3).equals("jpg")))//make sure link is for an image and not a gif/mp4 video.
                {
                    requestNewImageLink(inputImageClient);
                }
                else
                {
                    new setImage().execute(imageUrl);//set the image using the link
                }
            }

            @Override
            public void onFailure(Call<ImageResponseContent> call, Throwable t) {
                //Log.d("ImageLinkBug", t.getMessage());
                handleBug(new Exception(t));
            }
        });
    }

    public void requestNewQuoteLink(final QuoteClient inputQuoteClient)//requests a new quote and sets the quote in the textView.
    {
        Call<QuoteResponseContent> call = inputQuoteClient.getQuote();
        call.enqueue(new Callback<QuoteResponseContent>() {
            @Override
            public void onResponse(Call<QuoteResponseContent> call, Response<QuoteResponseContent> response) {
                TextView mainText = findViewById(R.id.mainTextView);
                if(response.body().getQuoteAuthor().equals("Joseph Stalin") || response.body().getQuoteAuthor().equals("Donald Trump"))
                {
                    requestNewQuoteLink(inputQuoteClient);
                    return;
                }
                mainText.setText(response.body().getQuoteText() + "\n - " + response.body().getQuoteAuthor());
            }

            @Override
            public void onFailure(Call<QuoteResponseContent> call, Throwable t) {
                //Log.d("QuoteLinkBug", t.getMessage());
                handleBug(new Exception(t));
            }
        });
    }

    public void randomizeFont(Typeface[] fontarr)//sets a random font for the quote
    {
        Random rand = new Random();
        int n = rand.nextInt(8);
        TextView mainText = findViewById(R.id.mainTextView);
        mainText.setTypeface(fontarr[n]);
    }

    public void handleBug(Exception e)//Simple bug handler. Allows user to send a bug report through email.
    {
        new AlertDialog.Builder(MainActivity.this)//inform the user that an error has occurred
                .setTitle("Oops!")
                .setMessage("Something went wrong. Send crash report through email? (Don't worry, the report will be created automatically!)")

                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ACRA.getErrorReporter().handleSilentException(e);
                    }
                })
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_export) {//set up export

            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                //no permission to write to external storage, request the user for permission
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_STORAGE);
            }
            else
            {
                //permission has already been granted, export the image
                exportImage();
            }
            return true;
        }

        if (id == R.id.action_about) {
            //open about activity
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_WRITE_STORAGE: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //as of now, this is only called when user exports image, hence upon receiving permission, export image
                    exportImage();

                } else {
                    //inform the user that permission is needed for export through a Toast.
                    Toast.makeText(MainActivity.this, "Could not export image without storage permissions.", Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }

    public void exportImage()
    {
        final ImageView mainImage = findViewById(R.id.mainimage);
        final ConstraintLayout mainContent = findViewById(R.id.contentLayout);
        final TextView watermark = findViewById(R.id.watermark);
        watermark.setAlpha(1f);//make watermark visible before exporting the image

        mainContent.setDrawingCacheEnabled(true);
        mainContent.buildDrawingCache();
        mainImage.setDrawingCacheEnabled(true);
        mainImage.buildDrawingCache();

        Bitmap exportBMP = Bitmap.createBitmap(mainContent.getWidth(), mainContent.getHeight(), Bitmap.Config.ARGB_8888);//create the bitmap of the correct size
        Canvas canvas = new Canvas(exportBMP);
        mainContent.draw(canvas);//draw everything from the parent view (that holds the image, quote, and watermark) into the exportBMP bitmap

        //Dynamic filename setup. Filename wil be generated with date and time.
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);


        File mainFolder = new File(Environment.getExternalStorageDirectory() + "/QuotesOverDogs");//the directory in which the images shall be stored
        File expimage = new File(mainFolder.getAbsolutePath()+"/QuotesOverDogs" + day+"_"+month+"_"+year+"_"+hour+"_"+minute+"_"+second+".png");//set dynamic image name

        if(!mainFolder.exists()){mainFolder.mkdir();}//if the directory QuotesOverDogs does not exist, create one

        try {
            if(!expimage.exists())
            {
                expimage.createNewFile();//create the image file
            }
        }
        catch(IOException e){
            //Log.d("IOException2", e.getMessage());
            handleBug(e);
        }

        try
        {
            FileOutputStream output = new FileOutputStream(expimage);
            exportBMP.compress(Bitmap.CompressFormat.PNG, 100, output);//convert the exportBMP to a png file
            output.close();
        }
        catch(IOException e)
        {
            //Log.d("IOException3", e.getMessage());
            handleBug(e);
        }
        watermark.setAlpha(0f);//set watermark to invisible again

        mainContent.setDrawingCacheEnabled(false);
        mainContent.destroyDrawingCache();
        mainImage.setDrawingCacheEnabled(false);
        mainImage.destroyDrawingCache();

        Toast.makeText(MainActivity.this, "Image exported to folder QuotesOverDogs", Toast.LENGTH_LONG).show();
    }

    public void transitionBackground(int colorTo)
    {
        ConstraintLayout topLayout = findViewById(R.id.topLayout);
        LinearLayout buttonLayout = findViewById(R.id.buttonLayout);

        int currColor = Color.TRANSPARENT;
        Drawable background = topLayout.getBackground();
        currColor = ((ColorDrawable) background).getColor();

        String hexColor = String.format("#%06X", (0xFFFFFF & colorTo));
        String hexColorNew = "#66"+hexColor.substring(1);
        int colorToFinal = Color.parseColor(hexColorNew);

        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), currColor, colorToFinal);
        colorAnimation.setDuration(500); // milliseconds

        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                topLayout.setBackgroundColor((int) animator.getAnimatedValue());
            }

        });

        colorAnimation.start();
    }
}
package com.rishat.developerlife;

import static java.lang.Math.max;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import java.util.List;


public class MainActivity extends AppCompatActivity {


    private List<GifItem> storedGif = new ArrayList<GifItem>();
    private ImageView gif;
    private Button prev;
    private Button next;
    static final String url = "https://developerslife.ru/random?json=true";
    private RequestQueue requestQueue;
    private TextView gifDescriptionView;
    private int curGif;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gif = (ImageView) findViewById(R.id.gifView);
        prev = (Button) findViewById(R.id.prevBtn);
        next = (Button) findViewById(R.id.nextBnt);
        gifDescriptionView = (TextView) findViewById(R.id.gif_description);

        requestQueue = Volley.newRequestQueue(this);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (curGif == storedGif.size()) {
                    getGif(getApplicationContext());

                } else {
                    curGif++;
                    showStoredGif(getApplicationContext(), storedGif.get(curGif-1));

                }
                if (!prev.isClickable() && curGif >1)
                    prev.setClickable(true);

            }
        });

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                curGif--;
                showStoredGif(getApplicationContext(), storedGif.get(curGif-1));
                if (curGif == 1)
                    prev.setClickable(false);
            }
        });

        prev.setClickable(false);
        curGif = 0;
        getGif(this);
        curGif++;
    }


    private void getGif(Context context) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    String gifUrl = response.getString("gifURL");
                    String gifDescription = response.getString("description");

                    GifItem gifItem = new GifItem(gifUrl, gifDescription);

                    AnimationDrawable animPlaceHolder = (AnimationDrawable) context.getDrawable(R.drawable.loading_animation);
                    animPlaceHolder.start();

                    storedGif.add(gifItem);
                    curGif =storedGif.size();
                    gifDescriptionView.setText(R.string.loading);
                    Glide.with(context)
                            .load(gifItem.getUrl())
                            .asGif()
                            .placeholder(animPlaceHolder)
                            .error(R.drawable.icon_error)
                            .listener(
                                    new RequestListener<String, GifDrawable>() {
                                        @Override
                                        public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                            gifDescriptionView.setText(R.string.bad_connection);
                                            return false;
                                        }

                                        @Override
                                        public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                            gifDescriptionView.setText(gifItem.getDescription());
                                            return false;
                                        }
                                    }
                            )
                            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                            .into(gif);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                gifDescriptionView.setText(R.string.bad_connection);
                gif.setImageResource(R.drawable.icon_error);
                error.printStackTrace();
            }
        });
        requestQueue.add(request);
    }

    private void showStoredGif(Context context, GifItem gifItem) {
        AnimationDrawable animPlaceHolder = (AnimationDrawable) context.getDrawable(R.drawable.loading_animation);
        animPlaceHolder.start();
        gifDescriptionView.setText(R.string.loading);
        Glide.with(context)
                .load(gifItem.getUrl())
                .asGif()
                .placeholder(animPlaceHolder)
                .error(R.drawable.icon_error)
                .listener(
                        new RequestListener<String, GifDrawable>() {
                            @Override
                            public boolean onException(Exception e, String model, Target<GifDrawable> target, boolean isFirstResource) {
                                gifDescriptionView.setText(R.string.bad_connection);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GifDrawable resource, String model, Target<GifDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                gifDescriptionView.setText(gifItem.getDescription());
                                return false;
                            }
                        }
                )
                .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                .into(gif);
    }

}
package techgravy.sunshine.utils;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.view.Window;
import android.widget.ImageView;

import com.squareup.picasso.Callback;

import techgravy.sunshine.R;
import timber.log.Timber;

/**
 * Created by aditlal on 26/03/16.
 */
public class GetPalette
        implements AnimatorUpdateListener, Callback, Palette.PaletteAsyncListener {
    private int mColorFrom;
    private int mPrimaryDarkColor;
    private Context mContext;
    private int statusBarColor;
    private Window window;
    ImageView imageView;

    public GetPalette(Context context, ImageView imageView, Window window) {
        this.mContext = context;
        this.window = window;
        this.imageView = imageView;
        mColorFrom = ContextCompat.getColor(context, android.R.color.white);
        mPrimaryDarkColor = ContextCompat.getColor(context, R.color.primary_dark);
    }

    @Override
    public void onAnimationUpdate(ValueAnimator animation) {
    }

    @Override
    public void onGenerated(Palette palette) {
        Palette.Swatch vibrant = palette.getDarkVibrantSwatch();
        ValueAnimator colorAnimation;

        if (vibrant != null) {
            statusBarColor = vibrant.getRgb();
        } else {
            int colorTo = palette.getDarkMutedColor(mPrimaryDarkColor);
            statusBarColor = colorTo;
        }
        Timber.tag("GetPalette").d("Palette success " + statusBarColor);

        CommonUtils.setTranslucentStatusBar(window, statusBarColor);
    }

    @Override
    public void onSuccess() {
        Timber.tag("GetPalette").d("Picasso success");
        Palette.from(((BitmapDrawable) imageView.getDrawable()).getBitmap()).generate(this);
    }

    @Override
    public void onError() {
        Timber.tag("GetPalette").e("Picasso error");
    }
}

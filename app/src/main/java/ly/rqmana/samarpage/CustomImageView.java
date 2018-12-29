package ly.rqmana.samarpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.DrawableRes;
import android.support.v7.widget.AppCompatImageView;

public class CustomImageView extends CustomView<AppCompatImageView> {

    public CustomImageView(Context context) {
        super(context, new AppCompatImageView(context));
    }

    public void setImageResource(@DrawableRes int resId) {
        mainView.setImageResource(resId);
    }

    @Override
    public CustomView copy() {
        Bitmap bitmap = ((BitmapDrawable) mainView.getDrawable()).getBitmap();
        CustomImageView copy = new CustomImageView(this.getContext());
        copy.mainView.setImageBitmap(bitmap);
        copy.setCode(getCode());
        return copy;
    }
}

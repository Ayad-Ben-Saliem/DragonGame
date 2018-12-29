package ly.rqmana.samarpage;

import android.content.Context;
import android.support.annotation.StringRes;
import android.support.v7.widget.AppCompatTextView;
import android.util.TypedValue;
import android.view.Gravity;

public class CustomTextView extends CustomView<AppCompatTextView> {

    public CustomTextView(Context context) {
        super(context, new AppCompatTextView(context));
    }

    @Override
    protected void setupMainView() {
        super.setupMainView();

        mainView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 32);
        mainView.setGravity(Gravity.CENTER);
    }

    public final void setText(@StringRes int resId) {
        mainView.setText(resId);
    }

    @Override
    public CustomView copy() {
        CustomTextView copy = new CustomTextView(getContext());
        copy.mainView.setText(mainView.getText());
        copy.setCode(getCode());
        return copy;
    }
}

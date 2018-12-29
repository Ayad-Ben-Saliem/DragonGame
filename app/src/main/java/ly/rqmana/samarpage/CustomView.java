package ly.rqmana.samarpage;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.View;
import android.view.ViewGroup;

public abstract class CustomView<T extends View> extends LinearLayoutCompat {

    private String code;

    private OnDrawCompleteListener onDrawCompleteListener;

    protected final int itemWidth = (int) getResources().getDimension(R.dimen.item_width);
    protected final int itemHeight = (int) getResources().getDimension(R.dimen.item_height);

    protected final View upperSplitter;
    protected final T mainView;
    protected final View lowerSplitter;

    public CustomView(Context context, T mainView) {
        super(context, null);

        this.mainView = mainView;
        upperSplitter = new View(context);
        lowerSplitter = new View(context);

        setupMainView();
        setupSplitters();

        setOrientation(VERTICAL);
        setLayoutParams(new LinearLayoutCompat.LayoutParams(itemWidth, itemHeight));

        addView(upperSplitter);
        addView(mainView);
        addView(lowerSplitter);

        setWillNotDraw(false);
    }

    protected void setupMainView() {
        mainView.setLayoutParams(
                new LinearLayoutCompat.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT, 0, 1));
    }

    private void setupSplitters() {
        int height = (int) getResources().getDimension(R.dimen.splitter_height);

        upperSplitter.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        upperSplitter.setBackgroundColor(Color.GREEN);
        upperSplitter.setVisibility(INVISIBLE);

        lowerSplitter.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height));
        lowerSplitter.setBackgroundColor(Color.GREEN);
        lowerSplitter.setVisibility(INVISIBLE);
    }


    public abstract CustomView copy();

    public void displayUpperSplitter() {
        upperSplitter.setVisibility(VISIBLE);
        lowerSplitter.setVisibility(INVISIBLE);
    }

    public void displayLowerSplitter() {
        upperSplitter.setVisibility(INVISIBLE);
        lowerSplitter.setVisibility(VISIBLE);
    }

    public void hideSplitters() {
        upperSplitter.setVisibility(INVISIBLE);
        lowerSplitter.setVisibility(INVISIBLE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(onDrawCompleteListener != null){
            onDrawCompleteListener.onComplete(this);
        }
    }

    public void setOnDrawCompleteListener(OnDrawCompleteListener onDrawCompleteListener) {
        this.onDrawCompleteListener = onDrawCompleteListener;
    }

    public interface OnDrawCompleteListener{
        void onComplete(CustomView view);
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}


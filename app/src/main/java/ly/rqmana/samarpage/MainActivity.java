package ly.rqmana.samarpage;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageButton;
import android.support.v7.widget.LinearLayoutCompat;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private LinearLayoutCompat imagesContainer;
    private LinearLayoutCompat namesContainer;
    private AppCompatImageButton checkBtn;

    private ScrollView scrollView;

    protected int itemWidth;
    protected int itemHeight;

    private final LinkedList<CustomImageView> imagesList = new LinkedList<>();
    private final LinkedList<CustomTextView> namesList = new LinkedList<>();

    private CustomView draggingView;

    Integer[] images;
    Integer[] names;
    String[] labels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imagesContainer = findViewById(R.id.imagesContainer);
        namesContainer = findViewById(R.id.namesContainer);

        scrollView = findViewById(R.id.scrollView);

        checkBtn = findViewById(R.id.checkBtn);

        itemWidth = (int) getResources().getDimension(R.dimen.item_width);
        itemHeight = (int) getResources().getDimension(R.dimen.item_height);

        images = new Integer[]{R.drawable.bicycle, R.drawable.brush, R.drawable.burj, R.drawable.comb, R.drawable.axe, R.drawable.cup};
        names = new Integer[]{R.string.bicycle,    R.string.brush,   R.string.burj ,  R.string.comb,   R.string.axe,   R.string.cup};
        labels = new String[names.length];
        for (int i = 0; i < names.length; i++) {
            labels[i] = getString(names[i]);
        }

        List<Integer> randomNameIndexes = generateRandomNumbers(names.length);
        List<Integer> randomImageIndexes = generateRandomNumbers(images.length);

        for (int i = 0; i < names.length; i++) {
            int imageResId = images[randomImageIndexes.get(i)];
            int nameResId = names[randomNameIndexes.get(i)];
            String imageCode = labels[randomImageIndexes.get(i)];
            String nameCode = labels[randomNameIndexes.get(i)];
            addItem(imageResId, nameResId, imageCode, nameCode);
        }

        findViewById(R.id.rootLayout).setOnDragListener((view, event) -> {
            if (event.getAction() == DragEvent.ACTION_DRAG_ENDED)
                resetSplitters();
            return true;
        });

        scrollView.setOnDragListener((view, event) -> {
            if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                if ((event.getY() + draggingView.getHeight() / 2) > (view.getY() + view.getHeight() / 2)) {
                    scrollView.scrollBy(0, 10);
                } else if ((event.getY() - draggingView.getHeight() / 2) > (view.getY() - view.getHeight() / 2)) {
                    scrollView.scrollBy(0, -10);
                }
            }
            return true;
        });
    }

    private List<Integer> generateRandomNumbers(int bound) {
        List<Integer> result = new ArrayList<>();

        int index;
        Random random = new Random();
        while (result.size() < bound) {
            index = random.nextInt(bound);
            if (!result.contains(index))
                result.add(index);
        }
        return result;
    }

    private void resetSplitters() {
        for (CustomView view : imagesList) {
            view.hideSplitters();
        }
        for (CustomView view : namesList) {
            view.hideSplitters();
        }
    }

    private void addItem(int imageResId, int nameResId, String imageCode, String nameCode) {
        CustomImageView imageItem = new CustomImageView(this);
        imageItem.setImageResource(imageResId);

        imagesList.add(imageItem);
        imagesContainer.addView(imageItem);

        imageItem.setOnTouchListener(this::startDragging);
        imageItem.setOnDragListener(this::onDragListener);

        imageItem.setCode(imageCode);

        CustomTextView nameItem = new CustomTextView(this);
        nameItem.setText(nameResId);

        namesList.add(nameItem);
        namesContainer.addView(nameItem);

        nameItem.setOnTouchListener(this::startDragging);
        nameItem.setOnDragListener(this::onDragListener);

        nameItem.setCode(nameCode);
    }

    private void scaleIn(View view) {
        ValueAnimator animator = ValueAnimator.ofInt(view.getHeight(), itemHeight);
        animator.addUpdateListener(valueAnimator -> view.getLayoutParams().height = (Integer) valueAnimator.getAnimatedValue());
        animator.setDuration(500);
        animator.start();
    }

    private void scaleOut(View view) {
        ValueAnimator animator = ValueAnimator.ofInt(view.getHeight(), 0);
        animator.addUpdateListener(valueAnimator -> {
            int val = (Integer) valueAnimator.getAnimatedValue();
            ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
            layoutParams.height = val;
            view.setLayoutParams(layoutParams);
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                if (view instanceof CustomImageView) {
                    int index = imagesList.indexOf(view);
                    imagesList.remove(index);
                    imagesContainer.removeViewAt(index);
                } else if (view instanceof CustomTextView) {
                    int index = namesList.indexOf(view);
                    namesList.remove(index);
                    namesContainer.removeViewAt(index);
                }

            }
        });
        animator.setDuration(500);
        animator.start();

    }

    private boolean startDragging(View view, MotionEvent event) {
        draggingView = (CustomView)view;
        view.startDrag(null, new View.DragShadowBuilder(view), null, 0);
        return false;
    }

    private boolean onDragListener(View view, DragEvent event) {

        CustomView targetView = (CustomView) view;

        if (targetView == draggingView)
            return true;

        if (targetView.getClass().equals(draggingView.getClass())) {
            int i = -1;
            if (targetView instanceof CustomImageView) {
                i = imagesList.indexOf(view);
            } else if (targetView instanceof CustomTextView) {
                i = namesList.indexOf(view);
            }

            if (event.getAction() == DragEvent.ACTION_DRAG_LOCATION) {
                if (event.getY() < (targetView.getHeight() / 2)) {
                    targetView.displayUpperSplitter();
                } else {
                    targetView.displayLowerSplitter();
                }
            } else if (event.getAction() == DragEvent.ACTION_DRAG_EXITED) {
                targetView.hideSplitters();
            } else if (event.getAction() == DragEvent.ACTION_DROP) {
                if (event.getY() < (targetView.getHeight() / 2)) {
                    insertItem(i);            // add item upper
                } else {
                    insertItem(i + 1);  // add item lower
                }
                targetView.hideSplitters();
            }
        }
        return true;
    }

    private void insertItem(int index) {

        CustomView view = draggingView.copy();

        view.setLayoutParams(new LinearLayoutCompat.LayoutParams(itemWidth, 0));
        view.setOnTouchListener(this::startDragging);
        view.setOnDragListener(this::onDragListener);

        if (draggingView instanceof CustomImageView) {
            imagesList.add(index, (CustomImageView) view);
            imagesContainer.addView(view, index);
        } else if (draggingView instanceof CustomTextView){
            namesList.add(index, (CustomTextView) view);
            namesContainer.addView(view, index);
        }

        scaleOut(draggingView);
        scaleIn(view);
    }

    public void onCheckBtnClicked(View view) {

        MediaPlayer mediaPlayer;

        boolean isWrong = false;
        for (int i = 0; i < imagesContainer.getChildCount(); i++) {
            String imageCode = ((CustomView<View>)imagesContainer.getChildAt(i)).getCode();
            String nameCode = ((CustomView<View>)namesContainer.getChildAt(i)).getCode();

            if (imageCode.equals(nameCode)) {
                imagesContainer.getChildAt(i).setBackgroundColor(0x00000000);
                namesContainer.getChildAt(i).setBackgroundColor(0x00000000);
                // TODO: play sound
//                mediaPlayer = MediaPlayer.create(ConnectImagesWithWords.this, R.raw.welldone);
//                mediaPlayer.start();
            } else {
                imagesContainer.getChildAt(i).setBackgroundColor(0x00ff8800);
                namesContainer.getChildAt(i).setBackgroundColor(0x00ff8800);
                checkBtn.setImageResource(R.drawable.falseclear);
                isWrong = true;
                // TODO: play sound
//                mediaPlayer = MediaPlayer.create(ConnectImagesWithWords.this,R.raw.tryagain);
//                mediaPlayer.start();
            }
        }
        if (!isWrong) {
            checkBtn.setImageResource(R.drawable.truecheck);
        }
    }

//    public void onLeftClicked(View view) {
//        Intent intent = new Intent(this,Words.class);
//        startActivity(intent);
//    }
}
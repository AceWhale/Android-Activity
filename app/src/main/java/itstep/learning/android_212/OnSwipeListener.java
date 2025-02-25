package itstep.learning.android_212;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class OnSwipeListener implements View.OnTouchListener {
    public void onSwipeBottom() { }   // Методи для перевантаження
    public void onSwipeLeft()   { }   // в активностях, що потребують
    public void onSwipeRight()  { }   // управління свайпами
    public void onSwipeTop()    { }   //
    // Загальна задача - аналізуючи жести викликати (або не викликати) один з методів

    private final GestureDetector gestureDetector;

    public OnSwipeListener( Context context ) {
        gestureDetector = new GestureDetector( context, new GestureListener() ) ;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch( View view, MotionEvent motionEvent ) {
        return gestureDetector.onTouchEvent( motionEvent );
    }


    private final class GestureListener extends GestureDetector.SimpleOnGestureListener {
        private static final int minSwipeDistance = 50;
        private static final int minSwipeVelocity = 50;

        @Override
        public boolean onDown(@NonNull MotionEvent e) {
            return true;
        }

        @Override
        public boolean onFling(@Nullable MotionEvent e1, @NonNull MotionEvent e2,
                               float velocityX, float velocityY) {
            boolean isServed = false;
            try {
                assert e1 != null;
                float dx = e2.getX() - e1.getX();
                float dy = e2.getY() - e1.getY();
                float mx = Math.abs(dx);   // довжини проведення по
                float my = Math.abs(dy);   // осях Х та Y
                // Детальна задача: перевірити якого типу Fling - горизонтальний
                // чи вертикальний, а також чи достатньо швидким і достатньо довгим є
                // проведення
                if( mx > 2 * my ) {   // визначаємо як горизонтальний
                    if( mx >= minSwipeDistance && velocityX >= minSwipeVelocity ) {
                        if( dx > 0 ) onSwipeRight();
                        else onSwipeLeft();
                        isServed = true;
                    }
                }
                else if( my > 2 * mx ) {   // визначаємо як вертикальний
                    if( my >= minSwipeDistance && velocityY >= minSwipeVelocity ) {
                        if( dy > 0 ) onSwipeBottom();
                        else onSwipeTop();
                        isServed = true;
                    }
                }
                // else - ігноруємо, не вважаємо за свайп
            }
            catch (Exception ignored) {}
            return isServed;
        }
    }
}
/*
Детектор жестів. Свайпи.
Детектор жестів - аналог маніпуляторів (на кшталт "миші") для ПК.
Але він значно відрізняється системою подій.
Свайпи не є базовими жестами, тому для них створюють власний детектор.
Свайп - послідовність двох подій: Down (торкання детектора) та Fling (проведення)
 */

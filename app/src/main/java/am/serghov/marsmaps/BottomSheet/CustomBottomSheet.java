package am.serghov.marsmaps.BottomSheet;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;

import com.flipboard.bottomsheet.BottomSheetLayout;

import java.lang.reflect.Field;

/**
 * Created by serghov on 4/20/2016.
 */
public class CustomBottomSheet extends BottomSheetLayout{
    public CustomBottomSheet(Context context) {
        super(context);
    }

    public CustomBottomSheet(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CustomBottomSheet(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CustomBottomSheet(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public float getBottomSheetTranslation() {
        float sheetTranslation = 0;

        try {
            Field field = BottomSheetLayout.class.getDeclaredField("sheetTranslation");
            field.setAccessible(true);
            sheetTranslation = (float) field.get(this);
            field.setAccessible(false);
        } catch(NoSuchFieldException e){

        } catch (IllegalAccessException e) {

        }
        return sheetTranslation;
    }
}

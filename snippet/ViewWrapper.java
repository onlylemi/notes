import android.view.View;

/**
 * ViewWrapper
 * 
 * 装饰 view，给 view 赋予setWidth、getWidth、setHeight、getHeight等方法，
 * 供属性动画中使用改变 view 的 width、height
 *
 * @author: onlylemi
 */
public class ViewWrapper {

    private View view;

    private ViewWrapper(View view) {
        this.view = view;
    }

    /**
     * 装饰 view
     *
     * @param view
     * @return
     */
    public static ViewWrapper decorator(View view) {
        return new ViewWrapper(view);
    }

    public int getHeight() {
        return view.getLayoutParams().height;
    }

    public void setHeight(int height) {
        view.getLayoutParams().height = height;
        view.requestLayout();
    }

    public int getWidth() {
        return view.getLayoutParams().width;
    }

    public void setWidth(int width) {
        view.getLayoutParams().width = width;
        view.requestLayout();
    }
}

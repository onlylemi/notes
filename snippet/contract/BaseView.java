/**
 * BaseView
 *
 * @author: onlylemi
 */
public interface BaseView<T> {

    /**
     * 为 view 设置一个对应的 presenter
     *
     * @param presenter
     */
    void setPresenter(T presenter);
}

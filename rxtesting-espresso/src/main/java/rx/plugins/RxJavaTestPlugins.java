package rx.plugins;

/**
 * Allows to access reset in RxJavaPlugins instance
 */
public class RxJavaTestPlugins extends RxJavaPlugins {

    RxJavaTestPlugins() {
        super();
    }

    public static void resetPlugins(){
        getInstance().reset();
    }
}

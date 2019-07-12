package top.andnux.api;

public interface NavigationListener {

    void onSuccess();

    void onError(NavigationException e);
}

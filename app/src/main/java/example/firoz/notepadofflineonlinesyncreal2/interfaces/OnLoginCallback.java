package example.firoz.notepadofflineonlinesyncreal2.interfaces;

import org.json.JSONObject;

public interface OnLoginCallback {

    void onLoginSuccess(JSONObject credentials);
    void onLoginError(String error);
    void onNetworkError();

}

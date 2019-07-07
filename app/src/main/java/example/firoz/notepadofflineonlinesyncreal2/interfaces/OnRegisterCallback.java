package example.firoz.notepadofflineonlinesyncreal2.interfaces;

import org.json.JSONObject;

public interface OnRegisterCallback {

    void onRegisterSuccess(JSONObject response);
    void onRegisterError(String error);
    void onNetworkError();

}

package example.firoz.notepadofflineonlinesyncreal2.data;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import example.firoz.notepadofflineonlinesyncreal2.interfaces.OnLoginCallback;
import example.firoz.notepadofflineonlinesyncreal2.interfaces.OnRegisterCallback;

public class CustomRequest {

    public StringRequest login(String url, final OnLoginCallback onLoginCallback, final String email, final String password) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("Login Response: ",response);

                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if(!error)
                    {
                        onLoginCallback.onLoginSuccess(jObj);
                    }
                    else
                    {
                        onLoginCallback.onLoginError(jObj.getString("error_msg"));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    onLoginCallback.onLoginError("Invalid input");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.e("Login Error: ",error.getMessage());
                if (error instanceof NetworkError) {
                    onLoginCallback.onNetworkError();
                } else {
                    if (error != null && error.getMessage() != null) {
                        onLoginCallback.onLoginError(error.getMessage());
                    } else {
                        onLoginCallback.onLoginError("");
                    }
                }
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                super.getParams();
                HashMap<String, String> parameter = new HashMap<>();
                parameter.put("email", email);
                parameter.put("password", password);
                Log.e("sending login request",parameter.toString());
                return parameter;


            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return stringRequest;
    }

    public StringRequest register(String url, final OnRegisterCallback onRegisterCallback, final String name, final String email, final String password)
    {

        StringRequest strReq= new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Registration response", "Register Response: " + response.toString());

                try
                {
                    JSONObject jObj = new JSONObject(response);
                    boolean error = jObj.getBoolean("error");

                    if(!error)
                    {

                        onRegisterCallback.onRegisterSuccess(jObj);
                    }
                    else
                    {
                        // Error occurred in registration. Get the error
                        // message
                        String errorMsg = jObj.getString("error_msg");
                        onRegisterCallback.onRegisterError(errorMsg);

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    onRegisterCallback.onRegisterError("Invalid Input");
                }




            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Register Error response", "Registration Error: " + error.getMessage());
                if (error instanceof NetworkError) {
                    onRegisterCallback.onNetworkError();
                } else {
                    if (error != null && error.getMessage() != null) {
                        onRegisterCallback.onRegisterError(error.getMessage());
                    } else {
                        onRegisterCallback.onRegisterError("");
                    }
                }

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                // Posting params to register url
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("email", email);
                params.put("password", password);

                return params;
            }
        };

        strReq.setRetryPolicy(new DefaultRetryPolicy(10000, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        return strReq;
    }

}

package example.firoz.notepadofflineonlinesyncreal2.data;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class ServiceRequest {
    private static ServiceRequest mInstance;
    private RequestQueue mRequestQueue;
    private static Context mCtx;

    public ServiceRequest(Context context) {
        this.mCtx = context;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public static synchronized ServiceRequest getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ServiceRequest(context);
        }
        return mInstance;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}

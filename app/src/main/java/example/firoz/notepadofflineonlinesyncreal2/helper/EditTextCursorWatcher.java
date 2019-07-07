package example.firoz.notepadofflineonlinesyncreal2.helper;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import example.firoz.notepadofflineonlinesyncreal2.activity.AddActivity;
import example.firoz.notepadofflineonlinesyncreal2.activity.MainActivity;
import example.firoz.notepadofflineonlinesyncreal2.interfaces.OnTextSelectionCallback;

public class EditTextCursorWatcher extends EditText {

    public EditTextCursorWatcher(Context context, AttributeSet attrs,
                                 int defStyle) {
        super(context, attrs, defStyle);

    }

    public EditTextCursorWatcher(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    public EditTextCursorWatcher(Context context) {
        super(context);

    }


    @Override
    protected void onSelectionChanged(int selStart, int selEnd) {
        Log.d("OnSelect", "selStart is " + selStart + "selEnd is " + selEnd);
        //Toast.makeText(getContext(), "selStart is " + selStart + "selEnd is " + selEnd, Toast.LENGTH_LONG).show();
        /*for(ISelectionChangedListener listener : selectionChangedListeners){
            listener.selectionChanged("...");
        }*/
        //super.onSelectionChanged(selStart, selEnd);
        getContext().sendBroadcast(new Intent(AddActivity.SELECTION_CHANGE_BROADCAST));
    }

    private ArrayList<ISelectionChangedListener> selectionChangedListeners = new ArrayList<>();

    public void addOnSelectionChangedListener(ISelectionChangedListener listener){
        selectionChangedListeners.add(listener);
    }

    public interface ISelectionChangedListener{
        void selectionChanged(String newSelection);
    }

}

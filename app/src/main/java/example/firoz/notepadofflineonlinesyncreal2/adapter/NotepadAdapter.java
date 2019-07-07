package example.firoz.notepadofflineonlinesyncreal2.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import example.firoz.notepadofflineonlinesyncreal2.R;
import example.firoz.notepadofflineonlinesyncreal2.interfaces.OnLongClickCallback;
import example.firoz.notepadofflineonlinesyncreal2.model.Notes;

public class NotepadAdapter extends RecyclerView.Adapter {

    //context object
    private Context context;
    //storing all the names in the list
    private List<Notes> notes;
    private OnLongClickCallback onLongClickCallback;

    public NotepadAdapter(Context context, List<Notes> notes, OnLongClickCallback onLongClickCallback) {
        this.context = context;
        this.notes = notes;
        this.onLongClickCallback= onLongClickCallback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.names, parent,false);
        return new NotepadHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Notes note = notes.get(position);
        NotepadHolder holder = (NotepadHolder) viewHolder;

        //setting the name to textview
        //holder.textViewName.setText(note.getNotes());

        //convert html tags to spannable
        Spanned stringBuilder = Html.fromHtml(note.getNotes());
        holder.textViewName.setText(stringBuilder);
        holder.textViewCreateDate.setText(note.getCreated_at());
        holder.textViewUpdateDate.setText(note.getUpdated_at());

        //if the synced status is 0 displaying
        //queued icon
        //else displaying synced icon
        if (note.getStatus() == 0)
        {
            holder.imageViewStatus.setBackgroundResource(R.drawable.stopwatch);
        }
        else
        {
            holder.imageViewStatus.setBackgroundResource(R.drawable.success);
        }


    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    private class NotepadHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener
    {

        RelativeLayout root;
        TextView textViewName;
        ImageView imageViewStatus;
        TextView textViewCreateDate;
        TextView textViewUpdateDate;


        public NotepadHolder(View itemView) {
            super(itemView);
            root= (RelativeLayout) itemView.findViewById(R.id.root_layout);
            textViewName = (TextView) itemView.findViewById(R.id.textViewName);
            imageViewStatus = (ImageView) itemView.findViewById(R.id.imageViewStatus);
            textViewCreateDate = (TextView) itemView.findViewById(R.id.textViewCreateDate);
            textViewUpdateDate = (TextView) itemView.findViewById(R.id.textViewUpdateDate);

            root.setOnLongClickListener(this);
        }

        @Override
        public boolean onLongClick(View v) {

            if(v==root)
            {
                //Toast.makeText(context, notes.get(getAdapterPosition()).getNotes().toString(), Toast.LENGTH_LONG).show();
                onLongClickCallback.onLongClickContextMenu(getAdapterPosition(), v);
            }

            return true;

        }
    }
}

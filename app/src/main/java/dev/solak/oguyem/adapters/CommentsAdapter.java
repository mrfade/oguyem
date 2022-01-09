package dev.solak.oguyem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.ocpsoft.prettytime.PrettyTime;

import java.text.ParseException;
import java.util.List;
import java.util.Locale;

import dev.solak.oguyem.R;
import dev.solak.oguyem.classes.Utils;
import dev.solak.oguyem.models.Comment;

public class CommentsAdapter extends ArrayAdapter<Comment> implements View.OnClickListener{

    private List<Comment> dataSet;
    Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView name, rating, timeago, comment;
    }

    public CommentsAdapter(List<Comment> data, Context context) {
        super(context, R.layout.fragment_comment_item, data);
        this.dataSet = data;
        this.mContext = context;

    }

    @Override
    public void onClick(View v) {

        /*int position=(Integer) v.getTag();
        Object object= getItem(position);
        Comment dataModel=(Comment)object;

        switch (v.getId())
        {
            case R.id.item_info:
                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
                        .setAction("No action", null).show();
                break;
        }*/
    }

    private int lastPosition = -1;

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Comment dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        ViewHolder viewHolder; // view lookup cache stored in tag

        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.fragment_comment_item, parent, false);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.rating = (TextView) convertView.findViewById(R.id.rating);
            viewHolder.timeago = (TextView) convertView.findViewById(R.id.timeago);
            viewHolder.comment = (TextView) convertView.findViewById(R.id.comment);

            result = convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result = convertView;
        }

        try {
            viewHolder.rating.setText(String.format(Locale.US, "%.1f", Double.valueOf(dataModel.getRating())));
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            PrettyTime p = new PrettyTime();
            viewHolder.timeago.setText(p.format(Utils.parseDate("yyyy-MM-dd'T'HH:mm:ss.uuuu'Z'", dataModel.getCreatedAt())));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        viewHolder.comment.setText(dataModel.getComment());

        // Return the completed view to render on screen
        return convertView;
    }
}
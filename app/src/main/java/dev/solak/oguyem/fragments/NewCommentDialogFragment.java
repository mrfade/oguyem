package dev.solak.oguyem.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;

import androidx.fragment.app.DialogFragment;

import java.util.Objects;

import dev.solak.oguyem.MenuDayActivity;
import dev.solak.oguyem.R;

// https://developer.android.com/guide/topics/ui/dialogs
public class NewCommentDialogFragment extends DialogFragment {

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface NoticeDialogListener {
        public void onDialogPositiveClick(NewCommentDialogFragment dialog);
        public void onDialogNegativeClick(NewCommentDialogFragment dialog);
    }

    // Use this instance of the interface to deliver action events
    NoticeDialogListener listener;

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = (NoticeDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(getActivity().toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public View view;

    RatingBar ratingBar;
    EditText editText;
    Button button;

    Float rating;
    String comment;

    public Float getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.AlertDialogTheme);
        // Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        view = inflater.inflate(R.layout.dialog_new_comment, null);

        ratingBar = view.findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                if(v < 1.0f)
                    ratingBar.setRating(1.0f);
            }
        });

        editText = view.findViewById(R.id.comment);
        button = view.findViewById(R.id.select_images);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MenuDayActivity) requireActivity()).onSelectImageClick();
            }
        });

        // Add action buttons
        builder.setView(view)
            .setPositiveButton(R.string.send, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int id) {
                    // Send the positive button event back to the host activity

                    rating = ratingBar.getRating();
                    comment = editText.getText().toString();

                    listener.onDialogPositiveClick(NewCommentDialogFragment.this);
                }
            })
            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    NewCommentDialogFragment.this.getDialog().cancel();
                }
            });

        return builder.create();
    }
}

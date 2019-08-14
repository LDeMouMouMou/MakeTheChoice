package com.example.makethechoice;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;

import java.util.List;

public class choicesListViewAdapter extends ArrayAdapter<ChoiceItem> {

    private int resourceId;
    private Context context;
    private View.OnClickListener onClickListener;

    public choicesListViewAdapter(Context context, int resource, List<ChoiceItem> objects,
                                  View.OnClickListener onClickListener) {
        super(context, resource, objects);
        this.resourceId = resource;
        this.context = context;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ChoiceItem choiceItem = getItem(position);
        View view = LayoutInflater.from(getContext()).inflate(resourceId, parent, false);
        Button choiceText = view.findViewById(R.id.choiceText);
        choiceText.setTag(choiceItem.getChoiceText().charAt(0));
        choiceText.setText(choiceItem.getChoiceText());
        if (choiceItem.getChoosen()) {
            choiceText.setTextColor(getContext().getColor(R.color.colorChoiceChosen));
            choiceText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
        }
        if (choiceItem.getShowingWrong()) {
            choiceText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            choiceText.setTextColor(getContext().getColor(R.color.colorAccent));
        }
        if (choiceItem.getShowingRight()) {
            choiceText.setTypeface(Typeface.DEFAULT, Typeface.BOLD);
            choiceText.setTextColor(getContext().getColor(R.color.colorPrimary));
        }
        choiceText.setOnClickListener(this.onClickListener);
        return view;
    }
}

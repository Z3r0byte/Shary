package eu.z3r0byteapps.shary.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import eu.z3r0byteapps.shary.MagisterLibrary.container.School;
import eu.z3r0byteapps.shary.R;

public class SchoolAdapter extends ArrayAdapter<School> {
    private final Context context;
    private final School[] values;

    public SchoolAdapter(Context context, School[] values) {
        super(context, -1, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_simple, parent, false);

        TextView textView = rowView.findViewById(R.id.list_text_schools);
        textView.setText(values[position].name);


        return rowView;
    }
}

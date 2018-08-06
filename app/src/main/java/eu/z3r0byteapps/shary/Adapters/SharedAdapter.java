/*
 * Copyright (c) 2018-2018 Bas van den Boom 'Z3r0byte'
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package eu.z3r0byteapps.shary.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatImageButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.Util.ShareDatabase;

public class SharedAdapter extends ArrayAdapter<Share> {
    private static final String TAG = "SharedAdapter";
    private final Activity context;
    private ArrayList<Share> values;

    private ShareDatabase shareDatabase;

    public SharedAdapter(Context context, ArrayList<Share> values) {
        super(context, -1, values);
        this.context = (Activity) context;
        this.values = values;
    }

    @SuppressLint("SimpleDateFormat")
    private static String formatDate(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_shared, parent, false);

        TextView title = rowView.findViewById(R.id.title);
        TextView expires = rowView.findViewById(R.id.expires);
        TextView status = rowView.findViewById(R.id.status);

        shareDatabase = new ShareDatabase(context);

        title.setText(values.get(position).getComment());

        if (formatDate(values.get(position).getExpire()).equals("01-01-2050")) {
            expires.setText(String.format(context.getString(R.string.expires_format), "nooit"));
        } else {
            expires.setText(String.format(context.getString(R.string.expires_format), formatDate(values.get(position).getExpire())));
        }

        if (values.get(position).getExpire().before(new Date())) {
            status.setText(R.string.expired);
            status.setTextColor(context.getResources().getColor(R.color.md_red_500));
        } else {
            status.setText(R.string.active);
            status.setTextColor(context.getResources().getColor(R.color.md_green_500));
        }

        AppCompatImageButton delete = rowView.findViewById(R.id.delete);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title(R.string.delete_share_title)
                        .content(R.string.delete_share_desc)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                shareDatabase.deleteShare(values.get(position));
                                remove(values.get(position));
                            }
                        }).show();
            }
        });


        return rowView;
    }
}

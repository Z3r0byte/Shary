package eu.z3r0byteapps.shary.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import eu.z3r0byteapps.shary.MagisterLibrary.util.HttpUtil;
import eu.z3r0byteapps.shary.MagisterLibrary.util.LogUtil;
import eu.z3r0byteapps.shary.R;
import eu.z3r0byteapps.shary.SharyLibrary.Result;
import eu.z3r0byteapps.shary.SharyLibrary.Share;
import eu.z3r0byteapps.shary.SharyLibrary.ShareType;
import eu.z3r0byteapps.shary.SharyLibrary.Urls;

public class ShareAdapter extends ArrayAdapter<Share> implements DatePickerDialog.OnDateSetListener {
    private final Activity context;
    private final Share[] values;
    Button dateButton;
    Date expiry;

    public ShareAdapter(Context context, Share[] values) {
        super(context, -1, values);
        this.context = (Activity) context;
        this.values = values;
    }

    @SuppressLint("SimpleDateFormat")
    public static Date parseDate(String date) {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(date);
        } catch (ParseException e) {
            return null;
        }
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatDate(Date date) {
        return new SimpleDateFormat("dd-MM-yyyy").format(date);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.list_shares, parent, false);

        TextView title = rowView.findViewById(R.id.title);
        TextView comment = rowView.findViewById(R.id.comment);
        TextView expires = rowView.findViewById(R.id.expires);
        TextView restrictions = rowView.findViewById(R.id.restrictions);
        TextView status = rowView.findViewById(R.id.status);

        String titleStr = "Fout: geen type";
        if (values[position].getType().getID() == 1) {
            titleStr = "Agenda";
        } else if (values[position].getType().getID() == 2) {
            titleStr = "Nieuwe cijfers";
        } else if (values[position].getType().getID() == 3) {
            titleStr = "Cijfers";
        }
        title.setText(titleStr);

        if (values[position].getComment().isEmpty()) {
            comment.setText("Geen opmerkingen");
        } else {
            comment.setText(values[position].getComment());
        }

        if (formatDate(values[position].getExpire()).equals("01-01-2050")) {
            expires.setText(String.format(context.getString(R.string.expires_format), "nooit"));
        } else {
            expires.setText(String.format(context.getString(R.string.expires_format), formatDate(values[position].getExpire())));
        }

        restrictions.setText(String.format(context.getString(R.string.restrictions_format), "geen"));

        if (values[position].getExpire().before(new Date())) {
            status.setText(R.string.expired);
            status.setTextColor(context.getResources().getColor(R.color.md_red_500));
        } else {
            status.setText(R.string.active);
            status.setTextColor(context.getResources().getColor(R.color.md_green_500));
        }

        Button share = rowView.findViewById(R.id.share);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Bekijk mijn Magister account via Shary: https://shary.z3r0byteapps.eu/view/share/" + values[position].getSecret());
                sendIntent.setType("text/plain");
                context.startActivity(Intent.createChooser(sendIntent, context.getString(R.string.send_link)));
            }
        });

        Button revoke = rowView.findViewById(R.id.revoke);
        revoke.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title(R.string.revoke_share_title)
                        .content(R.string.revoke_share_desc)
                        .positiveText(R.string.yes)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                values[position].setExpire(new Date());
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            GsonBuilder gsonBuilder = new GsonBuilder();
                                            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Gson gson = gsonBuilder.create();
                                            String resultStr = LogUtil.getStringFromInputStream(HttpUtil.httpPost(Urls.expire, gson.toJson(values[position])));
                                            Result result = new Gson().fromJson(resultStr, Result.class);
                                            if (result.error != null) {
                                                error("Fout tijdens het intrekken van share");
                                                return;
                                            }
                                            error(context.getString(R.string.share_revoked));
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            error(context.getString(R.string.err_no_connection));
                                        }
                                    }
                                }).start();
                            }
                        }).show();
            }
        });

        Button edit = rowView.findViewById(R.id.edit);
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expiry = values[position].getExpire();
                MaterialDialog dialog = new MaterialDialog.Builder(context)
                        .title(R.string.new_share)
                        .customView(R.layout.dialog_new_share, true)
                        .positiveText(R.string.update)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View view = dialog.getCustomView();
                                Spinner spinner = view.findViewById(R.id.type);
                                EditText comment = view.findViewById(R.id.comment);
                                values[position].setComment(comment.getText().toString().trim());
                                values[position].setType(ShareType.getTypeById(spinner.getSelectedItemPosition() + 1));
                                values[position].setExpire(expiry);

                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        try {
                                            GsonBuilder gsonBuilder = new GsonBuilder();
                                            gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
                                            Gson gson = gsonBuilder.create();
                                            String resultStr = LogUtil.getStringFromInputStream(HttpUtil.httpPost(Urls.updateShare, gson.toJson(values[position])));
                                            Result result = new Gson().fromJson(resultStr, Result.class);
                                            if (result.error != null) {
                                                error("Fout tijdens aanpassen van share");
                                                return;
                                            }
                                            error(context.getString(R.string.share_updated));
                                            context.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    notifyDataSetChanged();
                                                }
                                            });
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            error(context.getString(R.string.err_no_connection));
                                        }
                                    }
                                }).start();
                            }
                        }).show();
                Spinner spinner = dialog.getCustomView().findViewById(R.id.type);
                EditText comment = dialog.getCustomView().findViewById(R.id.comment);
                spinner.setSelection(values[position].getType().getID() - 1);
                comment.setText(values[position].getComment());
                dateButton = dialog.getCustomView().findViewById(R.id.date);
                dateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Calendar now = Calendar.getInstance();
                        DatePickerDialog dpd = DatePickerDialog.newInstance(
                                ShareAdapter.this,
                                now.get(Calendar.YEAR),
                                now.get(Calendar.MONTH),
                                now.get(Calendar.DAY_OF_MONTH)
                        );
                        dpd.setMinDate(Calendar.getInstance());
                        dpd.show(context.getFragmentManager(), "Datepickerdialog");
                    }
                });

                if (formatDate(values[position].getExpire()).equals("01-01-2050")) {
                    dateButton.setText(R.string.none);
                } else {
                    dateButton.setText(formatDate(values[position].getExpire()));
                }

            }
        });


        return rowView;
    }

    private void error(final String message) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        monthOfYear = monthOfYear + 1;
        dateButton.setText(String.format("%d-%d-%d", dayOfMonth, monthOfYear, year));
        expiry = parseDate(String.format("%d-%d-%d 00:00:00", year, monthOfYear, dayOfMonth));
    }
}

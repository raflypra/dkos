package com.raflyprayogo.dkos.fragmentExpense;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.Image;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.raflyprayogo.dkos.Volley.AppController;
import com.raflyprayogo.dkos.R;

import java.util.List;

/**
 * Created by Cendana on 11/2/2015.
 */
public class ExpenseAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<Expense> Items;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ExpenseAdapter(Activity activity, List<Expense> Items) {
        this.activity = activity;
        this.Items = Items;
    }

    @Override
    public int getCount() {
        return Items.size();
    }

    @Override
    public Object getItem(int location) {
        return Items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_expense, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();

        ImageView icon_m    = (ImageView) convertView.findViewById(R.id.icon_m);

        TextView Title = (TextView) convertView.findViewById(R.id.title);
        TextView Cost = (TextView) convertView.findViewById(R.id.desc);

        Expense m = Items.get(position);

        Title.setText(m.getTitle());
        Cost.setText(Html.fromHtml(m.getCost()));

        TextDrawable drawable = TextDrawable.builder()
                .buildRound(m.getTitle().substring(0, 1), R.color.colorAccent);
        icon_m.setImageDrawable(drawable);

        return convertView;
    }
}

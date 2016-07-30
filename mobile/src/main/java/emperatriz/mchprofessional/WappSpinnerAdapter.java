package emperatriz.mchprofessional;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import emperatriz.common.WappDto;

/**
 * Created by ramon on 29/07/2016.
 */
public class WappSpinnerAdapter extends ArrayAdapter<WappDto> {
    private Activity context;
    ArrayList<WappDto> data = null;

    public WappSpinnerAdapter(Activity context, int resource, ArrayList<WappDto> data2)
    {
        super(context, resource, data2);
        this.context = context;
        this.data = data2;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent)
    {
        View row = convertView;
        if(row == null)
        {
            //inflate your customlayout for the textview
            LayoutInflater inflater = context.getLayoutInflater();
            row = inflater.inflate(R.layout.wapprow, parent, false);
        }
        //put the data in it
        WappDto item = data.get(position);
        if(item != null)
        {
            TextView name = (TextView)row.findViewById(R.id.name);
            TextView url = (TextView)row.findViewById(R.id.url);
            name.setText(item.name);
            url.setText(item.url);
        }

        return row;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        row = View.inflate(context, R.layout.wapprow, null);
        WappDto item = data.get(position);
        if(item != null)
        {
            TextView name = (TextView)row.findViewById(R.id.name);
            TextView url = (TextView)row.findViewById(R.id.url);
            name.setText(item.name);
            url.setText(item.url);
        }
        return row;
    }

}

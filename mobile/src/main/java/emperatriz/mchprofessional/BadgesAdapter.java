package emperatriz.mchprofessional;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BadgesAdapter extends ArrayAdapter<String> {

    ArrayList<Bitmap> badges;



    public BadgesAdapter(Context context, int resource, List<String> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.badgerow, null);
        }

        String p = getItem(position);

        if (p != null) {
            TextView name = (TextView) v.findViewById(R.id.name);
            name.setText(p);
            ImageView img = (ImageView) v.findViewById(R.id.badge);
            switch (position){
                case 0:
                    img.setImageResource(emperatriz.common.R.drawable.badge);
                    break;
                case 1:
                    img.setImageResource(emperatriz.common.R.drawable.badge1);
                    break;
                case 2:
                    img.setImageResource(emperatriz.common.R.drawable.badge2);
                    break;
                case 3:
                    img.setImageResource(emperatriz.common.R.drawable.badge3);
                    break;
                case 4:
                    img.setImageResource(emperatriz.common.R.drawable.badge4);
                    break;
                case 5:
                    img.setImageResource(emperatriz.common.R.drawable.badge5);
                    break;
            }
        }

        return v;
    }

}
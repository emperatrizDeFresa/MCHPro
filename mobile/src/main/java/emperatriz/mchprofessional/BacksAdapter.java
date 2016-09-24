package emperatriz.mchprofessional;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class BacksAdapter extends ArrayAdapter<String> {

    ArrayList<Bitmap> badges;



    public BacksAdapter(Context context, int resource, List<String> items, boolean showHidden) {
        super(context, resource, showHidden?items:items.subList(0,8));
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.backrow, null);
        }

        String p = getItem(position);

        if (p != null) {
            TextView name = (TextView) v.findViewById(R.id.name);
            name.setText(p);
            ImageView img = (ImageView) v.findViewById(R.id.badge);
            switch (position){
                case 0:
                    img.setImageResource(emperatriz.common.R.drawable.back);
                    break;
                case 1:
                    img.setImageResource(emperatriz.common.R.drawable.back1);
                    break;
                case 2:
                    img.setImageResource(emperatriz.common.R.drawable.back2);
                    break;
                case 3:
                    img.setImageResource(emperatriz.common.R.drawable.back3);
                    break;
                case 4:
                    img.setImageResource(emperatriz.common.R.drawable.back4);
                    break;
                case 5:
                    img.setImageResource(emperatriz.common.R.drawable.back5);
                    break;
                case 6:
                    img.setImageResource(emperatriz.common.R.drawable.back6);
                    break;
                case 7:
                    img.setImageResource(emperatriz.common.R.drawable.back7);
                    break;
                case 8:
                    img.setImageResource(emperatriz.common.R.drawable.back8);
                    break;
                case 9:
                    img.setImageResource(emperatriz.common.R.drawable.back9);
                    break;

            }
        }

        return v;
    }

}
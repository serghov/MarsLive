package am.serghov.marsmaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by serghov
 */
public class ImageAdapter extends BaseAdapter {

    private Context context;

    public Integer[] imageIDs = {
            R.drawable.martian_icon_1,
            R.drawable.martian_icon_2,
            R.drawable.martian_icon_3,
            R.drawable.martian_icon_4,
            R.drawable.martian_icon_5,
            R.drawable.martian_icon_6,
            R.drawable.martian_icon_7,
            R.drawable.martian_icon_8,
            R.drawable.martian_icon_1,
            R.drawable.martian_icon_2,
    };

    public ImageAdapter(Context c) {
        context = c;
    }

    public int getCount() {
        return imageIDs.length;
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {

            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.grid_item, null);
        } else {

            view = convertView;
        }

        ImageView i = (ImageView) view.findViewById(R.id.img_grid_item);
        i.setImageResource(imageIDs[position]);

        return view;
    }
}



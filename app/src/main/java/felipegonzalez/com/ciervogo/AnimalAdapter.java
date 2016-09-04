package felipegonzalez.com.ciervogo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by felipe on 03-09-16.
 */
public class AnimalAdapter extends BaseAdapter {
    private Context mContext;
    private LayoutInflater mInflater;
    private ArrayList<Animal> mDataSource;

    public AnimalAdapter(Context context, ArrayList<Animal> items) {
        mContext = context;
        mDataSource = items;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    //1
    @Override
    public int getCount() {
        return mDataSource.size();
    }

    //2
    @Override
    public Object getItem(int position) {
        return mDataSource.get(position);
    }

    //3
    @Override
    public long getItemId(int position) {
        return position;
    }

    //4
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get view for row item
        View rowView = mInflater.inflate(R.layout.list_item_animal, parent, false);
        // Get title element
        TextView titleTextView =
                (TextView) rowView.findViewById(R.id.animal_list_title);

// Get subtitle element
        TextView subtitleTextView =
                (TextView) rowView.findViewById(R.id.animal_list_subtitle);

// Get detail element
        TextView detailTextView =
                (TextView) rowView.findViewById(R.id.animal_list_detail);

// Get thumbnail element
        ImageView thumbnailImageView =
                (ImageView) rowView.findViewById(R.id.animal_list_thumbnail);
        Animal animal = (Animal) getItem(position);

// 2
        titleTextView.setText(animal.title);
        subtitleTextView.setText(animal.description);
        detailTextView.setText(animal.label);

// 3
        Picasso.with(mContext).load(animal.imageUrl).placeholder(R.mipmap.ic_launcher).into(thumbnailImageView);


        return rowView;
    }

}

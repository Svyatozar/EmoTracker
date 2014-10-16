package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ru.hyperboloid.emotracker.R;

/**
 * Фрагмент для настройки и отображения событий
 */
public class FragmentUserEvent extends Fragment
{
    private Button okButton;
    private Gallery gallery;

    private View tempView;

    private Date startDate;
    private Date endDate;

    private List<int[]> eventData;

    private final Integer[] mImage = { R.drawable.airport, R.drawable.birthday_cake,
            R.drawable.cafe, R.drawable.fires, R.drawable.fog_day, R.drawable.humburger,
            R.drawable.like, R.drawable.music, R.drawable.no_food, R.drawable.tattoo };

    public void initData(Date startDate, Date endDate, List<int[]> eventData)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventData = eventData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_user_event, container, false);

        okButton = (Button)rootView.findViewById(R.id.okButton);
        gallery = (Gallery)rootView.findViewById(R.id.gallery);

        gallery.setAdapter(new ImageAdapter(getActivity().getApplicationContext()));

        // обрабатываем щелчок на элементе галереи
        gallery.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id)
            {
                parent.refreshDrawableState();

                if (null != tempView)
                {
                    tempView.setBackgroundDrawable(new ColorDrawable(getResources().getColor(android.R.color.white)));
                }
                v.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.main_background)));

                // Выводим номер позиции при щелчке на картинке из галереи
                Toast.makeText(getActivity(),
                        "Позиция: " + position, Toast.LENGTH_SHORT).show();

                tempView = v;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                getActivity().onBackPressed();
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
    }

    private class ImageAdapter extends BaseAdapter
    {

        private int mGalleryItemBackground;
        private Context mContext;

        public ImageAdapter(Context context)
        {
            mContext = context;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return mImage.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return mImage[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return mImage[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            // TODO Auto-generated method stub
            ImageView view = new ImageView(mContext);
            view.setImageResource(mImage[position]);
            view.setPadding(20, 20, 20, 20);
            view.setLayoutParams(new Gallery.LayoutParams(100, 100));
            view.setScaleType(ImageView.ScaleType.CENTER);
            view.setBackgroundResource(mGalleryItemBackground);

            return view;
        }
    }
}

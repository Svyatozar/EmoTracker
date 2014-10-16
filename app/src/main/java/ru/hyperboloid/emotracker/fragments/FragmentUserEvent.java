package ru.hyperboloid.emotracker.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.inputmethodservice.ExtractEditText;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.List;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.model.Event;

/**
 * Фрагмент для настройки и отображения событий
 */
public class FragmentUserEvent extends Fragment
{
    private Button okButton;
    private Gallery gallery;

    private View tempView;

    private EditText comment;
    private EditText eventName;
    private TextView eventLength;

    private Date startDate;
    private Date endDate;

    /**
     * 0 - пульс 1 - стресс 2 - активность
     */
    private List<int[]> eventData;

    private int selectedImage = -1;
    private String length;

    private final Integer[] mImage = { R.drawable.airport, R.drawable.birthday_cake,
            R.drawable.cafe, R.drawable.fires, R.drawable.fog_day, R.drawable.humburger,
            R.drawable.like, R.drawable.music, R.drawable.no_food, R.drawable.tattoo };

    public void initData(Date startDate, Date endDate, List<int[]> eventData, String length)
    {
        this.startDate = startDate;
        this.endDate = endDate;
        this.eventData = eventData;
        this.length = length;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        final View rootView = inflater.inflate(R.layout.fragment_user_event, container, false);

        okButton = (Button)rootView.findViewById(R.id.okButton);
        gallery = (Gallery)rootView.findViewById(R.id.gallery);

        comment = (EditText)rootView.findViewById(R.id.comment);
        eventName = (EditText)rootView.findViewById(R.id.eventName);
        eventLength = (TextView)rootView.findViewById(R.id.eventLength);
        eventLength.setText("Продолжительность: " + length);

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

                selectedImage = position;

                tempView = v;
            }
        });

        okButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if ((-1 != selectedImage) && (comment.length() > 1) && (eventName.length() > 1))
                {
                    int pulse = 0;
                    int stress = 0;
                    int activity = 0;

                    for (int[] event : eventData)
                    {
                        pulse+=event[0];
                        stress+=event[1];
                        activity+=event[2];
                    }

                    pulse = pulse / eventData.size();
                    stress = stress / eventData.size();
                    activity = activity / eventData.size();

                    String info = "Пульс: " + pulse + " Стресс: " + stress + " Активность: " + activity;

                    Event event = new Event(mImage[selectedImage], eventName.getText().toString().toUpperCase()
                            + "\n" + "Продолжи-\nтельность: " + length, info, 1);
                    ApplicationWrapper.getDataBaseWrapper().writeEvent(event);

                    ApplicationWrapper.getNetworkUtil().addEvent(eventName.getText().toString(),
                                                                 comment.getText().toString(),
                                                                 startDate,
                                                                 endDate,
                                                                 eventData,
                                                                 pulse,
                                                                 stress,
                                                                 activity);

                    getActivity().onBackPressed();
                }
                else
                {
                    Toast.makeText(getActivity(), "Заполните все поля", Toast.LENGTH_SHORT).show();
                }
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

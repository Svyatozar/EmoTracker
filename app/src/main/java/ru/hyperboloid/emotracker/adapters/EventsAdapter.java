package ru.hyperboloid.emotracker.adapters;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.model.Event;
import ru.hyperboloid.emotracker.util.DataBaseWrapper;

/**
 * Адаптер для списка с событиями
 */
public class EventsAdapter extends BaseAdapter
{
    private List<Event> events = new ArrayList<Event>();

    private Context context;
    private LayoutInflater inflater;

    public EventsAdapter()
    {
        context = ApplicationWrapper.getContext();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * Загрузить данные в адаптер
     * @param events события
     */
    public void loadData(List<Event> events)
    {
        this.events.clear();
        this.events.addAll(events);

        notifyDataSetChanged();
    }

    @Override
    public int getCount()
    {
        return events.size();
    }

    @Override
    public Object getItem(int position)
    {
        return events.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parentViewGroup)
    {
        View result;

        if (null != convertView)
            result = convertView;
        else
            result = inflater.inflate(R.layout.item_event, parentViewGroup,false);

        if (position == 0)
        {
            result.setBackgroundDrawable(new ColorDrawable(ApplicationWrapper.getContext().getResources().getColor(R.color.second_background)));
        }
        else
        {
            result.setBackgroundDrawable(new ColorDrawable(ApplicationWrapper.getContext().getResources().getColor(R.color.main_background)));
        }

        Event event = events.get(position);

        ((ImageView)result.findViewById(R.id.itemIcon)).setImageResource(event.getImage());
        ((TextView)result.findViewById(R.id.details)).setText(event.getDetails());
        ((TextView)result.findViewById(R.id.info)).setText(event.getInfo());

        ImageView status= (ImageView)result.findViewById(R.id.statusIcon);

        if (DataBaseWrapper.STATUS_SYNCHRONIZED == event.getStatus())
        {
            status.setImageResource(android.R.drawable.sym_action_chat);
        }
        else
        {
            status.setImageResource(android.R.drawable.stat_sys_warning);
        }

        return result;
    }
}

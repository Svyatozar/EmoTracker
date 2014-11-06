package ru.hyperboloid.emotracker.util;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import ru.hyperboloid.emotracker.ApplicationWrapper;
import ru.hyperboloid.emotracker.R;
import ru.hyperboloid.emotracker.model.Event;

/**
 * Класс - обертка для работы с базой данных
 */
public class DataBaseWrapper extends SQLiteOpenHelper
{
    public static final int STATUS_SYNCHRONIZED = 1;
    public static final int STATUS_NON_SYNCHRONIZED = 0;

    private static final String EVENTS_TABLE = "EVENTS_TABLE";
    private static final String DATABASE_NAME = "EMO_BASE";

    private SQLiteDatabase base;

    public DataBaseWrapper(Context context)
    {
        super(context, DATABASE_NAME, null, 1);

        base = getWritableDatabase();
    }

    public void writeEvent(Event event)
    {
        ContentValues cv = new ContentValues();
        Date currentTimeStamp = Calendar.getInstance().getTime();

        cv.put("picture", event.getImage());
        cv.put("description", event.getDetails() + " " + formatDate(currentTimeStamp));
        cv.put("info", event.getInfo());
        cv.put("status", event.getStatus());

        long rowID = base.insert(EVENTS_TABLE, null, cv);
        Log.d("LOG", "row inserted, ID = " + rowID);
    }

    public List<Event> readEvents()
    {
        // делаем запрос всех данных из таблицы EVENTS_TABLE, получаем Cursor
        Cursor c = base.query(EVENTS_TABLE, null, null, null, null, null, null);

        List<Event> events = new ArrayList<Event>();

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst())
        {
            // определяем номера столбцов по имени в выборке
            int pictureColIndex = c.getColumnIndex("picture");
            int descriptionColIndex = c.getColumnIndex("description");
            int infoColIndex = c.getColumnIndex("info");
            int statusColIndex = c.getColumnIndex("status");

            do
            {
                int picture = c.getInt(pictureColIndex);
                String description = c.getString(descriptionColIndex);
                String info = c.getString(infoColIndex);
                int status = c.getInt(statusColIndex);

                events.add(new Event(picture, description, info, status));
            }
            while (c.moveToNext());
        } else
            Log.d("LOG", "0 rows");

        c.close();

        return events;
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        Log.d("LOG", "--- onCreate database ---");
        /**
         * создаем таблицу с полями
         */
        db.execSQL("create table " + EVENTS_TABLE + " ("
                + "id integer primary key autoincrement,"
                + "picture integer,"
                + "description text,"
                + "info text,"
                + "status integer" + ");");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
    }

    public String formatDate(Date date)
    {
        SimpleDateFormat targetDateFormat = new SimpleDateFormat(ApplicationWrapper.getContext().getString(R.string.app_date_format), Locale.US);

        return targetDateFormat.format(date);
    }
}

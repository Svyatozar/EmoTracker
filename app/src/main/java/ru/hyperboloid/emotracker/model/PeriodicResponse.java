package ru.hyperboloid.emotracker.model;

import ru.hyperboloid.emotracker.util.BinaryUtil;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by olshanikov on 9/25/14.
 */
public class PeriodicResponse implements Serializable {

    public Date date;

    public int chss;
    public int stressInd;
    public int aktivnost;

    public static PeriodicResponse parse(byte[] data) {
        PeriodicResponse response = new PeriodicResponse();
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.HOUR, data[3]);
        cal.set(Calendar.MINUTE, data[4]);
        cal.set(Calendar.SECOND, data[5]);
        cal.set(Calendar.DAY_OF_MONTH, data[6]);
        cal.set(Calendar.MONTH, data[7] - 1);
        cal.set(Calendar.YEAR, 2000 + data[8]);
        response.date = cal.getTime();

        response.chss = BinaryUtil.buildInt(data[9], data[10]);
        response.stressInd = BinaryUtil.buildInt(data[11], data[12]);
        response.aktivnost = BinaryUtil.buildInt(data[13], data[14]);

        return response;
    }

    @Override
    public String toString() {
        return "PeriodicResponse{" +
                "date=" + date +
                ", chss=" + chss +
                ", stressInd=" + stressInd +
                ", aktivnost=" + aktivnost +
                '}';
    }
}

package ru.hyperboloid.emotracker.model;

import ru.hyperboloid.emotracker.util.BinaryUtil;

import java.io.Serializable;

/**
 * Created by olshanikov on 9/23/14.
 */
public class DeviceInfo implements Serializable {

    public int stat;
    public int vbat;
    public int chss;
    public int stressInd;
    public int aktivnost;
    public int stepsCnt;

    public static DeviceInfo parse(byte[] data) {
        DeviceInfo device = new DeviceInfo();
        device.stat = data[3] & 0xFF;
        device.vbat = data[4] & 0xFF;
        device.chss = BinaryUtil.buildInt(data[5], data[6]);
        device.stressInd = BinaryUtil.buildInt(data[7], data[8]);
        device.aktivnost = BinaryUtil.buildInt(data[9], data[10]);
        device.stepsCnt = BinaryUtil.buildInt(data[11], data[12]);

        return device;
    }

    @Override
    public String toString() {
        return "DeviceInfo{" +
                "stat=" + stat +
                ", vbat=" + vbat +
                ", chss=" + chss +
                ", stressInd=" + stressInd +
                ", aktivnost=" + aktivnost +
                ", stepsCnt=" + stepsCnt +
                '}';
    }
}

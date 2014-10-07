package ru.hyperboloid.emotracker.model;

import ru.hyperboloid.emotracker.util.BinaryUtil;

import java.io.Serializable;

/**
 * Created by olshanikov on 9/25/14.
 */
public class PorogShiftResponse implements Serializable {

    public boolean hChss = false;
    public boolean lChss = false;
    public boolean hStressInd = false;
    public boolean lStressInd = false;
    public boolean hAktivnost = false;
    public boolean lAktivnost = false;
    public boolean hSteps = false;
    public boolean lSteps = false;

    public int chss = 0;
    public int stressInd = 0;
    public int aktivnost = 0;
    public int steps = 0;

    public static PorogShiftResponse parse(byte[] data) {
        PorogShiftResponse response = new PorogShiftResponse();

        byte highFlags = data[3];
        byte lowFlags = data[4];

        response.hChss = (highFlags & 1) > 0;
        response.lChss = (lowFlags & 1) > 0;
        response.hStressInd = (highFlags & 2) > 0;
        response.lStressInd = (lowFlags & 2) > 0;
        response.hAktivnost = (highFlags & 4) > 0;
        response.lAktivnost = (lowFlags & 4) > 0;
        response.hSteps = (highFlags & 8) > 0;
        response.lSteps = (lowFlags & 8) > 0;

        response.chss = BinaryUtil.buildInt(data[5], data[6]);
        response.stressInd = BinaryUtil.buildInt(data[7], data[8]);
        response.aktivnost = BinaryUtil.buildInt(data[9], data[10]);
        response.steps = BinaryUtil.buildInt(data[11], data[12]);

        return response;
    }
}

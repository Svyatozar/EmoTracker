package ru.hyperboloid.emotracker.command;

import ru.hyperboloid.emotracker.util.BinaryUtil;

import java.io.Serializable;

/**
 * Created by olshanikov on 9/25/14.
 */
public class SetPorogValuesCommand extends AbstractCommand implements Serializable {

    public boolean sendChss = false;
    public boolean sendStressInd = false;
    public boolean sendAktivnost = false;
    public boolean sendSteps = false;

    public int hChss = 0;
    public int lChss = 0;
    public int hStressId = 0;
    public int lStressId = 0;
    public int hAktivnost = 0;
    public int lAktivnost = 0;
    public int hSteps = 0;
    public int lSteps = 0;


    @Override
    byte commandId() {
        return BinaryUtil.COMMAND_SET_POROG;
    }

    @Override
    byte[] data() {
        byte[] data = new byte[17];

        byte porogEn = 0;
        if (sendChss) {
            porogEn += 1;
        }
        if (sendStressInd) {
            porogEn += 2;
        }
        if (sendAktivnost) {
            porogEn += 4;
        }
        if (sendSteps) {
            porogEn += 8;
        }

        data[0] = porogEn;

        byte[] parts = BinaryUtil.splitInt(hChss);
        data[1] = parts[0];
        data[2] = parts[1];

        parts = BinaryUtil.splitInt(lChss);
        data[3] = parts[0];
        data[4] = parts[1];

        parts = BinaryUtil.splitInt(hStressId);
        data[5] = parts[0];
        data[6] = parts[1];

        parts = BinaryUtil.splitInt(lStressId);
        data[7] = parts[0];
        data[8] = parts[1];

        parts = BinaryUtil.splitInt(hAktivnost);
        data[9] = parts[0];
        data[10] = parts[1];

        parts = BinaryUtil.splitInt(lAktivnost);
        data[11] = parts[0];
        data[12] = parts[1];

        parts = BinaryUtil.splitInt(hSteps);
        data[13] = parts[0];
        data[14] = parts[1];

        parts = BinaryUtil.splitInt(lSteps);
        data[15] = parts[0];
        data[16] = parts[1];

        return data;
    }
}

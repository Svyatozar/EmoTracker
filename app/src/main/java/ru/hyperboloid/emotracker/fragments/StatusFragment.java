//package ru.hyperboloid.emotracker;
//
//import android.app.Fragment;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.os.Messenger;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import ru.hyperboloid.emotracker.model.DeviceInfo;
//import ru.hyperboloid.emotracker.service.BluetoothService;
//import ru.hyperboloid.emotracker.util.BinaryUtil;
//
///**
//* Created by olshanikov on 9/24/14.
//*/
//public class StatusFragment extends Fragment {
//
//    TextView pulse;
//    TextView stress;
//    TextView activity;
//    TextView steps;
//
//    Messenger serviceMessenger;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.status_fragment, null);
//
//        pulse = (TextView) view.findViewById(R.id.pulse);
//        stress = (TextView) view.findViewById(R.id.stress);
//        activity = (TextView) view.findViewById(R.id.activity);
//        steps = (TextView) view.findViewById(R.id.steps);
//
//        view.findViewById(R.id.update_button).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (serviceMessenger != null) {
//                    Message msg = Message.obtain(null, BinaryUtil.COMMAND_GET_RATES);
//                    BinaryUtil.send(serviceMessenger, msg);
//                }
//            }
//        });
//
//        return view;
//    }
//
//    public void setServiceMessenger(Messenger messenger) {
//        this.serviceMessenger = messenger;
//    }
//
//    public Handler getHandler() {
//        return new StatusHandler();
//    }
//
//    public class StatusHandler extends Handler {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case BinaryUtil.COMMAND_GET_RATES:
//                    DeviceInfo deviceInfo = (DeviceInfo) msg.getData().getSerializable(BluetoothService.RESPONSE_DATA);
//                    if (deviceInfo != null) {
//                        pulse.setText(Integer.toString(deviceInfo.chss));
//                        stress.setText(Integer.toString(deviceInfo.stressInd));
//                        activity.setText(Integer.toString(deviceInfo.aktivnost));
//                        steps.setText(Integer.toString(deviceInfo.stepsCnt));
//                    }
//                    break;
//                default:
//                    super.handleMessage(msg);
//            }
//        }
//    }
//}

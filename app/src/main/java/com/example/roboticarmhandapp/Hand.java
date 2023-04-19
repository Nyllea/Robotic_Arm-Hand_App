package com.example.roboticarmhandapp;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import android.view.MotionEvent;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class Hand extends Fragment implements ArduinoListener {
    private static final int ARDUINO_VENDOR_ID = 2341, ARDUINO_USB_PERMISSION_DELAY = 3;

    TextView HandArduinoConnected;
    TouchableButton openHandButton,closeHandButton;
    private Arduino arduino;
    byte[] sendSeeked = new byte[1];

    public Hand() {
        // Required empty public constructor
    }

    private void SendToArduino(int value) {
        // byte prend des valeurs entre 0 et 255: on effectue un décalage pour envoyer des valeurs entre -128 et 127
        sendSeeked[0] = (byte) (128 + value);

        arduino.send(sendSeeked);
    }

    private void SendToArduino(int value) {
        sendSeeked[0] = (byte) value;

        arduino.send(sendSeeked);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hand, container, false);

        arduino = new Arduino(getActivity());
        arduino.addVendorId(ARDUINO_VENDOR_ID);

        // Declaration des views
        openHandButton = v.findViewById(R.id.openHandButton);
        closeHandButton = v.findViewById(R.id.closeHandButton);
        HandArduinoConnected = v.findViewById(R.id.HandArduinoConnectedTxt);

        // On envoie une commande à l'arduino tant que le bouton Fermer est maintenu
        closeHandButton.setOnTouchListener((View _v, MotionEvent event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                SendToArduino(-1);
                closeHandButton.performClick();
                return true;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                SendToArduino(0);
                return true;
            }
            return false;
        });

        // On envoie une commande à l'arduino tant que le bouton Ouvrir est maintenu
        openHandButton.setOnTouchListener((View _v, MotionEvent event) -> {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                SendToArduino(1);
                openHandButton.performClick();
                return true;
            }
            else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                SendToArduino(0);
                return true;
            }
            return false;
        });

        return v;
    }

    //Lorsqu'on branche la carte arduino, on demande la permission pour la connecter au téléphone
    @Override
    public void onStart() {
        super.onStart();
        arduino.setArduinoListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        arduino.unsetArduinoListener();
        arduino.close();
    }

    //Connecte la carte arduino si la permission est donnée
    @Override
    public void onArduinoAttached(UsbDevice device) {
        HandArduinoConnected.setText(R.string.arduinoConnected);
        arduino.open(device);
    }

    //Si la carte est déconnectée
    @Override
    public void onArduinoDetached() {
        HandArduinoConnected.setText(R.string.arduinoDisconnected);
    }

    // Quand on recoit un message de l'Arduino, on l'affiche
    @Override
    public void onArduinoMessage(byte[] bytes) {
        String msg = new String(bytes);
        HandArduinoConnected.setText(msg);
    }

    @Override
    public void onArduinoOpened() {

    }


    //Si la permission n'est pas accordée
    @Override
    public void onUsbPermissionDenied() {
        HandArduinoConnected.setText(getString(R.string.arduinoPermissionDenied, ARDUINO_USB_PERMISSION_DELAY));
        new Handler().postDelayed(() -> arduino.reopen(), ARDUINO_USB_PERMISSION_DELAY);
    }

}
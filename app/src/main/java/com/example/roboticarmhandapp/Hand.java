package com.example.roboticarmhandapp;

import android.hardware.usb.UsbDevice;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import me.aflak.arduino.Arduino;
import me.aflak.arduino.ArduinoListener;

public class Hand extends Fragment implements ArduinoListener {

    private static final int MAX_ANGLE = 180, DEFAULT_ANGLE = 90;
    private static final int ARDUINO_VENDOR_ID = 2341, ARDUINO_USB_PERMISSION_DELAY = 3;

    SeekBar HandSeekBar;
    TextView HandAngleIndicator, HandArduinoConnected;
    Button openHandButton,closeHandButton;
    private Arduino arduino;
    byte seeked;
    byte[] sendSeeked = new byte[1];

    public Hand() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_hand, container, false);

        // Declaration des views
        HandSeekBar = v.findViewById(R.id.HandSeekBar);
        HandAngleIndicator = v.findViewById(R.id.HandAngleIndicator);
        openHandButton = v.findViewById(R.id.openHandButton);
        closeHandButton = v.findViewById(R.id.closeHandButton);
        HandArduinoConnected = v.findViewById(R.id.HandArduinoConnectedTxt);

        arduino = new Arduino(getActivity());
        arduino.addVendorId(ARDUINO_VENDOR_ID);

        //Détermine les caractéristiques du HandseekBar
        HandSeekBar.setMax(MAX_ANGLE); //La valeur max atteignable

        HandSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                int adjust = i / 2;
                int newi = adjust * 2;
                seeked = (byte) adjust; // byte prend des valeurs entre -128 et 127
                sendSeeked[0] = seeked;
                //on affiche la valeur de l'angle sur l'application
                HandAngleIndicator.setText(String.valueOf(newi));
                //on envoie l'angle voulue vers arduino
                arduino.send(sendSeeked);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        HandSeekBar.setProgress(DEFAULT_ANGLE); //La valeur par défaut quand on ouvre l'application

        //fonction appelée lorsqu'on appuie sur le bouton "fermer" : on met l'angle à 180
        closeHandButton.setOnClickListener( V -> HandSeekBar.setProgress(MAX_ANGLE));


        //fonction appelée lorsqu'on appuie sur le boutton "ouvrir" : on met l'angle à 0
        openHandButton.setOnClickListener( V -> HandSeekBar.setProgress(0));

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

    @Override
    public void onArduinoMessage(byte[] bytes) {

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
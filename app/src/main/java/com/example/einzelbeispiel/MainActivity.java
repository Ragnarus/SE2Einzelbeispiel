package com.example.einzelbeispiel;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Button btnAbschicken = findViewById(R.id.cmdAbschicken);
        Button btnAQuersumme = findViewById(R.id.cmdAQuersumme);
        TextView textServerAntwort = findViewById(R.id.txtServerAntwort);
        EditText textMartikelnummer = findViewById(R.id.editMartikelnummer);

        btnAbschicken.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMartikel = String.valueOf(textMartikelnummer.getText());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Socket socket = new Socket("se2-submission.aau.at",20080);

                            //BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                            //out.write("12103912"+"\n");

                            DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                            out.writeBytes(textMartikel + "\n");

                            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                            String messageReceived = in.readLine();
                            socket.close();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    textServerAntwort.setText(messageReceived);
                                }
                            });

                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }).start();
            }
        });

        btnAQuersumme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textMartikel = String.valueOf(textMartikelnummer.getText());
                int sum =0;
                boolean add = true;

                for(int i=0;i<textMartikel.length();i++){
                    int zahl = Character.getNumericValue(textMartikel.charAt(i));
                    if(add){
                        sum = sum + zahl;
                    }else {
                        sum = sum - zahl;
                    }
                    add = !add;
                }

                if(sum%2 ==0){
                    textServerAntwort.setText(sum+" ist gerade");
                } else {
                    textServerAntwort.setText(sum+" ist ungerade");
                }
            }
        });
    }
}
package it.unicampania.scacchiera;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Corso Laboratorio di Progettazione e Sviluppo Software
 * A.A. 2016-2017 - ing. Pasquale Cantiello
 * Dipartimento di Ingegneria Industriale e dell'Informazione
 * Soluzione esercizio Scacchiera
 */
public class MainActivity extends AppCompatActivity {

    // Widget
    private EditText mDimensione;
    private Button mApplica;
    private Scacchiera mScacchiera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inizializzo gli oggetti relativi ai widget
        mDimensione = (EditText)findViewById(R.id.editDimensione);
        mApplica = (Button)findViewById(R.id.btnApplica);
        mScacchiera = (Scacchiera)findViewById(R.id.viewScacchiera);

        // Imposto il listener del pulsante
        mApplica.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Click sul pulsante, avvio un nuovo gioco
                // e imposto il listener per essere avvisato al termine
                int dimensione = Integer.parseInt(mDimensione.getText().toString());
                mScacchiera.nuovoGioco(dimensione, new Scacchiera.FineGiocoListener() {
                    @Override
                    public void avvisaFineGioco(int mosse) {
                        Toast.makeText(MainActivity.this,
                                getString(R.string.vittoria) + " " + mosse + " " + getString(R.string.mosse),
                                Toast.LENGTH_SHORT).show();
                        mApplica.setText(getString(R.string.dinuovo));
                    }
                });
            }
        });

    }

}

package it.unicampania.scacchiera;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

/**
 * Classe Scacchiera: utilizzata come custom view per la visualizzazione
 * di una scacchiera
 * Created by pasquale on 23/03/17.
 */
public class Scacchiera extends View {

    // Costanti
    private final int dimensioneMin = 2;
    private final int dimensioneMax = 10;
    private final int coloreA = Color.RED;
    private final int coloreB = Color.BLUE;

    private final String TAG = "Scacchiera";

    // Variabili locali
    private static int dimensione = 2;
    private static boolean[][] statoScacchiera;
    private static int mosse = 0;

    // Oggetti utilizzati per la grafica. Dichiarati all'esterno di onDraw
    // per evitare allocazioni eccessive
    Rect schermo;
    Rect casella;
    Paint paint;

    // Dimensioni caselle
    private int larghezzaCasella;
    private int altezzaCasella;

    /**
     * Interfaccia per avvisare che il gioco è terminato
     */
    public interface FineGiocoListener {
        void avvisaFineGioco(int mosse);
    }

    // Handler per avvisare della fine del gioco
    private FineGiocoListener fineGiocoListener;


    public Scacchiera(Context context) {
        super(context);
    }

    public Scacchiera(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    /**
     * Avvia un nuovo gioco, reimpostando la scacchiera e il numero di mosse
     * @param suddivisione numero di caselle per lato
     */
    public void nuovoGioco(int suddivisione, FineGiocoListener avviso) {

        // Verifico validità suddivisione
        dimensione = suddivisione;
        if (dimensione < dimensioneMin)
            dimensione = dimensioneMin;
        else if (dimensione > dimensioneMax)
            dimensione = dimensioneMax;

        // Imposto la scacchiera allo stato iniziale
        statoScacchiera = new boolean[dimensione][dimensione];
        for (int i = 0; i < dimensione; i++)
            for (int j = 0; j < dimensione; j++)
                statoScacchiera[i][j] = (((i + j) % 2) == 0);

        mosse = 0;

        // Allocazione effettuata una sola volta all'esterno di onDraw
        schermo = new Rect();
        paint = new Paint();
        casella = new Rect();

        fineGiocoListener = avviso;

        this.invalidate();
    }

    @Override
    public void onDraw(Canvas canvas) {

        if (statoScacchiera == null)
            return;

        // Determino le dimensioni dello schermo e delle caselle
        canvas.getClipBounds(schermo);

        larghezzaCasella = schermo.width() / dimensione;
        altezzaCasella = schermo.height() / dimensione;

        // Disegno la scacchiera
        for (int i = 0; i < dimensione; i++) {
            for (int j = 0; j < dimensione; j++) {

                int x = i * larghezzaCasella;
                int y = j * altezzaCasella;
                casella.set(x, y, x + larghezzaCasella - 1, y + altezzaCasella - 1);
                paint.setColor( statoScacchiera[i][j] ? coloreA : coloreB );
                canvas.drawRect(casella, paint);
            }
        }
    }

    /**
     * Handler dell'evento touch
     * @param motionEvent oggetto ricevuto ad ogni pressione
     */
    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        int azione = motionEvent.getAction();

        if (azione == MotionEvent.ACTION_UP) {
            // Le coordinate ottenute dall'evento sono assolute rispetto allo schermo
            // occorre detrarre l'inizio della view e trasformarle in interi
            int x = (int)motionEvent.getX() - schermo.left;
            int y = (int)motionEvent.getY() - schermo.top;

            // Converto le coordinate negli indici di riga e colonna
            int i = x / larghezzaCasella;
            int j = y / altezzaCasella;
            ++mosse;

            boolean fineGioco = invertiCaselle(i, j);
            this.invalidate();

            // Avviso il listener della fine del gioco
            if (fineGioco)
                fineGiocoListener.avvisaFineGioco(mosse);
        }
        return true;
    }

    /**
     * Effettua l'inversione di tutte le caselle di una riga e di una colonna
     * @param riga riga da invertire
     * @param colonna colonna da invertire
     * @return true in caso di termine del gioco
     */
    private boolean invertiCaselle(int riga, int colonna) {

        // Inverto le caselle della riga
        for (int j = 0; j < dimensione; j++)
            statoScacchiera[riga][j] = !statoScacchiera[riga][j];

        // Inverto le caselle della colonna
        for (int i = 0; i < dimensione; i++)
            statoScacchiera[i][colonna] = !statoScacchiera[i][colonna];

        // Verifico se il gioco è terminato
        boolean fineGioco = true;
        boolean primaCasella = statoScacchiera[0][0]; // Prima casella come riferimento

        int i = 0;
        while (fineGioco && i < dimensione) {
            int j = 0;
            while (fineGioco && j < dimensione) {
                // Permango nella condizione di fine gioco se ogni casella è uguale
                fineGioco = (statoScacchiera[i][j] == primaCasella);
                j++;
            }
            i++;
        }

        return fineGioco;
    }

}

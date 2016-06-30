package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;


public class GhostActivity extends AppCompatActivity {
    private static final String COMPUTER_TURN = "Computer's turn";
    private static final String USER_TURN = "Your turn";
    private final String SCORE = "Computer Score: %d  Player Score: %d";
    private GhostDictionary dictionary;
    private boolean userTurn = false;
    private Random random = new Random();

    private int computerScore = 0;
    private int playerScore = 0;


    private TextView txtWordFragment;
    private TextView txtStatus;
    private TextView txtScore;
    private Button btnChallenge;
    private Button btnRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);
        AssetManager assetManager = getAssets();
        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new FastDictionary(inputStream);
//            dictionary = new SimpleDictionary(inputStream);
        } catch (IOException e) {
            Toast toast = Toast.makeText(this, "Could not load dictionary", Toast.LENGTH_LONG);
            toast.show();
        }
        txtWordFragment = (TextView) findViewById(R.id.ghostText);
        txtStatus = (TextView) findViewById(R.id.gameStatus);
        txtScore = (TextView) findViewById(R.id.txtScore);
        btnChallenge = (Button) findViewById(R.id.btnChallenge);
        btnRestart = (Button) findViewById(R.id.btnReset);

        btnChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dictionary.isWord(String.valueOf(txtWordFragment.getText()))) {
                    txtStatus.setText("Player Wins");
                    playerScore++;
                    onResult(null);
                } else {
                    if (dictionary.getAnyWordStartingWith(String.valueOf(txtWordFragment.getText())) != null) {
                        txtStatus.setText("Word Exists");
                        computerScore++;
                        txtWordFragment.setText(dictionary.getAnyWordStartingWith(String.valueOf(txtWordFragment.getText())));
                        onResult(null);
                    } else {
                        txtStatus.setText("Player Wins");
                        playerScore++;
                        onResult(null);
                    }
                }
            }
        });

        btnRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onStart(null);
            }
        });
        onStart(null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_ghost, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (userTurn) {
            if (keyCode > 28 && keyCode < 55) {
                char c = (char) ('a' - KeyEvent.KEYCODE_A + keyCode);
                txtStatus.setText(COMPUTER_TURN);
                txtWordFragment.append(Character.toString(c));
                userTurn = false;
                computerTurn();
                return true;
            }
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     *
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        txtWordFragment.setText("");
        txtScore.setText(String.format(SCORE,computerScore,playerScore));
        btnChallenge.setEnabled(true);
        if (userTurn) {
            txtStatus.setText(USER_TURN);
        } else {
            txtStatus.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }

    public boolean onResult(View view){
        userTurn = false;
        txtScore.setText(String.format(SCORE,computerScore,playerScore));
        btnChallenge.setEnabled(false);
        return true;
    }

    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again

        String prefix = String.valueOf(txtWordFragment.getText());
        if (dictionary.isWord(prefix)) {
            txtStatus.setText("Computer won");
            computerScore++;
            onResult(null);
            return;
        }

        String possibleInput = dictionary.getGoodWordStartingWith(prefix);
        Log.v(possibleInput, "COMPUTERSENT");

        if (possibleInput != null) {
            String computerInput = Character.toString((possibleInput).charAt(prefix.length()));
            txtWordFragment.append(computerInput);
        } else {
            txtStatus.setText("No such word exists.");
            computerScore++;
            onResult(null);
            return;
        }
        userTurn = true;
        txtStatus.setText(USER_TURN);

    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
    }


}

/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.ghost;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
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
    private Random random = new Random(System.currentTimeMillis());

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
            dictionary = new SimpleDictionary(inputStream);
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
                //It is already a word
                if (dictionary.isWord(String.valueOf(txtWordFragment.getText()))) {
                    txtStatus.setText("Player Wins");
                    playerScore++;
                    onResult();
                } else {
                    // No word can be made with current prefix
                    // If any word can be made with said prefix, computer wins.
                    if (dictionary.getAnyWordStartingWith(String.valueOf(txtWordFragment.getText())) != null) {
                        txtStatus.setText("Word Exists");
                        computerScore++;
                        txtWordFragment.setText(dictionary.getAnyWordStartingWith(String.valueOf(txtWordFragment.getText())));
                        onResult();
                    } else {
                        // player wins
                        txtStatus.setText("Player Wins");
                        playerScore++;
                        onResult();
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

    /**
     * Handler for the "Reset" button.
     * Randomly determines whether the game starts with a user turn or a computer turn.
     * @param view
     * @return true
     */
    public boolean onStart(View view) {
        userTurn = random.nextBoolean();
        TextView text = (TextView) findViewById(R.id.ghostText);
        text.setText("");
        btnChallenge.setEnabled(true);
        TextView label = (TextView) findViewById(R.id.gameStatus);
        if (userTurn) {
            label.setText(USER_TURN);
        } else {
            label.setText(COMPUTER_TURN);
            computerTurn();
        }
        return true;
    }


    public void onResult(){
        userTurn = false;
        txtScore.setText(String.format(SCORE,computerScore,playerScore));
        btnChallenge.setEnabled(false);
    }

    private void computerTurn() {
        // Do computer turn stuff then make it the user's turn again
        String prefix = String.valueOf(txtWordFragment.getText());
        if (dictionary.isWord(prefix)) {
            txtStatus.setText("Computer won");
            computerScore++;
            onResult();
            return;
        }
//        String possibleInput = dictionary.getAnyWordStartingWith(prefix);
        String possibleInput = dictionary.getGoodWordStartingWith(prefix);

        Log.v(possibleInput, "COMPUTERSENT");

        if (possibleInput != null) {
            String computerInput = Character.toString((possibleInput).charAt(prefix.length()));
            txtWordFragment.append(computerInput);
        } else {
            txtStatus.setText("No such word exists.");
            computerScore++;
            onResult();
            return;
        }
        userTurn = true;
        txtStatus.setText(USER_TURN);
    }

    /**
     * Handler for user key presses.
     * @param keyCode
     * @param event
     * @return whether the key stroke was handled.
     */
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

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedState) {
        super.onRestoreInstanceState(savedState);
    }
}

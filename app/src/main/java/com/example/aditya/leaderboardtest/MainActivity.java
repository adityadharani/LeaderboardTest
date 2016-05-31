package com.example.aditya.leaderboardtest;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private EditText input;
    private TableLayout leaderboard;
    private InputMethodManager imm;
    private String[] arrData;
    private SharedPreferences savedData;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.post_game_screen);

        leaderboard = (TableLayout) findViewById(R.id.leaderboard);
        input = (EditText)findViewById(R.id.userInput);
        arrData = new String[leaderboard.getChildCount()];
        savedData = getSharedPreferences("leaderboardData", MODE_PRIVATE);
        editor = savedData.edit();

        //displays data previously saved, or "..." id nothing is saved
        for(int i = 0; i < arrData.length; i++)
        {
            arrData[i] = savedData.getString("dataPiece" + i, "...");
        }
        transferFromArrDataToLeaderboard();

        //Calls updateLeaderBoard(EditText inputtedTime) once the user hits done on their keyboard
        input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE)
                {
                    String strInput = input.getText().toString();

                    //makes sure that the data inputted is in a logical format before displaying
                    // any data
                    if (strInput.length() != 5 || strInput.indexOf(":") != strInput.lastIndexOf(":")
                            || strInput.indexOf(":") != 2
                            || strInput.indexOf(":") != strInput.lastIndexOf(":")
                            || Integer.parseInt(strInput.substring(strInput.indexOf(":") + 1)) >= 60)
                    {
                        Toast toast = Toast.makeText(getApplicationContext(), "Invalid input",
                                Toast.LENGTH_SHORT);
                        toast.show();
                        input.setText("");
                        return false;
                    }
                    else
                    {
                        updateLeaderboard();

                        //saves data in the array
                        for (int i = 0; i < arrData.length; i++) {
                            editor.putString("dataPiece" + i, arrData[i]);
                        }
                        editor.apply();

                        input.setText("");

                        //Closes the keyboard
                        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                        return true;
                    }
                }
                return false;
            }
        });

    }

    //places user input appropriately on the leaderboard by comparing input to already existing values
    private void updateLeaderboard()
    {
        Toast failMessage = Toast.makeText(getApplicationContext(),
                "You did not make the leaderboard :(", Toast.LENGTH_LONG);
        String strInput = input.getText().toString();

        for(int i = 0; i <= leaderboard.getChildCount(); i++)
        {
            if (i == leaderboard.getChildCount())
            {

                failMessage.show();
                break;
            }

            TextView timeAt = getTextViewInTableRow(leaderboard.getChildAt(i));
            String strTimeAt = timeAt.getText().toString();

            if (strTimeAt.equals("..."))
            {
                timeAt.setText(input.getText());
                arrData[i] = strInput;
                break;
            }
            else if(getLargerTime(strInput, strTimeAt).equals(strInput))
            {
                for(int j = leaderboard.getChildCount() - 1; j >= i; j--)
                {
                    if(i == j)
                    {
                        timeAt.setText(input.getText());
                        arrData[i] = strInput;
                        break;
                    }

                    getTextViewInTableRow(leaderboard.getChildAt(j)).setText(getTextViewInTableRow
                            (leaderboard.getChildAt(j - 1)).getText());
                    arrData[j] = arrData[j-1];
                }
                break;
            }
        }
    }

    //returns the TextView object found in a TableRow
    private TextView getTextViewInTableRow(View view)
    {
        TableRow row = (TableRow)view;
        return (TextView) row.getChildAt(0);
    }

    //returns the larger of two times, or the String "neither" if they have the same value
    private String getLargerTime(String time1, String time2)
    {
        int time1Mins = Integer.parseInt(time1.substring(0, time1.indexOf(":")));
        int time2Mins = Integer.parseInt(time2.substring(0, time2.indexOf(":")));
        int time1Secs = Integer.parseInt(time1.substring(time1.indexOf(":") + 1));
        int time2Secs = Integer.parseInt(time2.substring(time2.indexOf(":") + 1));

        if (time1Mins < time2Mins)
            return time2;
        else if (time2Mins < time1Mins)
            return time1;
        else if (time1Secs < time2Secs)
            return time2;
        else if (time2Secs < time1Secs)
            return time1;

        return "neither";
    }

    //Transfers the values in arrData to the leaderboard
    private void transferFromArrDataToLeaderboard()
    {
        for (int i = 0; i < arrData.length; i++)
        {
            getTextViewInTableRow(leaderboard.getChildAt(i)).setText(arrData[i]);
        }
    }
}

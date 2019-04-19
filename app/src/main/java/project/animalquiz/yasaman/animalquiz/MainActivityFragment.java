package project.animalquiz.yasaman.animalquiz;


import android.animation.Animator;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.graphics.Color;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final int NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ=10;

    /* here it was possible if we used array but
    * List has more function than array so it's better for us*/
    private List<String> allAnimalsNamesList;
    private List<String> animalsNamesQuizList;

    /* a set is like a list but in set we can not assign duplicate value in it */
    private Set<String> animalTypesInQuiz;
    private String correctAnimalsAnswer;
    private int numberOfAllGuesses;
    private int numberOfRightAnswers;
    private int numberOfAnimalsGuessRows;
    private SecureRandom secureRandomNumber;
    private Handler handler;
    private Animation wrongAnswerAnimation;

    private LinearLayout animalQuizLinearLayout;
    private TextView txtQuestionNumber;
    private ImageView imgAnimal;
    private LinearLayout[] rowsOfGuessButtonsInAnimalQuiz;
    private TextView txtAnswer;



    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        allAnimalsNamesList = new ArrayList<>();
        animalsNamesQuizList = new ArrayList<>();
        secureRandomNumber = new SecureRandom();
        handler = new Handler();

        wrongAnswerAnimation = AnimationUtils.loadAnimation(getActivity(), R.anim.wrong_answer_animation);
        wrongAnswerAnimation.setRepeatCount(1);


        animalQuizLinearLayout = view.findViewById(R.id.animalQuizLinearLayout);
        txtQuestionNumber = view.findViewById(R.id.txtQuestionNumber);
        imgAnimal = view.findViewById(R.id.imgAnimal);
        rowsOfGuessButtonsInAnimalQuiz = new LinearLayout[3];
        rowsOfGuessButtonsInAnimalQuiz[0] = view.findViewById(R.id.firstRowLinearLayout);
        rowsOfGuessButtonsInAnimalQuiz[1] = view.findViewById(R.id.secondRowLinearLayout);
        rowsOfGuessButtonsInAnimalQuiz[2] = view.findViewById(R.id.thirdRowLinearLayout);
        txtAnswer = view.findViewById(R.id.txtAnswer);

        for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz){

            for (int column = 0 ; column < row.getChildCount(); column++){

                Button btnGuess = (Button) row.getChildAt(column);
                btnGuess.setOnClickListener(btnGuessListener);
                btnGuess.setTextSize(24);
            }
        }


        txtQuestionNumber.setText(getString(R.string.question_text, 1, NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ));


        return view;
    }

        private View.OnClickListener btnGuessListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Button btnGuess = ((Button) view);
                String guessValue = btnGuess.getText().toString();
                String answerValue = getTheExactAnimalName(correctAnimalsAnswer);
                ++numberOfAllGuesses;

                if (guessValue.equals(answerValue)) {

                    ++numberOfRightAnswers;

                    txtAnswer.setText(answerValue + "!" + "Right");

                    disableQuizGuessButtons();

                    if (numberOfRightAnswers == NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ) {

                        new AlertDialog.Builder(getContext()).setTitle(numberOfAllGuesses + "")
                                .setMessage("Congratulation! You finished The game. Do you want play again")
                                    .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        resetAnimalQuiz();
                                    }
                                })  .setNegativeButton("Quit", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                getActivity().finish();
                            }
                        }).setCancelable(false)
                                .show();


                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {

                                animateAnimalQuiz(true);

                            }
                        }, 1000);

                    }
                } else {

                    imgAnimal.startAnimation(wrongAnswerAnimation);
                    txtAnswer.setText(R.string.wrong_answer_message);
                    btnGuess.setEnabled(false);

                }


            }
        };

    private String getTheExactAnimalName(String animalName){

        return animalName.substring(animalName.indexOf('-') + 1).replace('_',' ');
    }

    private void disableQuizGuessButtons(){

        for(int row = 0; row < numberOfAnimalsGuessRows; row++){

            LinearLayout guessRowLinearLayout = rowsOfGuessButtonsInAnimalQuiz[row];
            for (int buttonIndex = 0; buttonIndex < guessRowLinearLayout.getChildCount(); buttonIndex++){

                guessRowLinearLayout.getChildAt(buttonIndex).setEnabled(false);
            }
        }
    }

    public void resetAnimalQuiz(){

        AssetManager assets = getActivity().getAssets();
        allAnimalsNamesList.clear();

        try {

            for (String animalType : animalTypesInQuiz){

                String[] animalImagePathsInQuiz = assets.list(animalType);

                for (String animalImagePathInQuiz : animalImagePathsInQuiz){

                    allAnimalsNamesList.add(animalImagePathInQuiz.replace(".png", ""));
                }
            }

        }catch (IOException e){

            Log.e("AnimalQuiz" , "Error", e);
        }

        numberOfRightAnswers = 0;
        numberOfAllGuesses = 0;
        animalsNamesQuizList.clear();

        int counter = 1;
        int numberOfAvailableAnimals = allAnimalsNamesList.size();

        while (counter <= NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ) {

            int randomIndex = secureRandomNumber.nextInt(numberOfAvailableAnimals);

            String animalImageName = allAnimalsNamesList.get(randomIndex);

            if (!animalsNamesQuizList.contains(animalImageName)) {

                animalsNamesQuizList.add(animalImageName);
                ++counter;

            }

        }

        showNextAnimal();


    }

    /* if we put value tru for animateAnimalQuiz input the image
                will animate out after answer and when we want go to next page  */

    private void animateAnimalQuiz(boolean animateOutAnimalImage) {

        /* when we reset our quiz or the app started we don't want any animation so this
        *           if prevents that. */
        if (numberOfRightAnswers == 0) {

            return;

        }

        int xTopLeft = 0;
        int yTopLeft = 0;


        int xBottomRight = animalQuizLinearLayout.getLeft() + animalQuizLinearLayout.getRight();
        int yBottomRight = animalQuizLinearLayout.getTop() + animalQuizLinearLayout.getBottom();

        // Here is max value for radius
        int radius = Math.max(animalQuizLinearLayout.getWidth(), animalQuizLinearLayout.getHeight());

        Animator animator;

        if (animateOutAnimalImage) {

            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,
                    xBottomRight, yBottomRight, radius, 0);


            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {

                    showNextAnimal();
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });


        }else {

            /* this else will happen when the next quiz to be shown to the user
            *       in fact animate for start the next quiz or animal */

            animator = ViewAnimationUtils.createCircularReveal(animalQuizLinearLayout,
                    xTopLeft, yTopLeft, 0, radius);
        }


        animator.setDuration(700);
        animator.start();


    }

    private void showNextAnimal() {

        String nextAnimalImageName = animalsNamesQuizList.remove(0);
        correctAnimalsAnswer = nextAnimalImageName;
        txtAnswer.setText("");

        txtQuestionNumber.setText(getString(R.string.question_text,
                (numberOfRightAnswers + 1), NUMBER_OF_ANIMALS_INCLUDED_IN_QUIZ));

        String animalType = nextAnimalImageName.substring(0, nextAnimalImageName.indexOf("-"));

        AssetManager assets = getActivity().getAssets();

        try (InputStream stream = assets.open(animalType + "/" + nextAnimalImageName + ".png")) {

            Drawable animalImage = Drawable.createFromStream(stream, nextAnimalImageName);

            imgAnimal.setImageDrawable(animalImage);

            animateAnimalQuiz(false);

        } catch (IOException e) {
            Log.e("AnimalQuiz", "There is an Error Getting" + nextAnimalImageName, e);
        }

        Collections.shuffle(allAnimalsNamesList);

        int correctAnimalNameIndex = allAnimalsNamesList.indexOf(correctAnimalsAnswer);

        String correctAnimalName = allAnimalsNamesList.remove(correctAnimalNameIndex);
        allAnimalsNamesList.add(correctAnimalName);

        for (int row = 0; row < numberOfAnimalsGuessRows; row++) {

            for (int column = 0; column < rowsOfGuessButtonsInAnimalQuiz[row].getChildCount(); column++) {

                Button btnGuess = (Button) rowsOfGuessButtonsInAnimalQuiz[row].getChildAt(column);
                btnGuess.setEnabled(true);

                String animalImageName = allAnimalsNamesList.get((row * 2) + column);
                btnGuess.setText(getTheExactAnimalName(animalImageName));

            }

        }

        int row = secureRandomNumber.nextInt(numberOfAnimalsGuessRows);
        int column = secureRandomNumber.nextInt(2);


        LinearLayout randomRow = rowsOfGuessButtonsInAnimalQuiz[row];
        String correctAnimalImageName = getTheExactAnimalName(correctAnimalsAnswer);

        ((Button) randomRow.getChildAt(column)).setText(correctAnimalImageName);


    }

    public void modifyAnimalsGuessRows(SharedPreferences sharedPreferences) {

        final String  NUMBER_OF_GUESS_OPTIONS = sharedPreferences.getString(MainActivity.GUESSES, null);

        numberOfAnimalsGuessRows = Integer.parseInt(NUMBER_OF_GUESS_OPTIONS) / 2;

        for (LinearLayout horizontalLinearLayout : rowsOfGuessButtonsInAnimalQuiz) {

            horizontalLinearLayout.setVisibility(View.GONE);

        }

        for (int row = 0; row < numberOfAnimalsGuessRows; row++) {

            rowsOfGuessButtonsInAnimalQuiz[row].setVisibility(View.VISIBLE);

        }

    }

    public void modifyTypeOfAnimalsInQuiz(SharedPreferences sharedPreferences) {

        animalTypesInQuiz = sharedPreferences.getStringSet(MainActivity.ANIMALS_TYPE, null);

    }


    public void modifyQuizFont(SharedPreferences sharedPreferences) {

        String fontStringValue = sharedPreferences.getString(MainActivity.QUIZ_FONT, null);

        switch (fontStringValue) {

            case "Chunkfive.otf":
                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.chunkfive);

                    }

                }

                break;
            case "FontleroyBrown.ttf":

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.fontlerybrown);

                    }

                }

                break;
            case "Wonderbar Demo.otf":

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setTypeface(MainActivity.wonderbarDemo);

                    }

                }

                break;
        }

    }



    public void modifyBackgroundColor(SharedPreferences sharedPreferences) {

        String backgroundColor = sharedPreferences.getString(MainActivity.QUIZ_BACKGROUND_COLOR, null);

        switch (backgroundColor) {

            case "White":

                animalQuizLinearLayout.setBackgroundColor(Color.WHITE);

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.BLUE);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;

            case "Black":

                animalQuizLinearLayout.setBackgroundColor(Color.BLACK);

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.YELLOW);
                        button.setTextColor(Color.BLACK);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Green":

                animalQuizLinearLayout.setBackgroundColor(Color.GREEN);

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.YELLOW);


                break;

            case "Blue":

                animalQuizLinearLayout.setBackgroundColor(Color.BLUE);

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.RED);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);

                break;

            case "Red":

                animalQuizLinearLayout.setBackgroundColor(Color.RED);

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLUE);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.WHITE);
                txtQuestionNumber.setTextColor(Color.WHITE);


                break;

            case "Yellow":

                animalQuizLinearLayout.setBackgroundColor(Color.YELLOW);

                for (LinearLayout row : rowsOfGuessButtonsInAnimalQuiz) {

                    for (int column = 0; column < row.getChildCount(); column++) {

                        Button button = (Button) row.getChildAt(column);
                        button.setBackgroundColor(Color.BLACK);
                        button.setTextColor(Color.WHITE);

                    }

                }

                txtAnswer.setTextColor(Color.BLACK);
                txtQuestionNumber.setTextColor(Color.BLACK);

                break;

        }

    }



}

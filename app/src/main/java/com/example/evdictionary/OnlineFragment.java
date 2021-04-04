package com.example.evdictionary;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class OnlineFragment extends Fragment {
    TextToSpeech textToSpeechEnglish;
    TextToSpeech textToSpeechVietnamese;

    TranslateType translateType = TranslateType.av;

    TextView textViewOnline;
    TextInputEditText textInputEditText;
    TextInputLayout textInputLayout;
    ImageButton swapButton;
    ImageButton speakButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_online, container, false);

        textViewOnline = view.findViewById(R.id.textViewResultOnline);
        textInputEditText = view.findViewById(R.id.editTextOnlineSearch);
        textInputLayout = view.findViewById(R.id.textInputOnline);

        swapButton = view.findViewById(R.id.btnChangeOnline);
        speakButton = view.findViewById(R.id.speakOnlineButton);

        swapButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                textInputEditText.setText("");
                if (translateType == TranslateType.av) {
                    translateType = TranslateType.va;
                    textInputLayout.setHint("Vietnamese - English");
                } else {
                    translateType = TranslateType.av;
                    textInputLayout.setHint("English - Vietnamese");
                }
            }
        });

        textInputEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    // Close keyboard when press search key
                    InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                    String sourceLang;
                    String targetLang;

                    if (translateType == TranslateType.av) {
                        sourceLang = "en";
                        targetLang = "vi";
                    } else {
                        sourceLang = "vi";
                        targetLang = "en";
                    }

                    String input = textInputEditText.getText().toString();

                    // I have to use encode for input because if input have multiple words
                    // like "how are you", execute then broken
                    try {
                        input = URLEncoder.encode(input, "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    if (input.length() > 0) {
                        FetchGoogleData fetchGoogleData = new FetchGoogleData();
                        fetchGoogleData.execute("https://translate.googleapis.com/translate_a/single?client=gtx&sl=" + sourceLang + "&tl=" + targetLang + "&dt=t&q=" + input);
                    }

                    return true;
                }
                return false;
            }
        });

        textToSpeechEnglish = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                textToSpeechEnglish.setLanguage(Locale.US);
            }
        });

        textToSpeechVietnamese = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onInit(int status) {
                textToSpeechVietnamese.setLanguage(new Locale("vi-VN"));
            }
        });

        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (translateType == TranslateType.av) {
                    textToSpeechEnglish.speak(textInputEditText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                } else {
                    textToSpeechVietnamese.speak(textInputEditText.getText().toString(), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        return view;
    }


    @SuppressWarnings("deprecation")
    public class FetchGoogleData extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... urls) {
            String result = "";

            try {
                HttpResponse execute = new DefaultHttpClient().execute(new HttpGet(urls[0]));
                StatusLine statusLine = execute.getStatusLine();

                if (statusLine.getStatusCode() == 200) {
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    execute.getEntity().writeTo(byteArrayOutputStream);
                    String response = byteArrayOutputStream.toString();
                    byteArrayOutputStream.close();

                    String [] temp = response.split("\n")[0].split("\"");

                    result = temp[1];

                    return result;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            textViewOnline.setText(result);
        }
    }
}

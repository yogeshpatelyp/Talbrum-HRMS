package com.talentcerebrumhrms.chatbot;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.activity.SplashScreenActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

import static android.app.Activity.RESULT_OK;

public class FragmentChatbot extends Fragment implements AIListener {

    private static final int RECORD_REQUEST_CODE = 101;
    private static final int REQUEST_CODE_SPEECH_INPUT = 505 ;
    Button listen, send;
    EditText textInput;
    TextToSpeech t1;
    private ChatAdapter mChatAdapter;
    private  List<Chat> mChat = new ArrayList<>();
    RecyclerView recyclerView;
    private static final String TAG = "MainActivity";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootview = inflater.inflate(R.layout.fragment_chatbot, null);

        listen = rootview.findViewById(R.id.listen);
        send = rootview.findViewById(R.id.send);
        textInput = rootview.findViewById(R.id.textInput);

        recyclerView = rootview.findViewById(R.id.recycler_view);
        mChatAdapter = new ChatAdapter(getActivity(),mChat);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(mChatAdapter);

        t1 = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    t1.setLanguage(Locale.UK);

                }
            }
        });
        int permission = ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.RECORD_AUDIO);
        if(permission != PackageManager.PERMISSION_GRANTED){
            Log.d(TAG, "onCreate: "+ "permission to record denied ");
            makeRequest();
        }
        final AIConfiguration config = new AIConfiguration("09e76e401a25409e88187deb99223215",
                AIConfiguration.SupportedLanguages.English,
                AIConfiguration.RecognitionEngine.System);

        final AIService aiService = AIService.getService(getActivity(), config);
        aiService.setListener(this);

        listen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                promptSpeechInput();
                aiService.startListening();
            }
        });
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = textInput.getText().toString();

                if(input.isEmpty()){

                }else {
                    sendMessage("11", "00", input);
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute(input);
                    textInput.setText("");
                }
            }
        });

        return rootview;
    }


    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Say Something");

        try{
            startActivityForResult(intent,REQUEST_CODE_SPEECH_INPUT);
        }
        catch (ActivityNotFoundException e ){
            Toast.makeText(getActivity(),"Your device doesn't supports speech input",Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Fragment uploadType = getChildFragmentManager().findFragmentById(R.id.content_frame);

        if (uploadType != null) {
            uploadType.onActivityResult(requestCode, resultCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case REQUEST_CODE_SPEECH_INPUT : {
                if (resultCode == RESULT_OK && null != data){
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String userQuery = result.get(0);
                    //  userText.setText(userQuery);
                    sendMessage("11","00",userQuery);
                    RetrieveFeedTask task = new RetrieveFeedTask();
                    task.execute(userQuery);

                }
                break;
            }
        }
    }


    private void sendMessage(String s, String s1, String userQuery)
    {
        Chat newChat = new Chat();
        newChat.setSender(s);
        newChat.setReciever(s1);
        newChat.setMessage(userQuery);
        mChat.add(newChat);

        mChatAdapter.notifyDataSetChanged();
        recyclerView.smoothScrollToPosition((mChat.size()));

    }


    private void makeRequest() {
        ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.RECORD_AUDIO},
                RECORD_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode){
            case RECORD_REQUEST_CODE : {
                if(grantResults.length == 0 || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "Permission denied by User");
                }
                else
                {
                    Log.d(TAG, "Permission granted by user");
                }
                return;
            }
        }
    }

    public String getText(String query)  throws UnsupportedEncodingException {
        String text = "";
        BufferedReader reader  = null;

        try {
            URL url = new URL("https://api.dialogflow.com/v1/query?v=20150910");

            //send POST Data request

            URLConnection conn = url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);

            conn.setRequestProperty("Authorization", "Bearer 09e76e401a25409e88187deb99223215");
            conn.setRequestProperty("Content-Type", "application/json");

            //create JSON Object here

            JSONObject jsonParam = new JSONObject();
            jsonParam.put("query",query);
            jsonParam.put("lang","en");
            jsonParam.put("sessionId","1234567890");

            //use Output Stream to send data with this post request
            OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
            Log.d(TAG, "getText: after conversation is "+ jsonParam.toString());
            wr.write(jsonParam.toString());
            wr.flush();
            Log.d(TAG, "karma "+ " json is "+ jsonParam);

            //Get the server response
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder ab = new StringBuilder();
            String line = null;

            //Read server response
            while ((line = reader.readLine()) != null){
                ab.append(line + "\n");
            }

            text = ab.toString();


            //access
            JSONObject jsonObject = new JSONObject(text);
            JSONObject object  = jsonObject.getJSONObject("result");
            JSONObject fullfillment  = null;
            String speech = null;

            fullfillment = object.getJSONObject("fulfillment");

            speech = fullfillment.optString("speech");

            Log.d(TAG, "karma "+ "response is : "+ text);



            return  speech;


        }

        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try{
                reader.close();
            }
            catch (Exception ex){

            }
        }
        return  null;
    }

    class RetrieveFeedTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... strings) {
            String s = null;
            try
            {
                Log.d(TAG, "doInBackground: called");
                s = getText(strings[0]);
                Log.d(TAG, "doInBackground: after called");
            }
            catch (UnsupportedEncodingException e){
                e.printStackTrace();
                Log.d(TAG, "doInBackground: "+ e);
            }
            return s;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            t1.speak(s,TextToSpeech.QUEUE_FLUSH,null);
            Log.d(TAG, "onPostExecute: "+ s);
            //   responseText.setText(s);
            Chat newChat = new Chat();
            newChat.setSender("00");
            newChat.setReciever("11");
            newChat.setMessage(s);
            mChat.add(newChat);
            //    readMessages("00","11",s);
            mChatAdapter.notifyDataSetChanged();
            recyclerView.smoothScrollToPosition((mChat.size()));
            if(s.contains("redirect")){
                Intent in = new Intent(getActivity(),SplashScreenActivity.class);
                startActivity(in);
                getActivity().finish();
            }
            else{
                //    Toast.makeText(MainActivity.this,"NOT FOUND ",Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void readMessages(String sender,String receiver,String message){

        Chat myChat = new Chat(sender,receiver,message);
        mChat.add(myChat);
        mChatAdapter.notifyDataSetChanged();



    }

    @Override
    public void onResult(AIResponse result) {
        Log.d(TAG, "onResult: "+ result.toString());
        Result result1 = result.getResult();

        //     textView.setText("Query "+ result1.getResolvedQuery()+" Action: "+ result1.getAction());


    }

    @Override
    public void onError(AIError error) {

    }

    @Override
    public void onAudioLevel(float level) {

    }

    @Override
    public void onListeningStarted() {

    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {

    }

}

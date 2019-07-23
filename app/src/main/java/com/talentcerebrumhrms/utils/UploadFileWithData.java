package com.talentcerebrumhrms.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.talentcerebrumhrms.utils.AppController.apiVersion;
import static com.talentcerebrumhrms.utils.AppController.appVersion;
import static com.talentcerebrumhrms.utils.AppController.sharedpreferences;

/**
 * Created by Harshit on 29-Jun-17.
 */

public class UploadFileWithData extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private String FILE_UPLOAD_URL;
    private File sourceFile;
    private String json_list;

    private ProgressDialog progressDialog;

    public UploadFileWithData(Context context, String FILE_UPLOAD_URL, File sourceFile, String json_list) {
        this.context = context;
        this.FILE_UPLOAD_URL = FILE_UPLOAD_URL;
        this.sourceFile = sourceFile;
        this.json_list = json_list;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.e("onPreExecute", "started");
        progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading File...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Log.e("cancel clicked", "cancelled uploading");
                cancel(true);
            }
        });
        progressDialog.show();
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        Log.e("onCancelled", "true");
        Toast.makeText(context, "Cancelled Successfully", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected Void doInBackground(Void... v) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(FILE_UPLOAD_URL).openConnection();
            connection.setRequestMethod("POST");
            String boundary = "---------------------------boundary";
            String tail = "\r\n--" + boundary + "--\r\n";
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("Connection", "close");
            connection.setRequestProperty("Accept", "application/json");
            connection.setRequestProperty("token", sharedpreferences.getString("token", ""));
            connection.setRequestProperty("appVersion", appVersion);
            connection.setRequestProperty("apiVersion", apiVersion);
            connection.setDoOutput(true);

            String fileHeader1 = "--" + boundary + "\r\n"
                    + "Content-Disposition: form-data; name=\"myFile\""
                    + "; filename=\"" + sourceFile.getName() + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n"
                    + "Content-Transfer-Encoding: binary\r\n"
                    + "; list=\"" + json_list;

            long fileLength = sourceFile.length() + tail.length();
            String fileHeader2 = "Content-length: " + fileLength + "\r\n";
            String fileHeader = fileHeader1 + fileHeader2 + "\r\n";

            long requestLength = fileHeader.length() + fileLength;
            connection.setRequestProperty("Content-length", "" + requestLength);
            connection.setFixedLengthStreamingMode((int) requestLength);
            connection.connect();

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes(fileHeader);
            out.flush();

            int progress = 0;
            int bytesRead;
            byte buf[] = new byte[1024];
            BufferedInputStream bufInput = new BufferedInputStream(new FileInputStream(sourceFile));
            while ((bytesRead = bufInput.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
                out.flush();
                progress += bytesRead;
                publishProgress((int) ((progress * 100) / sourceFile.length())); // sending progress percent to publishProgress
            }

            // Write closing boundary and close stream
            out.writeBytes(tail);
            out.flush();
            out.close();

            // Get server response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder builder = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            Log.e("response String", "" + builder);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        progressDialog.dismiss();
        Log.e("onPostExecute", "Uploading Complete");
    }
}

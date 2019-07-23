package com.talentcerebrumhrms.utils;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import static com.talentcerebrumhrms.utils.AppController.apiVersion;
import static com.talentcerebrumhrms.utils.AppController.appVersion;
import static com.talentcerebrumhrms.utils.AppController.sharedpreferences;

/**
 * Created by Harshit on 06-Jul-17.
 */

public class UploadImageWithData extends AsyncTask<Void, Integer, Void> {

    private Context context;
    private String FILE_UPLOAD_URL;
    private Bitmap pic;
    private String json_list;

    private ProgressDialog progressDialog;

    public UploadImageWithData(Context context, String FILE_UPLOAD_URL, Bitmap pic, String json_list) {
        this.context = context;
        this.FILE_UPLOAD_URL = FILE_UPLOAD_URL;
        this.pic = pic;
        this.json_list = json_list;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressDialog = new ProgressDialog(context);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setProgress(0);
        progressDialog.setMax(100);
        progressDialog.setCancelable(false);
        progressDialog.show();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        progressDialog.setProgress(values[0]);
    }

    @Override
    protected Void doInBackground(Void... v) {
        try {

            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            pic.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] buf = stream.toByteArray();

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
                    + "Content-Disposition: form-data; name=\"myPic\""
                    + "; picname=\"" + System.currentTimeMillis() + ".png" + "\"\r\n"
                    + "Content-Type: application/octet-stream\r\n"
                    + "Content-Transfer-Encoding: binary\r\n";

            long fileLength = buf.length + tail.length();
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
            ByteArrayInputStream bufInput = new ByteArrayInputStream(buf);
            while ((bytesRead = bufInput.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
                out.flush();
                progress += bytesRead;
                publishProgress((int) ((progress * 100) / buf.length)); // sending progress percent to publishProgress
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
    }
}

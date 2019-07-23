package com.talentcerebrumhrms.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.talentcerebrumhrms.adapter.UploadTimesheetAdapter;
import com.talentcerebrumhrms.datatype.UploadTimesheetDataType;
import com.talentcerebrumhrms.R;
import com.talentcerebrumhrms.utils.ApiUtil;
import com.talentcerebrumhrms.utils.UploadFileWithData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.talentcerebrumhrms.utils.AppController.serverUrl;
import static com.talentcerebrumhrms.utils.AppController.timesheet_upload_array;

/**
 * Created by Harshit on 19-Jun-17.
 */

public class UploadTimesheetActivity extends AppCompatActivity implements View.OnClickListener {

    RecyclerView recyclerView;
    UploadTimesheetAdapter uploadTimesheetAdapter;
    Button submit;
    LinearLayout file_footer;
    TextView file_name;
    ImageButton remove_file;

    public static final int MY_PERMISSIONS_REQUEST_FILE = 99;
    public static final int PICKFILE_REQUEST_CODE = 100;
    String src = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_timesheet);
        Log.e("UploadTimesheetActivity", "onCreate");

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(getString(R.string.upload_timesheet));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        file_footer = (LinearLayout) findViewById(R.id.file_footer);
        file_name = (TextView) findViewById(R.id.file_name);
        remove_file = (ImageButton) findViewById(R.id.remove_file);
        remove_file.setOnClickListener(this);

        if (getIntent().getExtras() != null && !getIntent().getExtras().getString("file_name").equals("")) {
            file_footer.setVisibility(View.VISIBLE);
            file_name.setText(getIntent().getExtras().getString("file_name"));
        }

        printUploadTimesheetArray(); // Only to print timesheet_upload_array

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        uploadTimesheetAdapter = new UploadTimesheetAdapter(this);
        recyclerView.setAdapter(uploadTimesheetAdapter);

        submit = (Button) findViewById(R.id.submit);
        submit.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_timesheet_row, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.upload_file:

                if (ActivityCompat.checkSelfPermission(UploadTimesheetActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                    ActivityCompat.requestPermissions(
                            UploadTimesheetActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_FILE);
                else
                    openFileChooser();
                break;
            case R.id.add_row:
                addRow("", "Present", "", "", "");
                printUploadTimesheetArray();
                uploadTimesheetAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(timesheet_upload_array.size() - 1);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_FILE: //FILE
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    openFileChooser();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 100: //File Chooser
                if (data != null) {
                    Uri uri = data.getData();
                    src = uri.getPath();
                    Log.e("file_source", src);
                    file_footer.setVisibility(View.VISIBLE);
                    String file_src_parts[] = src.split("/");
                    file_name.setText(file_src_parts[file_src_parts.length - 1]);
                }
                break;
        }
    }

    private void addRow(String working_date, String working_status, String working_hour, String leave_type_id, String remark) {
        UploadTimesheetDataType a = new UploadTimesheetDataType();
        a.setWorkingDate(working_date);
        a.setWorkingStatus(working_status);
        a.setWorkingHour(working_hour);
        a.setLeaveTypeId(leave_type_id);
        a.setRemark(remark);
        timesheet_upload_array.add(a);
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        intent = Intent.createChooser(intent, "Choose a File");
        startActivityForResult(intent, PICKFILE_REQUEST_CODE);
    }

    public static void printUploadTimesheetArray() {
        String s = "";
        for (int i = 0; i < timesheet_upload_array.size(); i++) {
            s = s.concat("{Working Date : " + timesheet_upload_array.get(i).getWorkingDate() +
                    " , Working Status : " + timesheet_upload_array.get(i).getWorkingStatus() +
                    " , Working Hours : " + timesheet_upload_array.get(i).getWorkingHour() +
                    " , Leave Type ID : " + timesheet_upload_array.get(i).getLeaveTypeId() +
                    " , Remark : " + timesheet_upload_array.get(i).getRemark() + " } ");
        }
        Log.e("timesheet_upload_array", s);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.submit:
                String list = new Gson().toJson(timesheet_upload_array); //JSONArray
                Log.e("lisst", list);
                if (src.equalsIgnoreCase(""))
                    uploadTimesheetApiCall(list);
                else {
                    File file = new File(src);
                    UploadFileWithData uploadFileWithData = new UploadFileWithData(this, serverUrl + "/api/v2/timesheets/save_timesheet", file, list);
                    uploadFileWithData.execute();
                }
                break;
            case R.id.remove_file:
                file_footer.setVisibility(View.GONE);
                file_name.setText("");
                src = "";
                break;
        }
    }

 /*   private void uploadTimesheetApiCall(String json_list) {

        AppController.upload_timesheet_record(new ResponseListener() {
            @Override
            public void onResponse(@NonNull JSONObject jsonObject) {
                try {
                    String message = jsonObject.getString("message");
                    Toast.makeText(UploadTimesheetActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(@NonNull VolleyError volleyError) {
                errorFunction(UploadTimesheetActivity.this, volleyError);
            }
        }, json_list);
    }*/

    private void uploadTimesheetApiCall(String json_list) {
        JSONObject obj = new JSONObject();
        try {
            JSONArray timesheet_array = new JSONArray(json_list);
            obj.put("list", timesheet_array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.e("upload_timesheet_obj", obj.toString());
        RequestBody myreqbody = null;
        try {
            myreqbody = RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    (new JSONObject(String.valueOf(obj))).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
         Call<JsonObject> call = ApiUtil.getServiceClass().upload_timesheet_record(myreqbody);
        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> jObject) {

                try {
                    JSONObject jsonObject = new JSONObject(jObject.body().toString());

                    String message = jsonObject.getString("message");
                    Toast.makeText(UploadTimesheetActivity.this, "" + message, Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("Error",t.getMessage());
               // errorFunction(UploadTimesheetActivity.this, volleyError);
            }
        });
    }
}
package com.example.onlinemusic;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.onlinemusic.retrofit.ApiUtils;
import com.example.onlinemusic.retrofit.RetrofitClient;
import com.example.onlinemusic.retrofit.ApiService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadActivity extends AppCompatActivity {
    private final int REQUEST_CODE = 1;
    private Uri uri;
    private EditText edtName, edtSinger;
    private ImageButton btnUpload, btnFileChooser;
    private TextView txtFilePath;

    private Dialog dialog;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE == requestCode && resultCode == Activity.RESULT_OK) {
            if (data == null) return;

            uri = data.getData();

            String path = uri.getPath();

            txtFilePath.setText(path.substring(path.lastIndexOf('/') + 1));
        }
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("audio/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        AppUtil.makeStatusBarTransparent(this);

        mapping();
        createDialog();
    }

    private void mapping() {
        edtName = findViewById(R.id.edtName);
        edtSinger = findViewById(R.id.edtSinger);
        btnUpload = findViewById(R.id.imbUpload);
        btnFileChooser = findViewById(R.id.imbFileChooser);
        txtFilePath = findViewById(R.id.txtFilePath);

        setButtonFileChooserListener();
        setButtonUploadListener();
    }

    private void setButtonFileChooserListener() {
        btnFileChooser.setOnClickListener(view -> {
            openFileChooser();
        });
    }

    private void setButtonUploadListener() {
        btnUpload.setOnClickListener(view -> {

            uploadImage();
        });
    }

    private void uploadImage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String name = edtName.getText().toString();
                String singer = edtSinger.getText().toString();

                if(name.isEmpty() || singer.isEmpty() || uri == null) return;

                dialog.show();

                byte[] byteData = null;

                try {
                    InputStream inputStream = getContentResolver().openInputStream(uri);
                    byteData = toByteArray(inputStream);
                } catch (IOException e) { e.printStackTrace(); }

                RequestBody dataBody = RequestBody.create(MediaType.parse("audio/*"), byteData);

                MultipartBody.Part dataPart
                        = MultipartBody.Part.createFormData("data", txtFilePath.getText().toString(), dataBody);

                ApiService apiService = ApiUtils.getService();

                Call call = apiService.uploadImage(dataPart, name, singer);

                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) {
                        
                        if(response.code() == 200) {
                            Toast.makeText(UploadActivity.this, "Song uploaded successfully", Toast.LENGTH_SHORT).show();

                            MainActivity.getInstance().finish();

                            startActivity(new Intent(UploadActivity.this, MainActivity.class));

                            UploadActivity.this.finish();

                        } else
                            Toast.makeText(UploadActivity.this, "Uploading failed", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(Call call, Throwable t) {
                        Toast.makeText(UploadActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    @Override
    protected void onPause() {
        dialog.dismiss();
        super.onPause();
    }

    private void createDialog() {
        dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_upload);

    }

    private byte[] toByteArray(InputStream inputStream) throws IOException {

        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int nRead;
        byte[] data = new byte[1024];

        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }

        buffer.flush();
        return buffer.toByteArray();
    }

}
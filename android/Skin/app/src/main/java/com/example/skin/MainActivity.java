package com.example.skin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.skin.services.PhotoClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getName();
    private ImageView mCapturedImage, mPlaceholderImage;
    private TextView mHeadingText, mSubText;
    private Button mDetectButton;
    private final int REQUEST_CODE = 11;
    private final int RESULT_LOAD_IMAGE = 12;
    private Uri imageUri = null;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCapturedImage = findViewById(R.id.captured_photo);
        mPlaceholderImage = findViewById(R.id.image_placeholder);
        mDetectButton = findViewById(R.id.detect_button);
        mHeadingText = findViewById(R.id.empty_text);
        mSubText = findViewById(R.id.sub_text);


        mDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog = new ProgressDialog(MainActivity.this);
                dialog.setTitle("Detecting...");
                dialog.setMessage("Please wait while we detect your disease from the uploaded image");
                dialog.setCanceledOnTouchOutside(false);
                dialog.show();

                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Bitmap bmp = getBitmap(MainActivity.this, imageUri);
                            Bitmap scaledBitmap = Bitmap.createScaledBitmap(bmp, 256, 256, true);
                            ByteArrayOutputStream stream = new ByteArrayOutputStream();
                            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
                            byte[] byteArray = stream.toByteArray();
                            bmp.recycle();
                            uploadImage(byteArray);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

            }

        });
    }

    public static Bitmap getBitmap(Context context, Uri uri) throws IOException {
        Bitmap bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri);
        return bitmap;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RESULT_LOAD_IMAGE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CODE)
                onCaptureImageResult(data);
        }


    }

    private void onSelectFromGalleryResult(Intent data) {
        Bitmap bm = null;
        mPlaceholderImage.setVisibility(View.GONE);
        mHeadingText.setVisibility(View.GONE);
        mSubText.setVisibility(View.GONE);
        mCapturedImage.setVisibility(View.VISIBLE);
        if (data != null) {
            try {
                imageUri = data.getData();
                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        mCapturedImage.setImageBitmap(bm);
        mDetectButton.setVisibility(View.VISIBLE);

    }

    private void onCaptureImageResult(Intent data) {
        String uri = imageUri.toString();
        Log.e("uri-:", uri);
        Toast.makeText(this, imageUri.toString(), Toast.LENGTH_LONG).show();

        mPlaceholderImage.setVisibility(View.GONE);
        mHeadingText.setVisibility(View.GONE);
        mSubText.setVisibility(View.GONE);
        mCapturedImage.setVisibility(View.VISIBLE);
        try {
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Drawable d = new BitmapDrawable(getResources(), bitmap);
        mCapturedImage.setImageDrawable(d);
        mDetectButton.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.photo:
                takePhoto();
                break;
            case R.id.gallery:
                choosePhoto();
                break;
            default:
                break;
        }
        return super.onContextItemSelected(item);

    }

    private void choosePhoto() {
        Log.d(TAG, "choosePhoto: ");
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, RESULT_LOAD_IMAGE);

    }

    private void takePhoto() {
        Log.d(TAG, "takePhoto: ");
        imageUri = null;
        mCapturedImage.setImageBitmap(null);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "MyPhoto.jpg");
        imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE);

    }


    public void uploadImage(byte[] imageBytes) {
        Log.d(TAG, "uploadImage: Uploading image........");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);

        MultipartBody.Part multipart = MultipartBody.Part.createFormData("photo", "MyPhoto.jpg", requestFile);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://5d9711d8.ngrok.io")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        PhotoClient photoClient = retrofit.create(PhotoClient.class);

        Call<ResponseBody> call = photoClient.uploadPhoto(multipart);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {
                    dialog.dismiss();
                    Log.d(TAG, "onResponse: Response: " + response);
                    Toast.makeText(MainActivity.this, "Succeess", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Success but Something went wrong", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialog.dismiss();
                if (t instanceof SocketTimeoutException) {
                    Log.d(TAG, "Socket time out exception " + t);
                }
                Log.d(TAG, "onFailure: Failed: " + t);
                Toast.makeText(MainActivity.this, "Failed!!!", Toast.LENGTH_SHORT).show();

            }
        });
    }


}

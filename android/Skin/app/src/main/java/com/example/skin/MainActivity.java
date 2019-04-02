package com.example.skin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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

import com.example.skin.models.SkinImage;
import com.example.skin.services.PhotoClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements LocationListener {
    private final String TAG = MainActivity.class.getName();
    private ImageView mCapturedImage, mPlaceholderImage;
    private TextView mHeadingText, mSubText;
    private Button mDetectButton;
    private final int REQUEST_CODE = 11;
    private final int RESULT_LOAD_IMAGE = 12;
    private Uri imageUri = null;
    private Bitmap bitmap = null;
    private ProgressDialog dialog;

    private final String apiKey = "AIzaSyCJsSEeJpAzMLkT-j4F2j1SxzOVJKIVabk";

    private Double latitude, longitude;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCapturedImage = findViewById(R.id.captured_photo);
        mPlaceholderImage = findViewById(R.id.image_placeholder);
        mDetectButton = findViewById(R.id.detect_button);
        mHeadingText = findViewById(R.id.empty_text);
        mSubText = findViewById(R.id.sub_text);

//        getLocation();


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

//    private void getLocation() {
//        try {
//            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 5, this);
//        } catch (SecurityException e) {
//            e.printStackTrace();
//        }
//    }

//    private void locationRetrofit() {
//        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
//// set your desired log level
//        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
//
//        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
//// add your other interceptors …
//
//// add logging as last interceptor
//        httpClient.addInterceptor(logging);
//
//        Retrofit.Builder builder = new Retrofit.Builder()
//                .baseUrl("https://maps.googleapis.com")
//                .addConverterFactory(GsonConverterFactory.create());
//        Retrofit retrofit = builder.build();
//
//
//        PlacesClient placesClient = retrofit.create(PlacesClient.class);
//
//        String myLocation = latitude + "," + longitude;
//        Log.d(TAG, "locationRetrofit: myLocation: " + myLocation);
//
//        Call<Response> call = placesClient.allHospitals(myLocation, 5000, "hospitals", apiKey);
//        call.enqueue(new Callback<Response>() {
//
//
//            @Override
//            public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
//                try {
//                    Log.d(TAG, "onResponse: response : " + new JSONObject(response.toString()));
////                    JSONObject jsonObject = new JSONObject(response.body().toString());
////                    String output = jsonObject.getString("message");
////                    Log.d(TAG, "onResponse: output:" + output);
//                    JsonObject post = new JsonObject().get(response.body().toString()).getAsJsonObject();
//                    Log.d(TAG, "onResponse: =======: " + post.get("message").getAsString());
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
////                JsonObject post = new JsonObject().get(response.body().toString()).getAsJsonObject();
////                Log.d(TAG, "onResponse: post.get(\"results\")" + post.get("results"));
//            }
//
//            @Override
//            public void onFailure(Call<Response> call, Throwable t) {
//                Log.d(TAG, "onFailure: Erorr------: " + t);
//            }
//        });
//
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        imageUri = null;
        mCapturedImage.setVisibility(View.GONE);
        mDetectButton.setVisibility(View.GONE);
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
        try {
            Log.d(TAG, "choosePhoto: ");
            Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to open gallery.", Toast.LENGTH_SHORT).show();

        }
    }

    private void takePhoto() {
        try {
            Log.d(TAG, "takePhoto: ");
            imageUri = null;
            mCapturedImage.setImageBitmap(null);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File file = new File(Environment.getExternalStorageDirectory(),
                    "MyPhoto.jpg");
            imageUri = Uri.fromFile(file);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(intent, REQUEST_CODE);
        } catch (Exception e) {
            Toast.makeText(MainActivity.this, "Failed to open camera", Toast.LENGTH_SHORT).show();
        }

    }


    public void uploadImage(byte[] imageBytes) {
        Log.d(TAG, "uploadImage: Uploading image........");

        OkHttpClient client = new OkHttpClient.Builder()
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS).build();

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageBytes);

        MultipartBody.Part multipart = MultipartBody.Part.createFormData("photo", "MyPhoto.jpg", requestFile);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl("http://0682204b.ngrok.io")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();

        PhotoClient photoClient = retrofit.create(PhotoClient.class);
        SkinImage skinImage;
        Call<SkinImage> call = photoClient.uploadPhoto(multipart);
        call.enqueue(new Callback<SkinImage>() {
            @Override
            public void onResponse(Call<SkinImage> call, retrofit2.Response<SkinImage> response) {
                try {
                    dialog.dismiss();
                    Log.d(TAG, "onResponse: Response: " + response);
                    Toast.makeText(MainActivity.this, "Succeess", Toast.LENGTH_SHORT).show();
                    SkinImage skinImage = response.body();
                    Log.d(TAG, "onResponse: message: " + skinImage.getMessage());

                    showDiseaseDetails(skinImage.getMessage(), skinImage.getPercentage());
                } catch (Exception e) {
                    dialog.dismiss();
                    Toast.makeText(MainActivity.this, "Please send image of infected area!!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<SkinImage> call, Throwable t) {
                dialog.dismiss();
                if (t instanceof SocketTimeoutException) {
                    Log.d(TAG, "Socket time out exception " + t);
                }
                Log.d(TAG, "onFailure: Failed: " + t);
                Toast.makeText(MainActivity.this, "Failed!!!", Toast.LENGTH_SHORT).show();

            }
        });
    }

    private void showDiseaseDetails(String output, String percentage) {
        AlertDialog.Builder builder
                = new AlertDialog
                .Builder(MainActivity.this);
        if (percentage.equals("null")) {
            percentage = "-";
        }
        builder.setMessage("Probability = " + percentage + "%");
        builder.setTitle(output);
        builder.setCancelable(false);
        builder.setPositiveButton(
                "Done",
                new DialogInterface
                        .OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog,
                                        int which) {
                        Log.d(TAG, "onClick: Okay");

                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onLocationChanged(Location location) {
//        locationText.setText("Latitude: " + location.getLatitude() + "\n Longitude: " + location.getLongitude());
        Log.d(TAG, "onLocationChanged: Latitude: " + location.getLatitude());
        Log.d(TAG, "onLocationChanged: Longitude: " + location.getLongitude());
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
//            locationText.setText(locationText.getText() + "\n" + addresses.get(0).getAddressLine(0) + ", " +
//                    addresses.get(0).getAddressLine(1) + ", " + addresses.get(0).getAddressLine(2));

            Log.d(TAG, "onLocationChanged: Address: " + addresses.get(0).getAddressLine(0));
            Log.d(TAG, "onLocationChanged: Address: " + addresses.get(1).getAddressLine(1));
            Log.d(TAG, "onLocationChanged: Address: " + addresses.get(2).getAddressLine(2));
        } catch (Exception e) {

        }

//        locationRetrofit();


    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(MainActivity.this, "Please Enable GPS and Internet", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }
}
package com.example.skindisease;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private final String TAG = MainActivity.class.getName();
    private ImageView mCapturedImage, mPlaceholderImage;
    private TextView mHeadingText, mSubText;
    private Button mDetectButton;
    private final int REQUEST_CODE = 11;
    private Uri imageUri = null;
    private Bitmap bitmap = null;

    private FirebaseStorage storage;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCapturedImage = findViewById(R.id.captured_photo);
        mPlaceholderImage = findViewById(R.id.image_placeholder);
        mDetectButton = findViewById(R.id.detect_button);
        mHeadingText = findViewById(R.id.empty_text);
        mSubText = findViewById(R.id.sub_text);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();

        mDetectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                uploadImage();
            }

        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: Result Code: " + resultCode);
        Log.d(TAG, "onActivityResult: Request Code: " + requestCode);
        String uri = imageUri.toString();
        Log.e("uri-:", uri);
        Toast.makeText(this, imageUri.toString(), Toast.LENGTH_LONG).show();

        try {
            mPlaceholderImage.setVisibility(View.GONE);
            mHeadingText.setVisibility(View.GONE);
            mSubText.setVisibility(View.GONE);
            mCapturedImage.setVisibility(View.VISIBLE);
            bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
            Drawable d = new BitmapDrawable(getResources(), bitmap);
            mCapturedImage.setImageDrawable(d);
            mDetectButton.setVisibility(View.VISIBLE);

        } catch (IOException e) {
            Toast.makeText(this, "Failed to take image!", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
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
            default:
                return super.onContextItemSelected(item);
        }
    }

    private void takePhoto() {
        imageUri = null;
        mCapturedImage.setImageBitmap(null);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStorageDirectory(),
                "MyPhoto.jpg");
        imageUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, REQUEST_CODE);

    }

//    private void uploadImage() {
//        if (imageUri != null) {
//            final ProgressDialog progressDialog = new ProgressDialog(this);
//            progressDialog.setTitle("Uploading...");
//            progressDialog.show();
//
//            StorageReference ref = storageReference.child("images/" + UUID.randomUUID().toString());
//            ref.putFile(imageUri)
//                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                            progressDialog.dismiss();
//                            Toast.makeText(MainActivity.this, "Uploaded", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            progressDialog.dismiss();
//                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
//                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot
//                                    .getTotalByteCount());
//                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
//                        }
//                    });
//        }
//
//    }

}

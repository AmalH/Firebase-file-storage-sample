package com.androidprojects.esprit.firebasefilestoragetutorial;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReferenceFromUrl("gs://fir-tutorials-2f1e8.appspot.com");

        /** UPLOADING **/
       findViewById(R.id.myImg).setOnClickListener(view -> {
           // file picker
           Intent i = new Intent();
           i.setType("image/*");
           i.setAction(Intent.ACTION_GET_CONTENT);
           startActivityForResult(Intent.createChooser(i, "Select Picture"), 100);
       });
        findViewById(R.id.btnUpload).setOnClickListener(view -> {
            // store the image
            StorageReference _storageRef = storageRef.child("myuploadedfile.png");
            (findViewById(R.id.myImg)).setDrawingCacheEnabled(true);
            (findViewById(R.id.myImg)).buildDrawingCache();
            Bitmap bitmap = (findViewById(R.id.myImg)).getDrawingCache();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            UploadTask uploadTask = _storageRef.putBytes(data);
            uploadTask.addOnFailureListener(exception -> {
                Toast.makeText(MainActivity.this, "TASK FAILED", Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(MainActivity.this, "TASK SUCCEEDED", Toast.LENGTH_SHORT).show();
                Uri downloadUrl = taskSnapshot.getDownloadUrl();
                String DOWNLOAD_URL = downloadUrl.getPath();
                Log.v("DOWNLOAD URL", DOWNLOAD_URL);
                Toast.makeText(MainActivity.this, DOWNLOAD_URL, Toast.LENGTH_SHORT).show();
            });
        });

        /** Downloading **/
        findViewById(R.id.btnDownload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final File localFile;
                try {
                    localFile = File.createTempFile("album2", "png");
                    storageRef.child("album2.png").getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                            ((ImageView)findViewById(R.id.myImg)).setImageBitmap(bitmap);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.d("FAILURE",exception.getMessage());
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

                /**final long ONE_MEGABYTE = 1024 * 1024;
                storageRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ((ImageView)findViewById(R.id.myImg)).setImageBitmap(bitmap);
                    }
                });**/

            }
        });

    }
   @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            if(requestCode==100){
                Uri selectedImageUri = data.getData();
                if (null != selectedImageUri) {
                    ((ImageView)findViewById(R.id.myImg)).setImageURI(selectedImageUri);
                }
            }
        }
    }
}

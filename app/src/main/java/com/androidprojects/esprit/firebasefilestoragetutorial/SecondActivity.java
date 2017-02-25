package com.androidprojects.esprit.firebasefilestoragetutorial;

import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Amal on 18/02/2017.
 */

public class SecondActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    final StorageReference storageRef = storage.getReferenceFromUrl("gs://fir-tutorials-2f1e8.appspot.com");

    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_second);

        findViewById(R.id.btnUploadNewFile).setOnClickListener(view -> {
            InputStream stream = getResources().openRawResource(R.raw.test_upload);
            UploadTask uploadTask = storageRef.child("test_upload.txt").putStream(stream);
        });
        findViewById(R.id.btnUploadToExistingFile).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    File file = File.createTempFile("test", "txt");
                    UploadTask uploadTask = storageRef.child("test.txt").putFile(Uri.fromFile(file));
            } catch( IOException e ) {
                    Log.d("FAILED",e.getMessage());
            }
            }
        });
    }
}

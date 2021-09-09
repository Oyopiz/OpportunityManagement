package com.nayatest.opportunity;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.nayatest.opportunity.activities.HomeActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class APPLY extends AppCompatActivity {
    EditText bnemail;
    EditText bnphone;
    EditText bnlocation;
    EditText bnyears;
    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri;
    private ImageButton bnimageBn;
    Button bnsubmit;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply);
        bnemail=findViewById(R.id.emailme);
        bnlocation=findViewById(R.id.location);
        bnphone=findViewById(R.id.phoneme);
        bnimageBn=findViewById(R.id.image_bnme);
        bnyears=findViewById(R.id.experience);
        bnsubmit=findViewById(R.id.submit);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Accounts");
        bnimageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        bnsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                publishPost();
            }
        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == GALLERY_REQUEST && resultCode == RESULT_OK) {
            mImageUri = data.getData();
            bnimageBn.setImageURI(mImageUri);
        }
    }


    private void publishPost() {
        final String editEmail = bnemail.getText().toString().trim();
        final String editPhone = bnphone.getText().toString().trim();
        final String editLocation = bnlocation.getText().toString().trim();
        final String editYears = bnphone.getText().toString().trim();
        if(!editEmail.isEmpty() && !editPhone.isEmpty() && !editLocation.isEmpty()
                && !editYears.isEmpty()) {

            final StorageReference filePath = mStorageRef.child("Profilepic").child(mImageUri.getLastPathSegment());

            UploadTask uploadTask = filePath.putFile(mImageUri);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();
                        String downloadUrl = downloadUri.toString();

                        DatabaseReference newPost = mDatabase.push();
                        newPost.child("Email").setValue(editEmail);
                        newPost.child("Phone").setValue(editPhone);
                        newPost.child("Location").setValue(editLocation);
                        newPost.child("Experience").setValue(editYears);
                        newPost.child("imageUrl").setValue(downloadUrl);

                        Toast.makeText(getApplicationContext(), "Applicaion Successfully!", Toast.LENGTH_SHORT).show();
                        Toast.makeText(getApplicationContext(), "We are reviewing your information please wait for an Email!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(APPLY.this, HomeActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Error... Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please provide a profile picture and fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
    }
}

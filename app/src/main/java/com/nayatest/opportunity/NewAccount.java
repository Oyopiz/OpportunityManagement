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

public class NewAccount extends AppCompatActivity {
EditText name;
EditText address;
EditText Email;
EditText phone;

    private static final int GALLERY_REQUEST = 1;
    private Uri mImageUri;
    private ImageButton imageBn;
Button create;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
        name=findViewById(R.id.name);
        address=findViewById(R.id.adress);
        Email=findViewById(R.id.email);
        phone=findViewById(R.id.phone);
        imageBn=findViewById(R.id.image_bn);
        create=findViewById(R.id.create);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Accounts");
        imageBn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQUEST);
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
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
            imageBn.setImageURI(mImageUri);
        }
    }


    private void publishPost() {
        final String editcompname = name.getText().toString().trim();
        final String editaddress = address.getText().toString().trim();
        final String editemail = Email.getText().toString().trim();
        final String editphone = phone.getText().toString().trim();
        if(!editcompname.isEmpty() && !editaddress.isEmpty() && !editemail.isEmpty()
                && !editphone.isEmpty()) {

            final StorageReference filePath = mStorageRef.child("AccountImages").child(mImageUri.getLastPathSegment());

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
                        newPost.child("Company Name").setValue(editcompname);
                        newPost.child("Address").setValue(editaddress);
                        newPost.child("Email").setValue(editemail);
                        newPost.child("Phone").setValue(editphone);
                        newPost.child("imageUrl").setValue(downloadUrl);

                        Toast.makeText(getApplicationContext(), "Added Successfully!", Toast.LENGTH_SHORT).show();

                        startActivity(new Intent(NewAccount.this, HomeActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Error... Please Try Again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            Toast.makeText(this, "Please provide a company Image and fill in all the fields.", Toast.LENGTH_SHORT).show();
        }
    }
}

package com.example.asitakipson.fragments;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.asitakipson.R;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;


public class MyProfil extends Fragment {
    ImageView imageView;
    EditText editText;
    Uri uriProfileImage;
    ProgressBar progressBar;
    String profileImageUrl;
    String uID;
    FirebaseAuth mAuth;
    private static final int CHOOSE_IMAGE = 101;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_profil, container, false);
        mAuth = FirebaseAuth.getInstance();
        uID = mAuth.getUid();
        editText = view.findViewById(R.id.editTextDisplayName);
        imageView = view.findViewById(R.id.imageView);
        progressBar = view.findViewById(R.id.progresbar) ;

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showImageChooser();
            }
        });

        loadUserInformation();

        view.findViewById(R.id.buttonKaydet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveUserInformation();
            }
        });

        return  view;
    }

    private void loadUserInformation() {
        final FirebaseUser user = mAuth.getCurrentUser();

        if (user != null) {
            if (user.getPhotoUrl() != null) {
                Glide.with(this)
                        .load(user.getPhotoUrl().toString())
                        .into(imageView)
                ;
            }

            if (user.getDisplayName() != null) {
                editText.setText(user.getDisplayName());
            }

        /*    if (user.isEmailVerified()) {
                textView.setText("Email Verified");
            } else {
                textView.setText("Email Not Verified (Click to Verify)");
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                Toast.makeText(ProfilActivity.this, "Verification Email Sent", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }*/
        }
    }
    private void saveUserInformation() {


        String displayName = editText.getText().toString();

        if (displayName.isEmpty()) {
            editText.setError("İsim Girmeniz Lazım");
            editText.requestFocus();
            return;
        }

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && profileImageUrl != null) {
            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .setPhotoUri(Uri.parse(profileImageUrl))
                    .build();

            user.updateProfile(profile)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(getActivity(), "Profil Güncellendi", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Glide.get(getActivity()).clearDiskCache();
                }
            }).start();
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CHOOSE_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            uriProfileImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uriProfileImage);
                imageView.setImageBitmap(bitmap);

                uploadImageToFirebaseStorage(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImageToFirebaseStorage(final Bitmap bitmap) {
        StorageReference profileImageRef =
                FirebaseStorage.getInstance().getReference();
        //    StorageReference profilImage = profileImageRef.child("profilepics");
        final StorageReference profiImageRef = profileImageRef.child("profilepics/"+FirebaseAuth.getInstance().getCurrentUser().getUid()+".jpeg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask  = profiImageRef.putBytes(data);

        progressBar.setVisibility(View.VISIBLE);
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return profiImageRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    profileImageUrl = downloadUri.toString();
                    //  Toast.makeText(getBaseContext(), "Upload success! URL - " + downloadUri.toString() , Toast.LENGTH_SHORT).show();

                    imageView.setImageBitmap(bitmap);
                    progressBar.setVisibility(View.GONE);
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }

    private void showImageChooser(){


        Dexter.withActivity(getActivity())
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        // permission is granted, open the gallery
                        Intent intent = new Intent();
                        intent.setType("image/*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
                        startActivityForResult(Intent.createChooser(intent,"Bir fotograf seçiniz"), CHOOSE_IMAGE  );
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        // check for permanent denial of permission
                        if (response.isPermanentlyDenied()) {
                            // navigate user to app settings
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                })
                .onSameThread()
                .check();
    }
}

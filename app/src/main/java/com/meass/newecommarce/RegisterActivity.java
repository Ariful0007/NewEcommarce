package com.meass.newecommarce;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kaopiz.kprogresshud.KProgressHUD;


import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;

public class RegisterActivity extends AppCompatActivity {
    private EditText edtname, edtemail, edtpass, edtcnfpass, edtnumber;
    private String check,name,email,password,mobile,profile;
    CircleImageView image;
    ImageView upload;
    private Uri mainImageURI = null;
    boolean IMAGE_STATUS = false;
    Bitmap profilePicture;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private final String TAG = this.getClass().getSimpleName();
    private String userId;

    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;

    private Bitmap compressedImageFile;
    private KProgressHUD progressDialog;
    ImageButton image_button;
    ImageView imageView;
    private final int PICK_IMAGE_REQUEST = 71;
    private Uri filePath;//Firebase

    DocumentReference documentReference;
    Button floatingActionButton;
    FirebaseStorage storage;
    private static final int CAMERA_REQUEST = 1888;
    Button generate_btn;
    //doctor
    private static final int READCODE = 1;
    private static final int WRITECODE = 2;

    private Uri mainImageUri = null;
    FirebaseAuth firebaseAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth=FirebaseAuth.getInstance();

        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();

        Typeface typeface = ResourcesCompat.getFont(this, R.font.blacklist);
        TextView appname = findViewById(R.id.appname);
        appname.setTypeface(typeface);
        progressDialog=KProgressHUD.create(RegisterActivity.this);

        FirebaseApp.initializeApp(RegisterActivity.this);

        mAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();


        upload=findViewById(R.id.uploadpic);
        image=findViewById(R.id.profilepic);
        edtname = findViewById(R.id.name);
        edtemail = findViewById(R.id.email);
        edtpass = findViewById(R.id.password);
        edtcnfpass = findViewById(R.id.confirmpassword);
        edtnumber = findViewById(R.id.number);
        edtnumber.setVisibility(View.INVISIBLE);
        edtnumber.setText(edtemail.getText().toString());

        edtname.addTextChangedListener(nameWatcher);
        //edtemail.addTextChangedListener(emailWatcher);
        edtpass.addTextChangedListener(passWatcher);
        edtcnfpass.addTextChangedListener(cnfpassWatcher);
        edtemail.addTextChangedListener(numberWatcher);


        //validate user details and register user

        Button register_button=findViewById(R.id.register);

        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String fullname = edtname.getText().toString();
                String fillpass = edtpass.getText().toString();
                String full2 = edtcnfpass.getText().toString();
                String fullnumber = edtemail.getText().toString();
                if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(fillpass) ||
                        TextUtils.isEmpty(full2) || TextUtils.isEmpty(fullnumber)) {
                    Toasty.error(getApplicationContext(), "Error give right information", Toast.LENGTH_SHORT, true).show();
                    return;
                }


                else {

                    progress_check();
                    name=edtname.getText().toString();
                    email=edtemail.getText().toString()+"@gmail.com";
                    password=edtcnfpass.getText().toString();
                    mobile=edtnumber.getText().toString();
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                progressDialog.dismiss();
                                user = mAuth.getCurrentUser();
                                userId = mAuth.getCurrentUser().getUid();
                                uploadImage();
                            }
                        }
                    });

                }
            }
        });




        register_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullname = edtname.getText().toString();
                String fillpass = edtpass.getText().toString();
                String full2 = edtcnfpass.getText().toString();
                String fullnumber = edtemail.getText().toString();
                if (TextUtils.isEmpty(fullname) || TextUtils.isEmpty(fillpass) ||
                        TextUtils.isEmpty(full2) || TextUtils.isEmpty(fullnumber)) {
                    Toasty.error(getApplicationContext(), "Error give right information", Toast.LENGTH_SHORT, true).show();
                    return;
                } else {

                    //TODO AFTER VALDATION
                    if (validateName() && validatePass() && validateCnfPass()) {

                        name = edtname.getText().toString();
                        email = edtemail.getText().toString() + "@gmail.com";
                        password = edtcnfpass.getText().toString();
                        mobile = edtnumber.getText().toString();

                        try {

                            progressDialog = KProgressHUD.create(RegisterActivity.this)
                                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                                    .setLabel("Please wait")
                                    .setCancellable(false)
                                    .setAnimationSpeed(2)
                                    .setDimAmount(0.5f)
                                    .show();


                            //Validation Success
                            //convertBitmapToString(profilePicture);

                            // Creating New Account with Email and Password
                            Log.d(TAG + "_TXN", "1. CREATING ACCOUNT");
                            mAuth.createUserWithEmailAndPassword(email, password)
                                    .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            if (task.isSuccessful()) {
                                                if (filePath != null) {

                                                    Log.d(TAG + "_TXN", "2. ACCOUNT CREATED");

                                                    // Sign in success, update UI with the signed-in user's information
                                                    Log.d(TAG + "_SS", "createUserWithEmail:success");


                                                    user = mAuth.getCurrentUser();
                                                    userId = mAuth.getCurrentUser().getUid();
                                                    Log.d(TAG + "_UID", userId);


                                                    final StorageReference image_path = storageReference.child("profile_images").child(userId + ".jpg");


                                                    Log.d(TAG + "_TXN", "3. SAVING MAIN IMG");
                                                    // Saving Image to Firebase Storage
                                                    image_path.putFile(filePath).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                        @Override
                                                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                            if (!task.isSuccessful()) {
                                                                Log.d(TAG + "_TXN_ERR_1", "ERROR IN SAVING MAIN IMAGE");
                                                                progressDialog.dismiss();
                                                                throw task.getException();
                                                            }

                                                            // Continue with the task to get the download URL
                                                            return image_path.getDownloadUrl();
                                                        }
                                                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Uri> task) {

                                                            if (task.isSuccessful()) {

                                                                Log.d(TAG + "_TXN", "4. MAIN IMAGE SAVED");
                                                                Uri downloadUri = task.getResult();

                                                                //compressBitmap(downloadUri,name, mobile,email);
                                                                storeToFirestore1(name, edtemail.getText().toString(), downloadUri.toString(), email);


                                                            } else {
                                                                progressDialog.dismiss();
                                                                Log.d(TAG + "_TXN_ERR_2", "ERROR IN SAVING MAIN IMAGE");
                                                                String errorMessage = task.getException().getMessage();
                                                                Log.d(TAG + "_ERR", "UPLOAD Error : " + errorMessage);

                                                            }
                                                        }

                                                    });


                                                } else {
                                                    progressDialog.dismiss();
                                                    // If sign in fails, display a message to the user.
                                                    mAuth.createUserWithEmailAndPassword(email, password)
                                                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                                @Override
                                                                public void onComplete(@NonNull Task<AuthResult> task) {
                                                                    if (task.isSuccessful()) {
                                                                        String image22="https://firebasestorage.googleapis.com/v0/b/mydex-91780.appspot.com/o/profile_images%2FNt4rITrPbqc9e1AHPKPaLb4IcFI3.jpg?alt=media&token=d2da7b65-ed6a-41c9-8d1c-ade16adddc3a";

                                                                        Log.w(TAG + "_ERR", "createUserWithEmail:failure", task.getException());
                                                                        //Toasty.error(RegisterActivity.this, "Failed to Register", Toast.LENGTH_LONG, true).show();
                                                                        storeToFirestore1(name, edtemail.getText().toString(), image22.toString(), email);
                                                                        progress_check();

                                                                        Toasty.error(RegisterActivity.this, "Please Insert Image", Toast.LENGTH_LONG, true).show();
                                                                    }
                                                                }
                                                            });

                                                }

                                            } else {
                                                progressDialog.dismiss();
                                                mAuth.createUserWithEmailAndPassword(email, password)
                                                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                                if (task.isSuccessful()) {
                                                                    String image22="https://firebasestorage.googleapis.com/v0/b/mydex-91780.appspot.com/o/profile_images%2FNt4rITrPbqc9e1AHPKPaLb4IcFI3.jpg?alt=media&token=d2da7b65-ed6a-41c9-8d1c-ade16adddc3a";

                                                                    Log.w(TAG + "_ERR", "createUserWithEmail:failure", task.getException());
                                                                    //Toasty.error(RegisterActivity.this, "Failed to Register", Toast.LENGTH_LONG, true).show();
                                                                    storeToFirestore1(name, edtemail.getText().toString(), image22.toString(), email);
                                                                    progress_check();

                                                                    Toasty.error(RegisterActivity.this, "Please Insert Image", Toast.LENGTH_LONG, true).show();
                                                                }
                                                            }
                                                        });
                                            }
                                        }
                                    });

                        } catch (Exception e) {
                            Log.w(TAG + "_ERR", "Error: " + e.getMessage());
                            Toasty.error(RegisterActivity.this, "Failed to Register", Toast.LENGTH_LONG, true).show();

                        }


                    }
                }
            }
        });




        //Take already registered user to login page

        final TextView loginuser=findViewById(R.id.login_now);
        loginuser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this,Login2.class));
                finish();
            }
        });

        //take user to reset password



        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });


    }
    private void progress_check()
    {
        progressDialog  =  KProgressHUD.create(RegisterActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Please wait")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f)
                .show();
    }

    private void uploadImage() {

        if(filePath != null)
        {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            final StorageReference image_path = storageReference.child("profile_images").child(userId + ".jpg");
            image_path.putFile(filePath)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Task<Uri> uriTask=taskSnapshot.getStorage().getDownloadUrl();
                            while (!uriTask.isSuccessful());
                            final Uri downloadUri=uriTask.getResult();



                            if (uriTask.isSuccessful()) {
                               /* HashMap<String,Object> hashMap=new HashMap<>();

                                hashMap.put("image",downloadUri.toString());
                                hashMap.put("product_name",product_name.getText().toString());
                                hashMap.put("product_price",product_price.getText().toString());
                                hashMap.put("old_price",old_price.getText().toString());
                                hashMap.put("stock",stock.getText().toString());*/

                                storeToFirestore1(name, edtemail.getText().toString(), downloadUri.toString(), email);



                            }
                            else {
                                progressDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Failed", Toast.LENGTH_SHORT).show();
                            }

                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                    .getTotalByteCount());
                            progressDialog.setMessage("Uploaded "+(int)progress+"%");
                        }
                    });
        }
        else {
            progress_check();
            String image22="https://firebasestorage.googleapis.com/v0/b/mydex-91780.appspot.com/o/profile_images%2FNt4rITrPbqc9e1AHPKPaLb4IcFI3.jpg?alt=media&token=d2da7b65-ed6a-41c9-8d1c-ade16adddc3a";
            storeToFirestore2(name, edtemail.getText().toString(),image22, email);
        }

    }

    private void storeToFirestore2(String name, String toString, String image22, String email) {
        Log.d(TAG+"_TXN","8. SAVING DATA TO FIRESTORE");
        // Storing data on Firestore...

        final Map<String, String> userMap = new HashMap<>();
        userMap.put("name",name);
        userMap.put("image",image22.toString());
        userMap.put("number", toString);
        userMap.put("email",email);



        firebaseFirestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful())
                {

                    // Sending Verification Link via Email
                    Log.d(TAG+"_TXN","9. DATA SAVED");
                    Log.d(TAG+"_TXN","10. SENDING EMAIL");
                    firebaseFirestore.collection("User2").document(firebaseAuth.getCurrentUser().getEmail())
                            .set(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(),Login2.class));
                                        finish();
                                    }
                                }
                            });





                } else {
                    Log.d(TAG+"_ERR5","ERROR IN SAVING DATA");
                    progressDialog.dismiss();
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this,"FIRESTORE Error : "+errorMessage,Toast.LENGTH_LONG).show();

                }

            }
        });
    }

    private void storeToFirestore1(String name, String toString, String toString1, String email) {
        Log.d(TAG+"_TXN","8. SAVING DATA TO FIRESTORE");
        // Storing data on Firestore...

        final Map<String, String> userMap = new HashMap<>();
        userMap.put("name",name);
        userMap.put("image",toString1.toString());
        userMap.put("number", toString);
        userMap.put("email",email);



        firebaseFirestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful())
                {

                    // Sending Verification Link via Email
                    Log.d(TAG+"_TXN","9. DATA SAVED");
                    Log.d(TAG+"_TXN","10. SENDING EMAIL");
                    firebaseFirestore.collection("User2").document(firebaseAuth.getCurrentUser().getEmail())
                            .set(userMap)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        progressDialog.dismiss();
                                        startActivity(new Intent(getApplicationContext(),Login2.class));
                                        finish();
                                    }
                                }
                            });





                } else {
                    Log.d(TAG+"_ERR5","ERROR IN SAVING DATA");
                    progressDialog.dismiss();
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this,"FIRESTORE Error : "+errorMessage,Toast.LENGTH_LONG).show();

                }

            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK
                && data != null && data.getData() != null )
        {
            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                image.setImageBitmap(bitmap);

            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }





        /*if (requestCode == 1000 && resultCode == Activity.RESULT_OK && data != null) {
            //Image Successfully Selected
            try {
                //parsing the Intent data and displaying it in the imageview
                Uri imageUri = data.getData();//Geting uri of the data
                InputStream imageStream = getContentResolver().openInputStream(imageUri);//creating an imputstrea
                profilePicture = BitmapFactory.decodeStream(imageStream);//decoding the input stream to bitmap
                image.setImageBitmap(profilePicture);
                IMAGE_STATUS = true;//setting the flag
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }*/


    private void convertBitmapToString(Bitmap profilePicture) {
            /*
                Base64 encoding requires a byte array, the bitmap image cannot be converted directly into a byte array.
                so first convert the bitmap image into a ByteArrayOutputStream and then convert this stream into a byte array.
            */
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        profilePicture.compress(Bitmap.CompressFormat.PNG, 50, byteArrayOutputStream);
        byte[] array = byteArrayOutputStream.toByteArray();
        profile = Base64.encodeToString(array, Base64.DEFAULT);
    }

    private boolean validateNumber() {

        check = edtnumber.getText().toString();
        Log.e("inside number",check.length()+" ");
        if (check.length()>10) {
            return false;
        }else if(check.length()<10){
            return false;
        }
        return true;
    }

    private boolean validateCnfPass() {

        check = edtcnfpass.getText().toString();

        return check.equals(edtpass.getText().toString());
    }

    private boolean validatePass() {


        check = edtpass.getText().toString();

        if (check.length() < 4 || check.length() > 20) {
            return false;
        } else if (!check.matches("^[A-za-z0-9@]+")) {
            return false;
        }
        return true;
    }

    private boolean validateEmail() {

        check = edtemail.getText().toString();

        if (check.length() < 4 || check.length() > 40) {
            return false;
        } else if (!check.matches("^[A-za-z0-9.@]+")) {
            return false;
        } else if (!check.contains("@") || !check.contains(".")) {
            return false;
        }

        return true;
    }

    private boolean validateName() {

        check = edtname.getText().toString();

        return !(check.length() < 4 || check.length() > 20);

    }

    private boolean validateProfile() {
        if (!IMAGE_STATUS)
            Toasty.info(RegisterActivity.this,"Select A Profile Picture", Toast.LENGTH_LONG).show();
        return IMAGE_STATUS;
    }

    //TextWatcher for Name -----------------------------------------------------

    TextWatcher nameWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtname.setError("Name Must consist of 4 to 20 characters");
            }
        }

    };

    //TextWatcher for Email -----------------------------------------------------

    TextWatcher emailWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 40) {
                edtemail.setError("Email Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9.@]+")) {
                edtemail.setError("Only . and @ characters allowed");
            } else if (!check.contains("@") || !check.contains(".")) {
                edtemail.setError("Enter Valid Email");
            }

        }

    };

    //TextWatcher for pass -----------------------------------------------------

    TextWatcher passWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length() < 4 || check.length() > 20) {
                edtpass.setError("Password Must consist of 4 to 20 characters");
            } else if (!check.matches("^[A-za-z0-9@]+")) {
                edtemail.setError("Only @ special character allowed");
            }
        }

    };

    //TextWatcher for repeat Password -----------------------------------------------------

    TextWatcher cnfpassWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (!check.equals(edtpass.getText().toString())) {
                edtcnfpass.setError("Both the passwords do not match");
            }
        }

    };


    //TextWatcher for Mobile -----------------------------------------------------

    TextWatcher numberWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            //none
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            //none
        }

        @Override
        public void afterTextChanged(Editable s) {

            check = s.toString();

            if (check.length()>11) {
                edtnumber.setError("Number cannot be grated than 11 digits");
            }else if(check.length()<11){
                edtnumber.setError("Number should be 11 digits");
            }
        }

    };

    @Override
    protected void onResume() {
        super.onResume();
        //check Internet Connection
        new CheckInternetConnection(this).checkConnection();
    }

    /*
    FOR COMPRESSING IMAGE
    private void compressBitmap(final Uri downloadUri, final String user_name, final String user_number, final String user_email) {

        Log.d(TAG+"_TXN","5. COMPRESSING IMAGE");


        Log.d(TAG+"_MAIN_URI", String.valueOf(downloadUri));

        File newImageFile = new File(mainImageURI.getPath());

        Log.d(TAG, String.valueOf(newImageFile));

        try {

            // Reducing Image Quality...
            compressedImageFile = new Compressor(RegisterActivity.this)
                    .setMaxWidth(100)
                    .setMaxHeight(100)
                    .setQuality(2)
                    .compressToBitmap(newImageFile);

        } catch (IOException e) {
            Log.d(TAG+"_ERR3","ERROR IN COMPRESSING IMAGE");
            progressDialog.dismiss();
            String errorMessage = e.getMessage();
            Toast.makeText(RegisterActivity.this,"Upload Error 1 : "+errorMessage,Toast.LENGTH_LONG).show();

        }

        Log.d(TAG+"_TXN","6. IMAGE COMPRESSED");
        Log.d(TAG, String.valueOf(compressedImageFile));

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] thumbData = baos.toByteArray();


        // File path for Thumb Image...

        String user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();
        final StorageReference thumbFilePath = storageReference.child("profile_images/thumbs").child(user_id+".jpg");


        //Uploading of thumb img...
        final UploadTask uploadTask = thumbFilePath.putBytes(thumbData);

        //Getting URI of thumb img...
        Log.d(TAG+"_TXN","6. SAVING THUMB IMAGE");
        uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    Log.d(TAG+"_ERR_4","ERROR IN SAVINNG THUMB IMAGE");
                    progressDialog.dismiss();
                    throw task.getException();
                }

                return thumbFilePath.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {

                if(task.isSuccessful())
                {


                    // Downloadable Thumb URI...
                    final Uri thumb_Uri = task.getResult();


                    // Success Listener for uploading Thumb URI...
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            Log.d(TAG+"_TXN","7. THUMB IMAGE SAVED");
                            Toast.makeText(RegisterActivity.this,"The Image is Uploaded",Toast.LENGTH_LONG).show();
                            storeToFirestore(user_name, user_number,downloadUri, thumb_Uri, user_email);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            Log.d(TAG+"_ERR_5","ERROR IN SAVINNG THUMB IMAGE");
                            String errorMessage = e.getMessage();
                            Toast.makeText(RegisterActivity.this,"Upload Error 2 : "+errorMessage,Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    Log.d(TAG+"_ERR_6","ERROR IN SAVINNG THUMB IMAGE");
                    progressDialog.dismiss();
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this,"Upload Error 3 : "+errorMessage,Toast.LENGTH_LONG).show();

                }
            }
        });
    } */

    private void storeToFirestore(String user_name, String user_number,  Uri downloadUri, String user_email) {


        Log.d(TAG+"_TXN","8. SAVING DATA TO FIRESTORE");
        // Storing data on Firestore...

        Map<String, String> userMap = new HashMap<>();
        userMap.put("name",user_name);
        userMap.put("image",downloadUri.toString());
        userMap.put("number", user_number);
        userMap.put("email",user_email);



        firebaseFirestore.collection("Users").document(userId).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {


                if(task.isSuccessful())
                {

                    // Sending Verification Link via Email
                    Log.d(TAG+"_TXN","9. DATA SAVED");
                    Log.d(TAG+"_TXN","10. SENDING EMAIL");
                    user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(),Login2.class));
                                finish();

                            }
                        }
                    });

                    new Handler().postDelayed(new Runnable() {

                        /*
                         * Showing splash screen with a timer. This will be useful when you
                         * want to show case your app logo / company
                         */

                        @Override
                        public void run() {
                            // This method will be executed once the timer is over
                            // Start your app main activity
                            startActivity(new Intent(RegisterActivity.this,Login2.class));
                            finish();
                        }
                    }, 2000);



                } else {
                    Log.d(TAG+"_ERR5","ERROR IN SAVING DATA");
                    progressDialog.dismiss();
                    String errorMessage = task.getException().getMessage();
                    Toast.makeText(RegisterActivity.this,"FIRESTORE Error : "+errorMessage,Toast.LENGTH_LONG).show();

                }

            }
        });



    }
}

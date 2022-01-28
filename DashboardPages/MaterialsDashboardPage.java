package com.kenoDigital.rookiemate.DashboardPages;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kenoDigital.rookiemate.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

public class MaterialsDashboardPage extends AppCompatActivity {

    Button backButton,addMaterialButton;
    LinearLayout addCardList;
    Uri selectedImageUri;
    Uri defaultImageUri;

    StorageReference storageReference;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String username;

    int SELECT_PICTURE = 8055;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_materials_dashboard_page);

        backButton = (Button) findViewById(R.id.BackButton);
        addMaterialButton = (Button) findViewById(R.id.addMaterialButton);
        addCardList=findViewById(R.id.addCardList);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        storageReference= FirebaseStorage.getInstance().getReference();

        defaultImageUri = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE +
                "://" + getResources().getResourcePackageName(R.drawable.material_icon_dashboard)
                + '/' + getResources().getResourceTypeName(R.drawable.material_icon_dashboard) + '/' + getResources().getResourceEntryName(R.drawable.material_icon_dashboard) );

        String uid = fAuth.getCurrentUser().getUid();
        DocumentReference userInfoDocumentReference = fStore.collection("Users").document(uid);
        userInfoDocumentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if(e!=null)
                    e.printStackTrace();
                else{
                    username = (documentSnapshot.getString("fullName"));
                }
            }
        });

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        addMaterialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAddMaterialsDialogue();
            }
        });

    }

    private void showAddMaterialsDialogue() {
        final Dialog dialog = new Dialog(MaterialsDashboardPage.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(true);
        dialog.setContentView(R.layout.add_material_dialogue);

        final EditText materialNameEditText = (EditText) dialog.findViewById(R.id.materialNameEditText);
        final EditText materialDesEditText = (EditText) dialog.findViewById(R.id.materialDesEditText);
        final EditText materialLinkEditText = (EditText) dialog.findViewById(R.id.materialLinkEditText);
        final Button selectImageButton = (Button) dialog.findViewById(R.id.selectImageButton);
        final Button uploadButton = (Button) dialog.findViewById(R.id.uploadButton);
        final ProgressBar spinner = (ProgressBar) dialog.findViewById(R.id.spinner);

        selectImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setType("image/*");
                i.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(i, "Select Picture"), SELECT_PICTURE);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadButton.setEnabled(false);
                spinner.setVisibility(View.VISIBLE);
                String materialName = materialNameEditText.getText().toString();
                String materialLink = materialLinkEditText.getText().toString();
                String materialDes = materialDesEditText.getText().toString();

                if(materialName.isEmpty()){
                    materialNameEditText.setError("Required!");
                    return;
                }
                if(materialDes.isEmpty()){
                    materialDesEditText.setError("Required!");
                    return;
                }
                if(materialLink.isEmpty()){
                    materialLinkEditText.setError("Required!");
                    return;
                }
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
                String format = simpleDateFormat.format(new Date());
                uploadDataToCloud(materialName,materialDes,materialLink,format);
                spinner.setVisibility(View.GONE);
                Toast.makeText(MaterialsDashboardPage.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                addCard(materialName,materialDes,materialLink,format);
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private void uploadDataToCloud(String materialName, String materialDes, String materialLink, String format) {
        if(fAuth.getCurrentUser()!=null){
            String cloudUserWiseMaterialAddress = fAuth.getUid()+"/"+"Materials"+"/"+format;
            String cloudMaterialViewAddress = "Materials"+"/"+format+"/"+fAuth.getUid();
            StorageReference cloudUserWiseMaterialFolder = storageReference.child(cloudUserWiseMaterialAddress);
            StorageReference cloudMaterialViewFolder = storageReference.child(cloudMaterialViewAddress);



            Map<String, Object> materialDetails = new HashMap<>();
            materialDetails.put("Name",materialName);
            materialDetails.put("Username",username);
            materialDetails.put("Description",materialDes);
            materialDetails.put("Link",materialLink);
            materialDetails.put("TimeStamp",format);
            materialDetails.put("UserWisePath",cloudUserWiseMaterialAddress);
            materialDetails.put("MaterialViewPath",cloudMaterialViewAddress);

            fStore.collection("Users").document(fAuth.getUid()).collection("Materials").document(format).set(materialDetails);
            fStore.collection("Materials").document(format).set(materialDetails);

            if(selectedImageUri != null) {
                cloudUserWiseMaterialFolder.putFile(selectedImageUri)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("Error tag",e.toString());
                        return;
                    }
                });

                cloudMaterialViewFolder.putFile(selectedImageUri)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d("Error tag",e.toString());
                                return;
                            }
                        });
            }else {
                cloudUserWiseMaterialFolder.putFile(defaultImageUri);
            }
        }
        return;
    }

    private void addCard(String materialName, String materialDes, String materialLink, String format){
        View cardView=getLayoutInflater().inflate(R.layout.add_card_layout,null,false);
        View spaceBetween=getLayoutInflater().inflate(R.layout.space_between_cards,null,false);
        TextView uploadedPostNameTextView=(TextView)cardView.findViewById(R.id.uploadedPostNameTextView);
        TextView descriptionTextView=(TextView)cardView.findViewById(R.id.descriptionTextView);
        TextView uploaderNameTextView = (TextView) cardView.findViewById(R.id.uploaderNameTextView);
        TextView uploadedPostDateTextView = (TextView) cardView.findViewById(R.id.uploadedPostDateTextView);
        Button linkButton = (Button) cardView.findViewById(R.id.linkButton);
        ImageView postImageView = (ImageView) cardView.findViewById(R.id.postImageView);

        uploadedPostNameTextView.setText(materialName);
        descriptionTextView.setText(materialDes);

        if (null != selectedImageUri) {
            postImageView.setImageURI(selectedImageUri);
        }else{
            postImageView.setBackgroundResource(R.drawable.material_ic_small);
        }

        uploaderNameTextView.setText(username);

        cardView.setBackgroundResource(R.drawable.materials_card_bg);
        uploadedPostDateTextView.setText(format);
        addCardList.addView(cardView);
        addCardList.addView(spaceBetween);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                selectedImageUri = data.getData();
            }
        }
    }

}
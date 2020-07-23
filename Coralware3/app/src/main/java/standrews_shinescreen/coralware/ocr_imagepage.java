package standrews_shinescreen.coralware;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.MenuItem;
import android.view.Surface;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ocr_imagepage extends AppCompatActivity {
    public static final int CAMERA_OPEN_CODE = 300;
    public static final int GALLERY_OPEN_CODE = 302;
    private Bitmap bitmap;
    private ImageView image_view;
    private EditText editText;
    private Button cancel, save;
    private Uri imageURI;
    private String mCameraFileName;
    private static final int GALLERY_REQUEST_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    DatabaseHelper mDatabasehelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_ocr_imagepage);


        editText = findViewById(R.id.editText_save);
        image_view = findViewById(R.id.image_view);
        ImageButton captureImagebutton = (ImageButton) findViewById(R.id.photoButton);
        cancel = (Button) findViewById(R.id.button_cancel);
        save = (Button) findViewById(R.id.button_save);
        ImageButton galleryButton = (ImageButton) findViewById(R.id.galleryButton);

        editText.setMovementMethod(new ScrollingMovementMethod());


        captureImagebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();
            }
        });

        galleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askGalleryPermission();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ocr_imagepage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String newEntry = editText.getText().toString();
                if (editText.length() != 0) {
                    AddData(newEntry);
                    editText.setText("");

                    Intent intent = new Intent(ocr_imagepage.this, MainActivity.class);
                    startActivity(intent);

                } else {
                    Toast.makeText(ocr_imagepage.this, "Enter data first", Toast.LENGTH_SHORT).show();

                }

            }
        });
    }




    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST_CODE);
        } else {
            activateCamera();
        }
    }

    private void askGalleryPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST_CODE);
        } else {
            openGallery();
        }
    }

    private void activateCamera() {
        Intent cameraOpen = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraOpen, CAMERA_OPEN_CODE);

    }

    private void openGallery() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(galleryIntent, GALLERY_OPEN_CODE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                activateCamera();
            } else {
                Toast.makeText(this, "Give Coralware permission to use your camera", Toast.LENGTH_SHORT).show();
            }
        }
        if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Give Coralware permission to access your gallery", Toast.LENGTH_SHORT).show();
            }
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap image = (Bitmap) data.getExtras().get("data");

        if (requestCode == CAMERA_OPEN_CODE && resultCode == RESULT_OK && data != null) {

            /*imageURI = data.getData();
            image_view.setImageURI(imageURI);
            image_view.setVisibility(View.VISIBLE);
            runTextRecognition();*/
            image_view.setImageBitmap(image);
            runTextRecognition();
        } else {

            if (requestCode == GALLERY_OPEN_CODE && resultCode == RESULT_OK && data != null) {
                image_view.setImageBitmap(image);
                runTextRecognition();
            } else {
                Toast.makeText(this, "No image was selected", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void runTextRecognition() {
        Bitmap bitmap = ((BitmapDrawable) image_view.getDrawable()).getBitmap();
        FirebaseVisionImage image = FirebaseVisionImage.fromBitmap(bitmap);

        FirebaseVisionTextRecognizer detector = FirebaseVision.getInstance()
                .getOnDeviceTextRecognizer();
        editText.setEnabled(false);
        detector.processImage(image)
                .addOnSuccessListener(
                        new OnSuccessListener<FirebaseVisionText>() {
                            @Override
                            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                                editText.setEnabled(true);
                                processTextRecognitionResult(firebaseVisionText);
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                editText.setEnabled(true);
                                e.printStackTrace();
                            }
                        });
    }

    private void processTextRecognitionResult(FirebaseVisionText firebaseVisionText) {
        List<FirebaseVisionText.TextBlock> blocks = firebaseVisionText.getTextBlocks();
        if (blocks.size() == 0) {
            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show();
        } else {
            for (FirebaseVisionText.TextBlock block : firebaseVisionText.getTextBlocks()) {
                String text = block.getText();
                editText.setText(text);
            }
        }
    }
    
    private void AddData(String newEntry) {

        boolean insertData = mDatabasehelper.addDatA(newEntry);

        if (insertData) {
            toastMessage("Data saved");
        } else {
            toastMessage("Error: data not saved");
        }
    }
    private void toastMessage(String message) {
        Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show();
    }
}


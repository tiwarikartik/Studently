package com.kartik.homework.student;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.ParcelFileDescriptor;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.kartik.homework.MainActivity;
import com.kartik.homework.R;
import com.kartik.homework.utils.Actions;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

public class Submission extends AppCompatActivity {

    private static final int PICK_PDF_REQUEST = 1;
    private ScaleGestureDetector pinchZoom;
    private StorageReference storageReference;
    private ConstraintLayout mPdfView;
    private Button mButtonNext, mButtonPrevious;
    private TextView mProgressPercentage, mPageCount;
    private ImageView mImageView;
    private TextInputLayout mEditTextFileName;
    private ProgressBar mProgressBar;
    private Uri pdfPath;
    private PdfRenderer renderer;
    int totalPages = 0;
    int displayPage = 0;
    String fileName, id, uId, className;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submission);

        SharedPreferences sp = getSharedPreferences(MainActivity.preferenceName, Context.MODE_PRIVATE);
        className = sp.getString(MainActivity.classDiv, null);
        uId = sp.getString(MainActivity.id, null);
        id = getIntent().getStringExtra("ID");

        mButtonNext = (Button) findViewById(R.id.pageNext);
        mButtonPrevious = (Button) findViewById(R.id.pagePrevious);

        mEditTextFileName = (TextInputLayout) findViewById(R.id.fileName);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mImageView = (ImageView) findViewById(R.id.pdfPage);
        mPdfView = (ConstraintLayout) findViewById(R.id.pdfView);

        mProgressPercentage = (TextView) findViewById(R.id.progressPercentage);
        mPageCount = (TextView) findViewById(R.id.pageCount);

        storageReference = FirebaseStorage.getInstance().getReference(className);
        pinchZoom = new ScaleGestureDetector(Submission.this, new Zoom(mImageView));

        mEditTextFileName.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (totalPages == 0) {
                    openFileChooser();
                    mEditTextFileName.setEndIconDrawable(R.drawable.ic_baseline_file_upload_24);
                } else {
                    String name = mEditTextFileName.getEditText().getText().toString().trim();
                    if (name.isEmpty()) {
                        mEditTextFileName.setError("File name cannot be empty");
                    } else {
                        mEditTextFileName.setErrorEnabled(false);
                        uploadFile();
                    }
                }
            }
        });

        mButtonPrevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (displayPage > 0) {
                    displayPage--;
                    _display(displayPage);
                }
            }
        });

        mButtonNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (displayPage < (totalPages - 1)) {
                    displayPage++;
                    _display(displayPage);
                }
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        pinchZoom.onTouchEvent(event);
        return true;
    }

    private class Zoom extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        ImageView imageView;
        float factor;

        public Zoom(ImageView image) {
            super();
            factor = 1.0f;
            imageView = image;
        }

        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            float scaleFactor = detector.getScaleFactor() - 1;
            factor += scaleFactor;
            if (!(factor >= 2.5f || factor <= 1.0f)) {
                imageView.setScaleX(factor);
                imageView.setScaleY(factor);
            } else {
                factor -= scaleFactor;
            }
            return true;
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openFileChooser() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, PICK_PDF_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PDF_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mPdfView.setVisibility(View.VISIBLE);
            mImageView.setVisibility(View.VISIBLE);

            pdfPath = data.getData();
            try {
                ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(pdfPath, "r");
                renderer = new PdfRenderer(parcelFileDescriptor);
                totalPages = renderer.getPageCount();
                displayPage = 0;
                _display(displayPage);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void _display(int displayPage) {
        if (renderer != null) {
            PdfRenderer.Page page = renderer.openPage(displayPage);
            Bitmap mBitmap = Bitmap.createBitmap(page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(mBitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            mImageView.setImageBitmap(mBitmap);
            page.close();
            mPageCount.setText((displayPage + 1) + "/" + totalPages);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (renderer != null) {
            renderer.close();
        }
    }

    private void uploadFile() {
        mProgressBar.setVisibility(View.VISIBLE);
        fileName = mEditTextFileName.getEditText().getText().toString().trim() + "." + getFileExtension(pdfPath);
        StorageReference fileReference = storageReference.child(id).child(uId).child(fileName);
        fileReference.putFile(pdfPath)

                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                        int progress = (int) ((100.0 * snapshot.getBytesTransferred()) / snapshot.getTotalByteCount());
                        mProgressPercentage.setText(progress + "%");
                        mProgressBar.setProgress(progress);
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mProgressBar.setVisibility(View.GONE);
                                mProgressPercentage.setVisibility(View.GONE);
                                Toast.makeText(Submission.this, "Upload Successful", Toast.LENGTH_SHORT).show();
                            }
                        }, 500);

                        fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            private DocumentReference documentReference;

                            @Override
                            public void onSuccess(Uri uri) {
                                HashMap<String, Object> details = new HashMap<>();
                                details.put("link", uri.toString());
                                details.put("uploaded", true);
                                details.put("notified", true);

                                FirebaseFirestore.getInstance()
                                        .collection("Homework")
                                        .document(id)
                                        .collection("Actions")
                                        .document(uId).update(details);

                                pdfPath = null;

                                startActivity(new Intent(Submission.this, StudentActivity.class));
                                finish();
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
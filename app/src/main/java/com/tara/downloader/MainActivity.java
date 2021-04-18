package com.tara.downloader;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.util.SparseArray;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.ashudevs.instagramextractor.InstagramExtractor;
import com.ashudevs.instagramextractor.InstagramFile;

import java.io.IOException;

import at.huber.youtubeExtractor.VideoMeta;
import at.huber.youtubeExtractor.YouTubeExtractor;
import at.huber.youtubeExtractor.YtFile;

public class MainActivity extends AppCompatActivity {
    private EditText et_search;
    private Button button_download, btn_popup;
    private String newLink, selectedVideoPath, filemanagerstring;
    private static final int STORAGE_REQUEST_CODE = 1;
    private String[] permission = {Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission
                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET};
    ProgressDialog dialog;
    public static final int REQUEST_TAKE_GALLERY_VIDEO = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUi();
        getClickonPopUp();
    }

    private void youtubeMethod() {
        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
                    String Url = et_search.getText().toString().trim();
                    if (Url.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Url is required", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog = new ProgressDialog(MainActivity.this);
                        dialog.setTitle("Wait for a moment");
                        dialog.setMessage("Please wait...");
                        dialog.setCancelable(false);
                        dialog.show();
                        new YouTubeExtractor(MainActivity.this) {
                            @SuppressLint("StaticFieldLeak")
                            @Override
                            protected void onExtractionComplete(SparseArray<YtFile> ytFiles, VideoMeta videoMeta) {
                                try {
                                    if (ytFiles != null) {
                                        YtFile ytFile = ytFiles.get(22);
                                        downloadFromUrl(ytFile.getUrl(), videoMeta.getTitle());
                                    }
                                } catch (NullPointerException e) {
                                    e.printStackTrace();
                                    Toast.makeText(MainActivity.this, "please try again", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    et_search.setVisibility(View.GONE);
                                    button_download.setVisibility(View.GONE);
                                    btn_popup.setVisibility(View.VISIBLE);
                                }
                            }
                        }.extract(Url, true, false);
                    }

                } else {
                    requestStoragePermission();
                }

            }
        });
    }

    private void downloadFromUrl(String url, String title) {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp4");
        DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
        downloadManager.enqueue(request);
        et_search.setText("");
        dialog.dismiss();
        et_search.setVisibility(View.GONE);
        button_download.setVisibility(View.GONE);
        btn_popup.setVisibility(View.VISIBLE);
        Toast.makeText(MainActivity.this, "Your video is being download", Toast.LENGTH_LONG).show();
    }

    private void getClickonPopUp() {
        btn_popup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(MainActivity.this, btn_popup);
                popupMenu.getMenuInflater().inflate(R.menu.popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.youtube:
                                et_search.setVisibility(View.VISIBLE);
                                button_download.setVisibility(View.VISIBLE);
                                btn_popup.setVisibility(View.GONE);
                                youtubeMethod();
                                break;
                            case R.id.instagram:
                                et_search.setVisibility(View.VISIBLE);
                                button_download.setVisibility(View.VISIBLE);
                                btn_popup.setVisibility(View.GONE);
                                instagramMethod();
                                break;
                            case R.id.other:
                                et_search.setVisibility(View.VISIBLE);
                                button_download.setVisibility(View.VISIBLE);
                                btn_popup.setVisibility(View.GONE);
                                OtherMethod();
                                break;
                            case R.id.tomp3:
                                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                                startActivityForResult(i, REQUEST_TAKE_GALLERY_VIDEO);
                               /* Intent intent = new Intent();
                                intent.setType("video/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);*/
                                break;

                        }
                        return true;
                    }
                });
                popupMenu.show();
            }
        });
    }

    private void OtherMethod() {
        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED) {
                    String Url = et_search.getText().toString().trim();
                    if (Url.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Url is required", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog = new ProgressDialog(MainActivity.this);
                        dialog.setTitle("Wait for a moment");
                        dialog.setMessage("Please wait...");
                        dialog.setCancelable(false);
                        dialog.show();
                        downloadFromUrl(Url, "Url");

                    }
                } else {
                    requestStoragePermission();
                }

            }
        });
    }

    private void requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle("Permission needed")
                    .setMessage("This permission is needed because of this and that")
                    .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission
                                    .WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, STORAGE_REQUEST_CODE);
                        }
                    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        } else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission
                            .WRITE_EXTERNAL_STORAGE, Manifest.permission.INTERNET}, STORAGE_REQUEST_CODE);
        }
    }

    private void instagramMethod() {

        button_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    String Url = et_search.getText().toString().trim();
                    if (Url.isEmpty()) {
                        Toast.makeText(MainActivity.this, "Url is required", Toast.LENGTH_SHORT).show();
                    } else {
                        dialog = new ProgressDialog(MainActivity.this);
                        dialog.setTitle("Wait for a moment");
                        dialog.setMessage("Please wait...");
                        dialog.setCancelable(false);
                        dialog.show();
                        new InstagramExtractor() {
                            @Override
                            protected void onExtractionComplete(InstagramFile vimeoFile) {
                                if (vimeoFile != null) {
                                    String title = "Video is downloading !!";
                                    String link = vimeoFile.getUrl();
                                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(link));
                                    request.setTitle(title);
                                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, title + ".mp4");
                                    DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                    request.allowScanningByMediaScanner();
                                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                    request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
                                    downloadManager.enqueue(request);
                                    et_search.setText("");
                                    dialog.dismiss();
                                    et_search.setVisibility(View.GONE);
                                    button_download.setVisibility(View.GONE);
                                    btn_popup.setVisibility(View.VISIBLE);
                                    Toast.makeText(MainActivity.this, "Your video is being download", Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            protected void onExtractionFail(String Error) {
                                Toast.makeText(MainActivity.this, "please try again", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        }.Extractor(MainActivity.this, Url);

                    }
                } else {
                    requestStoragePermission();
                }

            }
        });
    }

    private void initUi() {
        et_search = (EditText) findViewById(R.id.et_search);
        button_download = (Button) findViewById(R.id.button_download);
        btn_popup = (Button) findViewById(R.id.btn_popup);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_TAKE_GALLERY_VIDEO) {
                selectedVideoPath = getPath(data.getData());
                if(selectedVideoPath == null) {
                    Toast.makeText(this, "empty"+requestCode, Toast.LENGTH_SHORT).show();
                } else {
                    Log.d("Tarang", "onActivityResult: "+selectedVideoPath);
                }
            }
        }
       // finish();
    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if(cursor!=null) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            Log.d("Tarang", "getPath: "+cursor.getString(column_index));
            return cursor.getString(column_index);
        }
        else return null;
    }
}
package com.dima.emmeggi95.jaycaves.me.entities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.util.LruCache;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Implementation of a cache for album covers. It saves bitmaps into a @LruCache, avoids re-downloading of cached images, speeds up the look-up.
 * @see LruCache
 */
public class CoverCache {


    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference storageAlbumReference = storage.getReference("Album_covers");
    private static final StorageReference storageArtistReference= storage.getReference("Artist_covers");
    private static final StorageReference storageUserReference= storage.getReference("User_covers");
    public static final String INTERNAL_DIRECTORY_ALBUM = "Album_covers";
    public static final String INTERNAL_DIRECTORY_ARTIST= "Artist_covers";
    public static final String INTERNAL_DIRECTORY_USERS= "User_covers";

    private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(1024 * 1024 * 10) {
        @Override
        public int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }
    };

    /**
     * Given the @id, retrieves the corresponding cover, loads it as content of @view. Makes the @progress bar invisible when content is ready. @directory is the internal storage reference for the lookup of persistent app data. Search starts within local Lru.
     * In case of miss, the image is either retrieved from persistent storage or downloaded from the FirebaseStorage then saved in cache.
     * @param id
     * @param view
     * @param progressBar
     * @param directory
     */
    public static void retrieveCover(final String id, final ImageView view, final ProgressBar progressBar, final File directory) {

        Bitmap result = mCache.get(id);

        // Cache HIT
        if (result!=null) {
            view.setImageBitmap(result);
            progressBar.setVisibility(View.GONE);
            view.setVisibility(View.VISIBLE);
            //System.err.println("CACHE HIT");
        }
        else {

            // Cache MISS
            if (new File(directory, id).exists()) {
                // The cover is already saved in the internal storage
                File file = new File(directory , id);
                Bitmap bitmap = BitmapFactory.decodeFile(file.getPath());
                mCache.put(id, bitmap);
                view.setImageBitmap(bitmap);
                progressBar.setVisibility(View.GONE);
                view.setVisibility(View.VISIBLE);
                //System.err.println("FOUND IN INTERNAL STORAGE");
            } else {

                final File file= new File(directory, id);

                if (directory.getName().contains("Album")){

                    storageAlbumReference.child(id).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                            Bitmap bitmap= BitmapFactory.decodeFile(file.getAbsolutePath());
                            mCache.put(id, bitmap);
                            view.setImageBitmap(bitmap);
                            progressBar.setVisibility(View.GONE);
                            view.setVisibility(View.VISIBLE);

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            System.out.println(e.getMessage());

                        }
                    });
                }
                else{
                    if (directory.getName().contains("Artist"))
                        storageArtistReference.child(id).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mCache.put(id, BitmapFactory.decodeFile(file.getAbsolutePath()));
                                view.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                progressBar.setVisibility(View.GONE);
                                view.setVisibility(View.VISIBLE);

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getMessage());

                            }
                        });
                    else
                        storageUserReference.child(id).getFile(file).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                mCache.put(id, BitmapFactory.decodeFile(file.getAbsolutePath()));
                                view.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
                                progressBar.setVisibility(View.GONE);
                                view.setVisibility(View.VISIBLE);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                System.out.println(e.getMessage());

                            }
                        });
                }
            }

        }


    }

}

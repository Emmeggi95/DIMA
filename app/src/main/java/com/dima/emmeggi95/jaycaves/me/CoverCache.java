package com.dima.emmeggi95.jaycaves.me;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.util.LruCache;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

/**
 * Implementation of a cache for Album Covers
 */
public class CoverCache {


    private static final FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final StorageReference storageAlbumReference = storage.getReference("Album_covers");
    private static final StorageReference storageArtistReference= storage.getReference("Artist_covers");
    private static File tempFile;
    public static final String INTERNAL_DIRECTORY_ALBUM = "Album_covers";
    public static final String INTERNAL_DIRECTORY_ARTIST= "Artist_covers";

    private static LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(1024 * 1024 * 10) {
        @Override
        public int sizeOf(String key, Bitmap value) {
            return value.getRowBytes() * value.getHeight();
        }
    };


    public static void retrieveCover(final String id, final ImageView view, final File directory) {

        Bitmap result = mCache.get(id);

        // Cache HIT
        if (result!=null) {
            view.setImageBitmap(result);
            //System.err.println("CACHE HIT");
        }

        // Cache MISS
        if (new File(directory + id).exists()) {
            // The cover is already saved in the internal storage
            result = BitmapFactory.decodeFile(directory+id);
            mCache.put(id, result);
            view.setImageBitmap(result);
           // System.err.println("FOUND IN INTERNAL STORAGE");
        }
        else {
            // The cover is downloaded
            tempFile = new File(directory + id);
           // System.err.println("PATH: "+tempFile.getAbsolutePath());
            if (directory.getName().contains("Album")){
                storageAlbumReference.child(id).getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        mCache.put(id,BitmapFactory.decodeFile(tempFile.getPath()));
                        view.setImageBitmap(BitmapFactory.decodeFile(tempFile.getPath()));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        System.out.println(e.getMessage());

                    }
                });
            }
            else
                storageArtistReference.child(id).getFile(tempFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        mCache.put(id,BitmapFactory.decodeFile(tempFile.getPath()));
                        view.setImageBitmap(BitmapFactory.decodeFile(tempFile.getPath()));

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

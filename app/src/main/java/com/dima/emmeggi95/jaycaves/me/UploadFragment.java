package com.dima.emmeggi95.jaycaves.me;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.dima.emmeggi95.jaycaves.me.activities.AddContentActivity;


public class UploadFragment extends Fragment {

    private TextView message;
    private ProgressBar progressBar;
    private ImageView imageView;
    private TextView submit;

    private Handler handler;

    public UploadFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upload, container, false);

        message = view.findViewById(R.id.message);
        progressBar = view.findViewById(R.id.progress_bar);
        imageView = view.findViewById(R.id.image_view);
        submit = view.findViewById(R.id.submit);

        message.setText(getString(R.string.uploading_message));
        message.setTextColor(getResources().getColor(R.color.colorTextOnLightBackground));
        imageView.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        submit.setVisibility(View.GONE);

        handler = new Handler();

        return view;
    }

    public void success(String messageText){
        message.setText(messageText);
        message.setTextColor(getResources().getColor(R.color.success_color));
        progressBar.setVisibility(View.GONE);
        imageView.setAlpha(0f);
        imageView.setColorFilter(getResources().getColor(R.color.success_color));
        imageView.setVisibility(View.VISIBLE);
        imageView.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                submit.setText(getString(R.string.submit_success));
                submit.setAlpha(0f);
                submit.setVisibility(View.VISIBLE);
                submit.animate().alpha(1f).setDuration(250);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getActivity().finish();
                    }
                });
            }
        });
    }

    public void failure(String messageText){
        message.setText(messageText);
        message.setTextColor(getResources().getColor(R.color.failure_color));
        progressBar.setVisibility(View.GONE);
        imageView.setImageResource(R.drawable.ic_sentiment_dissatisfied_black_24dp);
        imageView.setAlpha(0f);
        imageView.setColorFilter(getResources().getColor(R.color.failure_color));
        imageView.setVisibility(View.VISIBLE);
        imageView.animate().alpha(1f).setDuration(1000).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                submit.setText(getString(R.string.submit_failure));
                submit.setAlpha(0f);
                submit.setVisibility(View.VISIBLE);
                submit.animate().alpha(1f).setDuration(250);
                submit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ((AddContentActivity) getActivity()).goBackToFragment();
                    }
                });
            }
        });
    }

}

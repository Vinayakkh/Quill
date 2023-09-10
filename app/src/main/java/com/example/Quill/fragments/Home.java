package com.example.Quill.fragments;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.Quill.R;
import com.example.Quill.adapter.HomeAdapter;
import com.example.Quill.adapter.StoriesAdapter;
import com.example.Quill.chat.ChatUsersActivity;
import com.example.Quill.model.HomeModel;
import com.example.Quill.model.StoriesModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


public class Home extends Fragment {

    private final MutableLiveData<Integer> commentCount = new MutableLiveData<>();
    HomeAdapter adapter;
    RecyclerView storiesRecyclerView;
    StoriesAdapter storiesAdapter;
    List<StoriesModel> storiesModelList;
    DocumentReference reference;
    private RecyclerView recyclerView;
    private List<HomeModel> list;
    private FirebaseUser user;

    public Home() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);


        list = new ArrayList<>();
        adapter = new HomeAdapter(list, getActivity());
        recyclerView.setAdapter(adapter);

        loadDataFromFirestore();

        adapter.OnPressed(new HomeAdapter.OnPressed() {
            @Override
            public void onLiked(int position, String id, String uid, List<String> likeList, boolean isChecked) {

                DocumentReference reference = FirebaseFirestore.getInstance().collection("Users")
                        .document(uid)
                        .collection("Post Images")
                        .document(id);

                if (likeList.contains(user.getUid())) {
                    likeList.remove(user.getUid()); // unlike
                } else {
                    likeList.add(user.getUid()); // like
                }

                
                    Map<String, Object> map = new HashMap<>();
                    map.put("likes", likeList);
                    reference.update(map);

            }


            @Override
            public void setCommentCount(final TextView textView) {

                Activity activity = getActivity();

                commentCount.observe((LifecycleOwner) activity, integer -> {


                    if (commentCount.getValue() == 0) {
                        textView.setVisibility(View.GONE);
                    } else
                        textView.setVisibility(View.VISIBLE);


                    textView.setText("See all " + commentCount.getValue() + "comments");
                });


            }
        });

     view.findViewById(R.id.sendBtn).setOnClickListener(v -> {

        Intent intent = new Intent(getActivity(), ChatUsersActivity.class);
        startActivity(intent);

    });

}


    private void init(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        if (getActivity() != null)
            ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        storiesRecyclerView = view.findViewById(R.id.storiesRecyclerView);
        storiesRecyclerView.setHasFixedSize(true);
        storiesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        storiesModelList = new ArrayList<>();
        storiesModelList.add(new StoriesModel("", "", "", "",""));
        storiesAdapter = new StoriesAdapter(storiesModelList, getActivity());
        storiesRecyclerView.setAdapter(storiesAdapter);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
    }

    private void loadDataFromFirestore() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Get the list of UIDs the current user is following
        DocumentReference userReference = db.collection("Users").document(user.getUid());
        userReference.get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> followingUids = (List<String>) documentSnapshot.get("following");
                        if (followingUids != null && !followingUids.isEmpty()) {
                            // Query posts from all followers
                            db.collection("Users")
                                    .whereIn("uid", followingUids)
                                    .get()
                                    .addOnSuccessListener(queryDocumentSnapshots -> {
                                        list.clear(); // Clear the existing list
                                        for (QueryDocumentSnapshot followerSnapshot : queryDocumentSnapshots) {
                                            String followerUid = followerSnapshot.getString("uid");
                                            // Fetch and add posts for each follower
                                            fetchPostsForFollower(followerUid);
                                        }

                                        //todo: fetch stories
                                        loadStories(followingUids);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Error: ", "Failed to fetch posts from followers", e);
                                    });
                        } else {
                            // Handle case when the user is not following anyone
                        }
                    } else {
                        // Handle case when the user document doesn't exist
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Error: ", "Failed to fetch user data", e);
                });
    }

    private void fetchPostsForFollower(String followerUid) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("Users")
                .document(followerUid)
                .collection("Post Images")
                .addSnapshotListener((postSnapshot, postError) -> {
                    if (postError != null) {
                        Log.e("Error: ", "Failed to fetch posts for user " + followerUid, postError);
                        return;
                    }

                    if (postSnapshot == null) {
                        Log.e("Error: ", "Post snapshot is null for user " + followerUid);
                        return;
                    }

                    for (QueryDocumentSnapshot snapshot1 : postSnapshot) {
                        if (!snapshot1.exists()) {
                            continue;
                        }

                        HomeModel model = snapshot1.toObject(HomeModel.class);

                        // Check if the post with the same ID already exists in the list
                        boolean postExists = false;
                        for (HomeModel existingModel : list) {
                            if (existingModel.getId().equals(model.getId())) {
                                postExists = true;
                                break;
                            }
                        }

                        // Add the post to the list only if it doesn't already exist
                        if (!postExists) {
                            list.add(model);
                        }
                    }

                    adapter.notifyDataSetChanged();
                });
    }



    void loadStories(List<String> followingList) {

        Query query = FirebaseFirestore.getInstance().collection("Stories");
        query.whereIn("uid", followingList)
                .addSnapshotListener((value, error) -> {


                    if (error != null) {
                        Log.d("Error: ", error.getMessage());
                    }

                    if (value == null)
                        return;


                    for (QueryDocumentSnapshot snapshot : value) {

                        if (!value.isEmpty()) {
                            StoriesModel model = snapshot.toObject(StoriesModel.class);
                            storiesModelList.add(model);
                        }

                    }
                    storiesAdapter.notifyDataSetChanged();

                });

    }
}


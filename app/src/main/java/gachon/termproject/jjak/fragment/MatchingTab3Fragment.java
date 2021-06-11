package gachon.termproject.jjak.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import gachon.termproject.jjak.Content.MatchingPostContent;
import gachon.termproject.jjak.R;
import gachon.termproject.jjak.UserInfo;
import gachon.termproject.jjak.adapter.MatchingPostAdapter;

public class MatchingTab3Fragment extends Fragment {
    private View view;
    private SwipeRefreshLayout refresher;
    private RecyclerView contents;
    private MatchingPostContent postContent;
    private MatchingPostAdapter matchingpostAdapter;
    private ArrayList<MatchingPostContent> postContentList;
    public static DatabaseReference databaseReference;
    public static ValueEventListener postEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_matching, container, false);

        contents = view.findViewById(R.id.matchingContent);
        refresher = view.findViewById(R.id.refresh_layout);

        databaseReference = FirebaseDatabase.getInstance().getReference("MeetingPosts");

        postContentList = new ArrayList<>();
        matchingpostAdapter = new MatchingPostAdapter(getActivity(), postContentList);

        contents.setLayoutManager(new LinearLayoutManager(getActivity()));
        contents.setHasFixedSize(true);
        contents.setAdapter(matchingpostAdapter);

        postEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postContentList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    postContent = snapshot.getValue(MatchingPostContent.class);
                    postContent.setUserId(snapshot.child("userId").getValue().toString());

                    if (postContent.getIsMatched()) { //글 상태가 매칭 완료인데
                        if (snapshot.child("requests/" + UserInfo.getUserId()).exists()) //매칭 대기 목록에 앗 내가 잇네?
                            if ((boolean) snapshot.child("requests/" + UserInfo.getUserId() + "/getIsMatched").getValue()) //그럼 내가 match = true인지 확인하기
                                postContentList.add(0, postContent);
                    } else if (postContent.getIsRecruited() && snapshot.child("requests/" + UserInfo.getUserId()).exists())
                        postContentList.add(0, postContent);
                }
                matchingpostAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        databaseReference.addListenerForSingleValueEvent(postEventListener);

        refresher.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                databaseReference.addListenerForSingleValueEvent(postEventListener);
                refresher.setRefreshing(false);
            }
        });

        return view;
    }
}

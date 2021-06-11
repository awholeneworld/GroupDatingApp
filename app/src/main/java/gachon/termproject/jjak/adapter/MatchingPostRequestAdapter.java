package gachon.termproject.jjak.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import android.app.AlertDialog;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;

import gachon.termproject.jjak.Content.NotificationContent;
import gachon.termproject.jjak.Content.RequestContent;
import gachon.termproject.jjak.R;
import gachon.termproject.jjak.UserInfo;
import gachon.termproject.jjak.activity.ChatActivity;
import gachon.termproject.jjak.activity.MatchingWriterSeePostActivity;
import gachon.termproject.jjak.activity.SeeProfileActivity;
import gachon.termproject.jjak.fragment.MatchingTabProgressFragment;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

public class MatchingPostRequestAdapter extends RecyclerView.Adapter<MatchingPostRequestAdapter.ViewHolder> {
    private Context context;
    private ArrayList<RequestContent> requestsList;
    private String postId;
    private Activity activity;

    public MatchingPostRequestAdapter(Activity activity, Context context, ArrayList<RequestContent> requestsList, String postId)
    {
        this.context = context;
        this.requestsList = requestsList;
        this.postId = postId;
        this.activity = activity;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        ImageView profileImgView;
        TextView nicknameView;
        Button viewProfile;
        Button chatBtn;
        Button matching_btn;
        String nickname;
        String profileImg;
        String userId;
        String pushToken;
        String intro;
        boolean isMatched;
        ArrayList<String> location;
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("MeetingPosts/" + postId);

        ViewHolder(View itemView) {
            super(itemView);
            profileImgView = itemView.findViewById(R.id.requestImg);
            nicknameView = itemView.findViewById(R.id.requestNickname);
            viewProfile = itemView.findViewById(R.id.matching_request_item_profile);
            chatBtn = itemView.findViewById(R.id.matching_request_item_chat);
            matching_btn = itemView.findViewById(R.id.matching_request_item_accept_cancel);

            viewProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SeeProfileActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("profileImg", profileImg);
                    intent.putExtra("pushToken", pushToken);
                    intent.putExtra("intro", intro);
                    intent.putStringArrayListExtra("location", location);
                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            });


            chatBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, ChatActivity.class);
                    intent.putExtra("userId", userId);
                    intent.putExtra("nickname", nickname);
                    intent.putExtra("profileImg", profileImg);
                    intent.putExtra("pushToken", pushToken);
                    intent.putExtra("intro", intro);
                    intent.putStringArrayListExtra("location", location);
                    context.startActivity(intent.addFlags(FLAG_ACTIVITY_NEW_TASK));
                }
            });


            matching_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dlg = new AlertDialog.Builder(activity);
                    dlg.setTitle(nickname + "님의 매칭을 수락하시겠습니까?"); //제목
                    dlg.setMessage("최대 4명까지 수락할 수 있습니다."); // 메시지

                    dlg.setPositiveButton("수락", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            if (MatchingWriterSeePostActivity.recruited == 4) {
                                Toast.makeText(context, "더 이상 수락할 수 없습니다", Toast.LENGTH_SHORT);
                                return;
                            }
                            dbRef.child("requests").orderByKey().equalTo(userId).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {
                                    snapshot.getRef().child("isMatched").setValue(true).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            sendFCM(pushToken);
                                            MatchingWriterSeePostActivity.recruited++;
                                            if (MatchingWriterSeePostActivity.recruited == 4) dbRef.child("isRecruited").setValue(true);
                                            MatchingWriterSeePostActivity.acceptedView();
                                            MatchingWriterSeePostActivity.requestsView();
                                            if (MatchingPagerAdapter.tab3 != null)
                                                MatchingTabProgressFragment.databaseReference.addValueEventListener(MatchingTabProgressFragment.postEventListener);
                                            activity.finish();
                                        }
                                    });
                                }
                                @Override
                                public void onChildChanged(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {}

                                @Override
                                public void onChildRemoved(@NonNull @NotNull DataSnapshot snapshot) {}

                                @Override
                                public void onChildMoved(@NonNull @NotNull DataSnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable String previousChildName) {}

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                            });
                        }
                    });

                    dlg.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });

                    dlg.show();
                }
            });
        }
    }

    @NonNull
    @Override
    public MatchingPostRequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_request_see_post_matching, parent,false);

        return new MatchingPostRequestAdapter.ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull MatchingPostRequestAdapter.ViewHolder holder, int position) {
        RequestContent request = requestsList.get(position);

        String profileImg = request.getProfileImg();
        String nickname = request.getNickname();

        holder.userId = request.getUserId();
        holder.nickname = nickname;
        holder.profileImg = request.getProfileImg();
        holder.pushToken = request.getPushToken();
        holder.location = request.getLocation();
        holder.intro = request.getIntro();
        holder.isMatched = request.getIsMatched();

        // 신청인 프로필 사진 표시
        holder.profileImgView.setBackground(new ShapeDrawable(new OvalShape()));
        holder.profileImgView.setClipToOutline(true);
        if (!profileImg.equals("None"))
            Glide.with(context).load(profileImg).into(holder.profileImgView);

        // 신청인 표시
        holder.nicknameView.setText(request.getNickname());

        //매칭된 상태라면 신청버튼 없애기
        if(holder.isMatched){
            holder.matching_btn.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void sendFCM(String pushToken) {
        Gson gson = new Gson();

        NotificationContent notificationContent = new NotificationContent();
        notificationContent.to = pushToken;
        notificationContent.notification.title = "매칭 알림";
        notificationContent.notification.body = UserInfo.getNickname() + "님과의 매칭에 성공하였습니다.";
        notificationContent.data.title = "매칭 알림";
        notificationContent.data.body = UserInfo.getNickname() + "님과의 매칭에 성공하였습니다.";


        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), gson.toJson(notificationContent));
        Request request = new Request.Builder().header("Content-Type", "application/json")
                .addHeader("Authorization", "key=AAAAvRnN9Ug:APA91bHY1OaJb5vdTudwaNQNfMWDglwpa4_NLIJW6ySedNOZvhD3k_NmLpwIodJ5zq2XHzAFZj87-D_hth1gcYMHZ73cL403o2bdUimGX0XmAn0oRzlSm7j3Tt3yEireZKNveR3MM-Jw")
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody).build();

        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {

            }

            @Override
            public void onResponse(Response response) throws IOException {

            }
        });
    }
}

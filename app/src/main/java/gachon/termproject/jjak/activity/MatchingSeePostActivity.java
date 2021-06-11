package gachon.termproject.jjak.activity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
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
import gachon.termproject.jjak.fragment.MatchingTab2Fragment;
import gachon.termproject.jjak.fragment.MatchingTab1Fragment;

public class MatchingSeePostActivity extends AppCompatActivity {
    private LinearLayout container;
    private Button matching_btn;
    private RequestContent request;
    private String postId;
    private String userId;
    private String nickname;
    private String profileImg;
    private String intro;
    private String pushToken;
    private ArrayList<String> location;
    private DatabaseReference databaseReference;
    private int state = 0; //0 - 매칭요청 / 1 - 취소 / 2 - 완료 (가능한 액션)
    Intent intent;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.matching_see_post);

        //toolbar를 activity bar로 지정!
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true); //자동 뒤로가기?
        actionBar.setDisplayShowTitleEnabled(false); //기본 제목 삭제

        // 인텐트 데이터 가져오기
        intent = getIntent();
        postId = intent.getStringExtra("postId");
        userId = intent.getStringExtra("userId");
        nickname = intent.getStringExtra("nickname");
        profileImg = intent.getStringExtra("profileImg");
        intro = intent.getStringExtra("intro");
        pushToken = intent.getStringExtra("pushToken");
        location = intent.getStringArrayListExtra("location");
        ArrayList<String> content = intent.getStringArrayListExtra("content");
        ArrayList<String> images = intent.getStringArrayListExtra("images");

        // 레이아웃 가져오기
        ImageView profileView = findViewById(R.id.postProfile);
        TextView titleView = findViewById(R.id.title);
        TextView nicknameView = findViewById(R.id.postNickname);
        TextView time = findViewById(R.id.postTime);
        TextView loca = findViewById(R.id.see_post_location_name);
        container = findViewById(R.id.see_post_content);
        matching_btn = findViewById(R.id.matching_expert_button);

        // 이미 매칭 요청 했는지 확인
        databaseReference = FirebaseDatabase.getInstance().getReference("MeetingPosts/" + postId + "/requests");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                int i = 0;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    i++;
                    RequestContent content = snapshot.getValue(RequestContent.class);
                    if (snapshot.getKey().equals(UserInfo.getUserId())) {
                        matching_btn.setText("취소");
                        state = 1; //0 - 매칭요청 / 1 - 취소 / 2 - 완료 (가능한 액션)
                        if (content.getIsMatched()) {
                            matching_btn.setText("신청완료");
                            state = 2;
                        }
                        break;
                    }
                }
                if (i == dataSnapshot.getChildrenCount() && i != 1) {
                    matching_btn.setText("참가신청");
                    state = 0;
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {
            }
        });

        // 제목, 닉네임, 작성시간 세팅
        titleView.setText(intent.getStringExtra("title"));
        nicknameView.setText(nickname);
        time.setText(intent.getStringExtra("time"));
        loca.setText(intent.getStringExtra("locationInPost"));

        // 프로필 사진 세팅 (oimage 동그랗게)
        profileView.setBackground(new ShapeDrawable(new OvalShape()));
        profileView.setClipToOutline(true);
        if (!profileImg.equals("None"))
            Glide.with(this).load(profileImg).into(profileView);

        //TextView 생성 후 layout_width, layout_height, gravity, 내용 등 설정
        TextView text_content = new TextView(MatchingSeePostActivity.this);
        text_content.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        text_content.setText(content.get(0));
        text_content.setTextSize(dpToPx(7));
        text_content.setTextColor(Color.BLACK);

        // 글 넣기
        container.addView(text_content);

        // 이미지 있으면 넣기
        if (images != null) {
            LinearLayout imageContainer = findViewById(R.id.see_post_imagecontainer);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(dpToPx(150), dpToPx(150));
            layoutParams.setMargins(dpToPx(10),0, dpToPx(10), 0);

            // imageView 채우기
            for (int i = 0; i < images.size(); i++) {
                if(images.get(0).compareTo("") == 0) break;

                ImageView imageView = new ImageView(getApplicationContext());
                imageView.setLayoutParams(layoutParams);
                imageView.setScaleType(ImageView.ScaleType.CENTER);
                Glide.with(getApplicationContext()).load(images.get(i)).into(imageView);
                imageContainer.addView(imageView);
            }
        }

        matching_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (state == 0) {
                    //매칭신청
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MatchingSeePostActivity.this);
                    dlg.setTitle("참가 신청"); //제목
                    dlg.setMessage("참가 신청을 하시겠습니까?"); // 메시지
                    dlg.setPositiveButton("신청", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            if (request == null)
                                request = new RequestContent(UserInfo.getNickname(), UserInfo.getProfileImg(), UserInfo.getPushToken(), UserInfo.getIntroduction(), UserInfo.getLocation(), false);

                            databaseReference.child(UserInfo.getUserId()).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                    sendFCM();
                                }
                            });
                            matching_btn.setText("취소");
                            state = 1;
                        }
                    });
                    dlg.setNegativeButton("취소", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            matching_btn.setText("매칭신청");
                            state = 0;
                        }
                    });
                    dlg.show();
                } else if (state == 1) {
                    //취소
                    AlertDialog.Builder dlg = new AlertDialog.Builder(MatchingSeePostActivity.this);
                    dlg.setTitle("신청 취소"); //제목
                    dlg.setMessage("참가 신청을 취소하시겠습니까?"); // 메시지
                    dlg.setPositiveButton("예", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                            databaseReference.child(UserInfo.getUserId()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                }
                            });
                            matching_btn.setText("참가 신청");
                            state = 0;
                        }
                    });

                    dlg.setNegativeButton("아니요", new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dlg.show();
                }
            }
        });
    }

    //위에 메뉴 관련
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            //남이 쓴 글일때 - 프로필보기 / 신고
            case R.id.show_profile:
                Intent intent2 = new Intent(getApplicationContext(), SeeProfileActivity.class);
                intent2.putExtra("userId", userId);
                intent2.putExtra("nickname", nickname);
                intent2.putExtra("profileImg", profileImg);
                intent2.putExtra("intro", intro);
                intent2.putStringArrayListExtra("location", location);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);
                break;
            case R.id.decelerate:
                Toast.makeText(getApplicationContext(), intent.getStringExtra("nickname") + "(이)가 신고되었습니다.", Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.others_post_menu,menu);

        // To display icon on overflow menu
        if (menu instanceof MenuBuilder){
            MenuBuilder m = (MenuBuilder) menu;
            m.setOptionalIconsVisible(true);
        }

        return true;
    }

    @Override
    public void onBackPressed() {
        if (state == 1)
            MatchingTab1Fragment.databaseReference.addValueEventListener(MatchingTab1Fragment.postEventListener);
        else if (state == 0)
            MatchingTab2Fragment.databaseReference.addValueEventListener(MatchingTab2Fragment.postEventListener);
        finish();
    }

    public void sendFCM() {
        Gson gson = new Gson();

        NotificationContent notificationContent = new NotificationContent();
        notificationContent.to = pushToken;
        notificationContent.notification.title = "매칭 알림";
        notificationContent.notification.body = UserInfo.getNickname() + "님의 매칭 신청";
        notificationContent.data.title = "매칭 알림";
        notificationContent.data.body = UserInfo.getNickname() + "님의 매칭 신청";


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

    public static int dpToPx(int dp){
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }
}
package gachon.termproject.me4u.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import gachon.termproject.me4u.R;
import gachon.termproject.me4u.Content.PeopleListContent;
import gachon.termproject.me4u.activity.SeeProfileActivity;

public class MatchingPeopleListAdapter extends RecyclerView.Adapter<MatchingPeopleListAdapter.ViewHolder>{
    private Context context;
    private ArrayList<PeopleListContent> expertList;

    public MatchingPeopleListAdapter(Context context, ArrayList<PeopleListContent> expertList) {
            this.context = context;
            this.expertList = expertList;
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {
        LinearLayout seePortfolio;
        TextView nickname;
        ImageView profileImg;
        String expertUserId;
        String expertNickname;
        String expertProfileImg;
        String expertPushToken;
        String expertIntro;
        ArrayList<String> expertLocation;

        ViewHolder(View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.expert_nickname);
            profileImg = itemView.findViewById(R.id.expert_image);
            seePortfolio = itemView.findViewById(R.id.seeProfile);

            seePortfolio.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SeeProfileActivity.class);
                    intent.putExtra("userId", expertUserId);
                    intent.putExtra("nickname", expertNickname);
                    intent.putExtra("profileImg", expertProfileImg);
                    intent.putExtra("pushToken", expertPushToken);
                    intent.putExtra("intro", expertIntro);
                    intent.putStringArrayListExtra("location", expertLocation);
                    context.startActivity(intent);
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_people_list_matching, parent,false);

        return new ViewHolder(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PeopleListContent content = expertList.get(position);

        String contentNickname = content.getNickname();
        String contentProfileImg = content.getProfileImg();

        holder.expertUserId = content.getUserId();
        holder.expertNickname = contentNickname;
        holder.expertProfileImg = contentProfileImg;
        holder.expertPushToken = content.getPushToken();
        holder.expertIntro = content.getIntro();
        holder.expertLocation = content.getLocation();

        holder.nickname.setText(contentNickname);
        holder.profileImg.setBackground(new ShapeDrawable(new OvalShape()));
        holder.profileImg.setClipToOutline(true);
        if (!contentProfileImg.equals("None"))
            Glide.with(context).load(contentProfileImg).override(1000).thumbnail(0.1f).into(holder.profileImg);
    }

    @Override
    public int getItemCount() {
        return expertList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}

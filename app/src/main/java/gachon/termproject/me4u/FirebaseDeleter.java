package gachon.termproject.me4u;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import gachon.termproject.me4u.activity.SeePostActivity;
import gachon.termproject.me4u.fragment.CommunityFreeFragment;
import gachon.termproject.me4u.fragment.CommunityReviewFragment;
import gachon.termproject.me4u.fragment.CommunityTipFragment;

public class FirebaseDeleter {
    public static void postDelete(Activity activity, final String bigCategory,  final String category, final String id, final ArrayList<String> imgs) {
        //Bigcategory => Matching, Posts
        //category => free, review, tip, userRequests...

        if (imgs != null) {
            String uri = imgs.get(0);

            String uripath = uri.substring(uri.indexOf("/o/") + 3, uri.indexOf("%2Fimage"));
            uripath = uripath.replace("%2F", "/");

            FirebaseStorage.getInstance().getReference().child(uripath).listAll().addOnSuccessListener(new OnSuccessListener<ListResult>() {
                @Override
                public void onSuccess(ListResult listResult) {
                    for (StorageReference item : listResult.getItems()) {
                        // All the items under listRef.
                        item.delete();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(activity, "사진 삭제 에러.", Toast.LENGTH_SHORT).show();
                }
            });
        }


        FirebaseDatabase.getInstance().getReference(bigCategory + "/").child(category).child(id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                Toast.makeText(activity, "게시글이 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                if (bigCategory.equals("Posts")) {
                    if (category.equals("free"))
                        CommunityFreeFragment.databaseReference.addValueEventListener(CommunityFreeFragment.postsListener);
                    else if (category.equals("review"))
                        CommunityReviewFragment.databaseReference.addValueEventListener(CommunityReviewFragment.postsListener);
                    else if (category.equals("tip"))
                        CommunityTipFragment.databaseReference.addValueEventListener(CommunityTipFragment.postsListener);
                }
            }
        });
    }

    public static void commentDelete(DatabaseReference databaseReference, final String commentid) {
        //Bigcategory => Matching, Posts
        //category => free, review, tip, userRequests...
        databaseReference.child(commentid).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<Void> task) {
                SeePostActivity.databaseReference.addValueEventListener(SeePostActivity.commentsListener);
            }
        });
    }
}

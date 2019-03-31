package verma.sahil.arthub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import verma.sahil.arthub.models.Project;
import verma.sahil.arthub.models.User;

public class MyJavaInterface {
    static final int PICK_IMAGE = 6589;
    private static final String TAG = "JavaInterface";
    private final Activity activity;
    private WebView webView;
    private Context context;

    private Project project;
    User user;


    MyJavaInterface(Activity activity, WebView webView, Context context, Project project, User user) {
        this.activity = activity;
        this.webView = webView;
        this.context = context;
        this.project = project;
        this.user = user;
        project.setLeader_id(user.getId());

    }

    @JavascriptInterface
    public void toastData(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    @JavascriptInterface
    public void loadURL(final String url){

        Toast.makeText(context, "loading:"+url, Toast.LENGTH_SHORT).show();

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                startNextAct(url);
            }
        });

    }

    private void startNextAct(String url){
        Intent intent = new Intent(context,WebViewActivity.class);
        intent.putExtra("url","file:///android_asset/template/"+url);
        activity.startActivity(intent);
    }



    @JavascriptInterface
    public void selectImage(){
        Toast.makeText(activity, "Select Image!", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);

    }


    void setSelectedImagePath(final String path){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:replace('imagePathId', '"+path+"')");
            }
        });

    }


    @JavascriptInterface
    public void submitData(final String projName, final String desc){
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(activity, "projectname:"+projName+"\n"+desc, Toast.LENGTH_SHORT).show();
                project.setTitle(projName);
                project.setDesc(desc);
                pushProject();
            }
        });
    }


    private void pushProject(){
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("Projects");
        db.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long projNo = dataSnapshot.getChildrenCount();


                DatabaseReference projDB = FirebaseDatabase.getInstance().
                        getReference("Projects/"+(projNo+1));
                projDB.setValue(project);

                Toast.makeText(context, "Success Creating Project", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(context, "failed!", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "onCancelled: "+databaseError.getMessage() );
                Log.e(TAG, "onCancelled: "+databaseError.getDetails() );
            }
        });
    }
}

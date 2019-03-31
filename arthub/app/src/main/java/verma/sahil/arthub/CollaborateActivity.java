package verma.sahil.arthub;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Random;

import verma.sahil.arthub.models.Diary;
import verma.sahil.arthub.models.Project;
import verma.sahil.arthub.models.User;

import static verma.sahil.arthub.MyJavaInterface.PICK_IMAGE;

public class CollaborateActivity extends AppCompatActivity {

    private final String TAG = "TAG_WebViewA";


    private String url;

    private WebView webView;
    private ProgressBar progressBar;
    private TextView errorMessage;
    private ImageButton navBack;
    private boolean isSchedule;


    private  MyJavaInterface myJavaInterface;


    private Project project;
    private User user;
    private Diary diary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);
        // Makes Progress bar Visible


        webView = findViewById(R.id.webView);
        errorMessage = findViewById(R.id.errorMessage);
        progressBar = findViewById(R.id.progressBar);
        navBack = findViewById(R.id.navBack);


        project = new Project(String.valueOf(new Random().nextLong()));

        diary = new Diary(String.valueOf(new Random().nextLong()));

        project.setDiary_Id(diary.getId());


        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
        if(account==null){
            finish();
            return;
        }

        user = new User(account.getEmail());
        user.setName(account.getDisplayName());






        url = getIntent().getStringExtra("url");

        isSchedule = getIntent().getBooleanExtra("isSchedule",false);




        progressBar.setIndeterminate(false);
        progressBar.setVisibility(View.VISIBLE);
        errorMessage.setVisibility(View.INVISIBLE);


        navBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supportFinishAfterTransition();
            }
        });



        loadURL();
    }

    private void loadURL() {

        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setWebChromeClient(new MyWebViewClient());

        webView.getSettings().setAppCachePath( getApplicationContext().getCacheDir().getAbsolutePath() );
        webView.getSettings().setAllowFileAccess( true );
        webView.getSettings().setAppCacheEnabled( true );
        webView.getSettings().setJavaScriptEnabled( true );

        if(isSchedule){
            webView.getSettings().setCacheMode( WebSettings.LOAD_DEFAULT ); // load online by default
        }else{
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }


        if ( !isNetworkAvailable() ) { // loading offline
            webView.getSettings().setCacheMode( WebSettings.LOAD_CACHE_ELSE_NETWORK );
        }

        if(url==null || url.isEmpty()){
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.setText("Something went Wrong! \n Please Try Again Later");
        }else{
            webView.loadUrl(url);
        }


        myJavaInterface = new MyJavaInterface(CollaborateActivity.this,webView,getApplicationContext(),project,user);
        webView.addJavascriptInterface(myJavaInterface,"MyInterface");

    }


    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService( CONNECTIVITY_SERVICE );
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    class MyWebViewClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            super.onProgressChanged(view, newProgress);
            if (view.getProgress() >= 99) {
                progressBar.setVisibility(View.INVISIBLE);
            }else{
                progressBar.setVisibility(View.VISIBLE);
                progressBar.setProgress(newProgress);
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && webView.canGoBack()) {
            webView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PICK_IMAGE){
            Uri uri = data.getData();

            if(uri == null){
                return;
            }

            //TODO:- upload image to firebase and get path of it to save here
            project.setImage(uri.getLastPathSegment());

            myJavaInterface.setSelectedImagePath(uri.getLastPathSegment());
        }
    }

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

}


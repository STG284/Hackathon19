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

import java.util.ArrayList;
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

//        if(url==null || url.isEmpty()){
//            errorMessage.setVisibility(View.VISIBLE);
//            errorMessage.setText("Something went Wrong! \n Please Try Again Later");
//        }else{
//            webView.loadUrl(url);
//        }


        webView.loadData(getHTMLFromProjects(null),"text/html","utf-8");
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





    }



    private String getHTMLFromProjects(ArrayList<Project> projects){
        StringBuilder builder = new StringBuilder();

        String header = "<!DOCTYPE html>\n" +
                "<html>\n" +
                "<title>List</title>\n" +
                "<meta charset=\"UTF-8\">\n" +
                "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">\n" +
                "<link rel=\"stylesheet\" href=\"https://www.w3schools.com/w3css/4/w3.css\">\n" +
                "<link rel=\"stylesheet\" href=\"css/bootstrap.css\">\n" +
                "<link rel=\"stylesheet\" href=\"vendors/linericon/style.css\">\n" +
                "<link rel=\"stylesheet\" href=\"css/font-awesome.min.css\">\n" +
                "<link rel=\"stylesheet\" href=\"vendors/lightbox/simpleLightbox.css\">\n" +
                "<link rel=\"stylesheet\" href=\"vendors/nice-select/css/nice-select.css\">\n" +
                "<!-- main css -->\n" +
                "<link rel=\"stylesheet\" href=\"css/style.css\">\n" +
                "<link rel=\"stylesheet\" href=\"css/responsive.css\">\n" +
                "<body>\n" +
                "\n" +
                "\n" +
                "<!-- Page content -->\n" +
                "<div class=\"w3-content w3-padding\" style=\"max-width:1564px\">\n" +
                "\n" +
                "\n" +
                "  <!-- About Section -->\n" +
                "  <div class=\"w3-container w3-padding-32\" id=\"about\">\n" +
                "    <h1 class=\"w3-border-bottom w3-border-light-grey w3-padding-16\">Collaborate</h1>\n" +
                "  </div>";

        String footer = "\n" +
                "</div>\n" +
                "\n" +
                "</body>\n" +
                "\n" +
                "</html>";

        builder.append(header);
        builder.append("<div class=\"w3-row-padding w3-grayscale\">\n" +
                "    <br> <br><br> <br><br> <br>\n" +
                "    <div class=\"w3-col l3 m6 w3-margin-bottom\">\n" +
                "      <img src=\"img/user_image.jpg\" alt=\"John\" style=\"width:100%\">\n" +
                "      <h3>John Doe</h3>\n" +
                "      <p class=\"w3-opacity\">CEO & Founder</p>\n" +
                "      <p>Phasellus eget enim eu lectus faucibus vestibulum. Suspendisse sodales pellentesque elementum.</p>\n" +
                "      <p><button class=\"w3-button w3-light-grey w3-block\">Contact</button></p>\n" +
                "    </div>\n" +
                "  </div>");
        builder.append(footer);

        return builder.toString();

    }

}


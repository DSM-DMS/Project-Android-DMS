package com.dms.beinone.application.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.dms.beinone.application.DMSService;
import com.dms.beinone.application.R;
import com.dms.beinone.application.managers.HttpManager;
import com.dms.beinone.application.models.Meal;
import com.dms.beinone.application.models.Notice;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.dms.beinone.application.DMSService.HTTP_NO_CONTENT;
import static com.dms.beinone.application.DMSService.HTTP_OK;

public class NoticeActivity extends AppCompatActivity {

    Notice mNotice;
    TextView title_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);

        TextView toolbar_title = (TextView) findViewById(R.id.tv_toolbar_title);
        toolbar_title.setText("공지사항");

        title_TextView=(TextView)findViewById(R.id.tv_notice_title);
        title_TextView.setText("제목");

        ImageView imageView=(ImageView)findViewById(R.id.ib_toolbar_back);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(),DormitoryNoticeActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent intent=getIntent();
        String id=intent.getStringExtra("id");

        Log.d("NOTICE_ID",id);
        getNotice_detail(id);

    }

    private void setNotice(Notice notice){
        mNotice=notice;
        bind();
    }

    private void bind(){

        if(mNotice!=null){
            title_TextView.setText(mNotice.getTitle());
            title_TextView.setTextSize(12);
            WebView webView=(WebView)findViewById(R.id.notice_webView);
            webView.loadData(mNotice.getContent(),"text/html; charset=utf-8", "UTF-8");
            webView.getSettings().setBuiltInZoomControls(true);
            webView.setWebViewClient(new WebViewClient());

        }
    }

    public void getNotice_detail(String id){
        DMSService service = HttpManager.createDMSService(getApplicationContext());
        service.loadNotice_detail(id).enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                switch (response.code()) {
                    case HTTP_OK:
                        Toast.makeText(NoticeActivity.this, response.body().toString(), Toast.LENGTH_LONG).show();
                        Notice notice=getParserNotice(response.body().toString());
                        setNotice(notice);
                        break;
                    case HTTP_NO_CONTENT:
                        Toast.makeText(NoticeActivity.this, "해당아이디 정보 없음", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        break;
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("NOTICE_ERROR", t.getMessage());

            }
        });
    }

    public Notice getParserNotice(String jsonString){
        Gson gson=new Gson();
        Notice notice=gson.fromJson(jsonString, Notice.class);

        return  notice;
    }
}

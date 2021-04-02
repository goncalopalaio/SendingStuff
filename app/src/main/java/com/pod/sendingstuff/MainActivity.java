package com.pod.sendingstuff;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "SendingStuff";

    private EditText editText;

    private final Gson gson = new GsonBuilder()
            .setLenient()
            .create();

    private final Retrofit retrofit = new Retrofit.Builder()
            .baseUrl("https://ptsv2.com/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SendingStuffService service = retrofit.create(SendingStuffService.class);

        editText = findViewById(R.id.et_content);

        findViewById(R.id.btn_send).setOnClickListener(v -> {
            CharSequence text = editText.getText();

            if (text == null) {
                Log.d(TAG, "Ignoring, no text.");
                return;
            }

            sendStuffJson(service, new TextContent(text.toString()));
        });

        findViewById(R.id.btn_request_latest).setOnClickListener(v -> {
            requestLatest(service);
        });
    }

    private void sendStuffJson(@NonNull SendingStuffService service, @NonNull TextContent content) {
        service.createContent(content).enqueue(new Callback<String>() {
            @Override
            public void onResponse(@NonNull Call<String> call, @NonNull Response<String> response) {
                displayAndClear("Success: " + response);
            }

            @Override
            public void onFailure(@NonNull Call<String> call, @NonNull Throwable t) {
                displayAndClear("Error: " + t.toString());
            }
        });
    }

    private void requestLatest(@NonNull SendingStuffService service) {
        service.getLatest().enqueue(new Callback<ResponseContent>() {
            @Override
            public void onResponse(@NonNull Call<ResponseContent> call, @NonNull Response<ResponseContent> response) {
                displayAndClear("Success: " + response.body());
            }

            @Override
            public void onFailure(@NonNull Call<ResponseContent> call, @NonNull Throwable t) {
                displayAndClear("Error: " + t.toString());
            }
        });
    }

    private void displayAndClear(@NonNull String content) {
        Toast.makeText(getApplicationContext(), content, Toast.LENGTH_SHORT).show();

        Log.d(TAG, content);

        if (editText == null) return;
        editText.setText("");
    }

    public interface SendingStuffService {
        @POST("/t/76kiz-1617388031/post")
        Call<String> createContent(@Body TextContent content);

        @GET("t/76kiz-1617388031/d/latest/json")
        Call<ResponseContent> getLatest();
    }

    public static class TextContent {
        @SerializedName("text")
        private final String text;

        TextContent(@NonNull String text) {
            this.text = text;
        }

        @NonNull
        @Override
        public String toString() {
            return "Text -> " + text;
        }
    }

    public static class ResponseContent {
        @SerializedName("Body")
        private final String content;

        ResponseContent(@NonNull String content) {
            this.content = content;
        }

        @NonNull
        @Override
        public String toString() {
            return "Content -> " + content;
        }
    }
}
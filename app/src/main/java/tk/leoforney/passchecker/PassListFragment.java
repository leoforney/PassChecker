package tk.leoforney.passchecker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tk.leoforney.passchecker.dummy.DummyContent;
import tk.leoforney.passchecker.dummy.DummyContent.DummyItem;

public class PassListFragment extends Fragment {

    OkHttpClient client;
    Gson gson;

    public PassListFragment() {
    }

    public static PassListFragment newInstance() {
        PassListFragment fragment = new PassListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        client = new OkHttpClient();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        Request carRequest = new Request.Builder()
                .get()
                .url("http://" + getResources().getString(R.string.server_url) + "/pass/listcars/json")
                .addHeader("Token", getResources().getString(R.string.token))
                .build();

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            final RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            client.newCall(carRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Type listType = new TypeToken<ArrayList<Car>>(){}.getType();
                    List<Student> studentList = gson.fromJson(response.body().toString(), listType);
                    recyclerView.setAdapter(new PassRVAdapter(studentList));
                }
            });
        }
        return view;
    }


}

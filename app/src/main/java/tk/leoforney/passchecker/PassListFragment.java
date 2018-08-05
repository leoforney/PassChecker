package tk.leoforney.passchecker;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tk.leoforney.passchecker.student.StudentListAdapter;

public class PassListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private OkHttpClient client;
    private Gson gson;
    public StudentListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;

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
        client = new OkHttpClient.Builder().connectTimeout(3L, TimeUnit.SECONDS).build();
        gson = new Gson();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_students, container, false);

        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_students);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        refreshData();

        return view;
    }

    @Override
    public void onRefresh() {
        refreshData();

    }

    private void refreshData() {
        Request carRequest = new Request.Builder()
                .get()
                .url("http://" + getResources().getString(R.string.server_url) + "/pass/student/all/json")
                .addHeader("Token", getResources().getString(R.string.token))
                .build();

        // Set the adapter
        if (recyclerView != null) {
            final Context context = recyclerView.getContext();
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            client.newCall(carRequest).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String body = response.body().string();
                    Log.d("PassListFragment", body);
                    Type listType = new TypeToken<ArrayList<Student>>() {
                    }.getType();
                    List<Student> studentList = gson.fromJson(body, listType);
                    adapter = new StudentListAdapter(studentList);
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerView.setAdapter(adapter);
                            swipeRefreshLayout.setRefreshing(false);
                        }
                    });
                }
            });
        }
    }
}
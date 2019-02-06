package tk.leoforney.passchecker;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import tk.leoforney.passchecker.student.StudentListAdapter;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class PassListFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener, MaterialSearchBar.OnSearchActionListener, TextWatcher {

    private OkHttpClient client;
    private Gson gson;
    public StudentListAdapter adapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MaterialSearchBar searchBar;
    private List<Student> studentList;

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
        client = new OkHttpClient.Builder()
                .connectTimeout(3L, TimeUnit.SECONDS)
                .writeTimeout(3L, TimeUnit.SECONDS)
                .build();
        gson = new Gson();

        Log.d(PassListFragment.class.getName(), "PassList requested");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_students, container, false);

        LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(LAYOUT_INFLATER_SERVICE);

        recyclerView = view.findViewById(R.id.list);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_students);
        swipeRefreshLayout.setOnRefreshListener(this);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.refresh_progress_1,
                R.color.refresh_progress_2,
                R.color.refresh_progress_3);

        searchBar = view.findViewById(R.id.searchBar);
        searchBar.setOnSearchActionListener(this);
        searchBar.addTextChangeListener(this);

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
                .url("http://" + CredentialsManager.getInstance(getContext()).getIP() + "/pass/student/all/json")
                .addHeader("Token", CredentialsManager.getInstance(getContext()).getToken())
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
                    studentList = gson.fromJson(body, listType);
                    adapter = new StudentListAdapter(studentList);
                    Objects.requireNonNull(getActivity()).runOnUiThread(() -> {
                        recyclerView.swapAdapter(adapter, true);
                        swipeRefreshLayout.setRefreshing(false);
                    });
                }
            });
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onSearchStateChanged(boolean enabled) {

    }

    @Override
    public void onSearchConfirmed(CharSequence text) {

    }

    @Override
    public void onButtonClicked(int buttonCode) {

    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        adapter.getFilter().filter(charSequence);
        recyclerView.swapAdapter(recyclerView.getAdapter(), true);
        /*
        if (studentList != null) {
            suggestionAdapter.clearSuggestions();
            for (Student iteratedStudent: studentList) {
                boolean studentAdded = false;
                if (String.valueOf(iteratedStudent.id).toLowerCase().replace(" ", "").contains(charSequence) ||
                        iteratedStudent.name.toLowerCase().replace(" ", "").contains(charSequence)) {
                    Log.d("PassListFragment", "Student added: " + iteratedStudent.name);
                    suggestionAdapter.addSuggestion(iteratedStudent);
                    studentAdded = true;
                }
                if (!studentAdded) {
                    for (Car car: iteratedStudent.cars) {
                        if (car.plateNumber.toLowerCase().replace(" ", "").contains(charSequence)) {
                            Log.d("PassListFragment", "Car added: " + car.plateNumber);
                            suggestionAdapter.addSuggestion(iteratedStudent);
                            studentAdded = true;
                        }
                    }
                }
            }
        }*/
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }
}
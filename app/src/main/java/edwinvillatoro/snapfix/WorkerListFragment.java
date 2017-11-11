package edwinvillatoro.snapfix;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;



import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import edwinvillatoro.snapfix.objects.WorkerAdapter;
public class WorkerListFragment extends Fragment {
    private RecyclerView workersView;
    private List<String> workersList = new ArrayList<>();
    private DatabaseReference db;
    private WorkerAdapter workerAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.workers_list, container, false);
        workersView = (RecyclerView) rootView.findViewById(R.id.workers_list);
        //populate recyclerview
        workerAdapter = new WorkerAdapter(this.getActivity(), workersList);

        workersView.setAdapter(workerAdapter);
        workersView.setLayoutManager(new LinearLayoutManager(getActivity()));
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //find the ui element for the list


        //gets a reference to the database, looking at the user list
        db = FirebaseDatabase.getInstance().getReference().child("users");

        db.addChildEventListener(
                new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        updateView((Map<String, Object>) dataSnapshot.getValue());
                        workerAdapter.notifyDataSetChanged();
                    }
                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {
                    }
                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.d("Database Error", "Cancelled");
                    }
                });


    }

    private void updateView(Map<String,Object> workers) {
        String type = "";
        String worker = "";
        for (Map.Entry<String, Object> entry : workers.entrySet()) {
            if (entry.getKey().equals("name")) {
                worker = (String) entry.getValue();
            }
            if (entry.getKey().equals("type")){
                type = (String) entry.getValue();
                if (type.equals("worker")) {
                    workersList.add(worker);
                }
            }
        }

    }
}

package edwinvillatoro.snapfix.objects;

/**
 * Created by miles on 11/8/2017.
 */

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;


import edwinvillatoro.snapfix.R;
public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.ViewHolder> {

    public static class ViewHolder extends RecyclerView.ViewHolder {
        // holder containing variables for display
        public TextView name;
        private final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = (TextView) itemView.findViewById(R.id.worker_name);
        }
    }

    private List<String> workers;
    private Context context;

    public WorkerAdapter(Context context, List<String> workers) {
        this.context = context;
        this.workers = workers;
    }

    private Context getContext() {
        return context;
    }

    @Override
    public WorkerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View workerView = inflater.inflate(R.layout.item_worker, parent, false);
        ViewHolder viewHolder = new ViewHolder(workerView);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(WorkerAdapter.ViewHolder viewHolder, int position) {
        String nme = this.workers.get(position);
        TextView nm = viewHolder.name;
        nm.setText(nme);
    }

    @Override
    public int getItemCount() {
        return workers.size();
    }
}

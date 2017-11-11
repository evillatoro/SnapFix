package edwinvillatoro.snapfix.objects;

/**
 * Created by miles on 11/8/2017.
 */

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import edwinvillatoro.snapfix.R;
public class WorkerAdapter extends RecyclerView.Adapter<WorkerAdapter.ViewHolder> {

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        // holder containing variables for display
        public TextView name;
        private final Context context;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            name = (TextView) itemView.findViewById(R.id.worker_name);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    if(position != RecyclerView.NO_POSITION) {
                        String worker = workers.get(position);
                        selected_worker = worker;
                        Toast.makeText(v.getContext(), worker, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {

            toastMessage("hello");
        }
    }

    private List<String> workers;
    private Context context;
    private String selected_worker;

    public WorkerAdapter(Context context, List<String> workers) {
        this.context = context;
        this.workers = workers;
    }

    private Context getContext() {
        return context;
    }

    public String getSelected() { return selected_worker; }

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

    /**
     * customizable toast
     * @param message
     */
    private void toastMessage(String message){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

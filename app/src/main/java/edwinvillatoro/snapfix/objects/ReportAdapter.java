package edwinvillatoro.snapfix.objects;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import edwinvillatoro.snapfix.R;

/**
 * Created by Allen on 9/27/2017.
 * Adapter for RecyclerView to load reports
 */

public class ReportAdapter extends RecyclerView.Adapter<ReportAdapter.ViewHolder> {

    // provide a direct reference to each of the views
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // holder containing variables for display
        public TextView description;
        public TextView timeStamp;

        public ViewHolder(View itemView) {
            super(itemView);
            description = (TextView) itemView.findViewById(R.id.report_description);
            timeStamp = (TextView) itemView.findViewById(R.id.time_stamp);
        }
    }

    // store the reports
    private List<Report> reports;
    // store the context for easy access
    private Context context;

    // pass in the report list into the constructor
    public ReportAdapter(Context context, List<Report> reports) {
        this.reports = reports;
        this.context = context;
    }

    // easy access to the context object in the recyclerview
    private Context getContext() {
        return context;
    }

    @Override
    public ReportAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        // inflate the custom layout
        View reportView = inflater.inflate(R.layout.item_report, parent, false);
        // return a new holder instance
        ViewHolder viewHolder = new ViewHolder(reportView);
        return viewHolder;
    }

    // involves populating data into the item through holder
    @Override
    public void onBindViewHolder(ReportAdapter.ViewHolder viewHolder, int position) {
        // Get the report model from position
        Report report = this.reports.get(position);
        // Set report views based on description and timestamp
        TextView des = viewHolder.description;
        des.setText(report.getDescription());
        TextView ts = viewHolder.timeStamp;
        ts.setText(report.getTimestamp());
    }

    // returns the total count of reports in the list
    @Override
    public int getItemCount() {
        return reports.size();
    }

    /*public void updateData(List<Report> data) {
        reports.clear();
        reports = data;
        this.notifyDataSetChanged();
    }*/

}

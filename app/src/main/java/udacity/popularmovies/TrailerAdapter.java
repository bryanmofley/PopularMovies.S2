package udacity.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Bryan K Mofley on October 06, 2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerAdapterViewHolder> {
    private static final String LOG_TAG = TrailerAdapter.class.getSimpleName();

    private final TrailerAdapterOnClickHandler mClickHandler;
    private Context mContext;
    private Cursor mCursor;

    public TrailerAdapter(Context context, TrailerAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    @Override
    public TrailerAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.trailer_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new TrailerAdapter.TrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailerAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final String trailerTitle = mCursor.getString(MovieDetailActivity.INDEX_TRAILER_NAME);
        holder.mTrailerNameTextView.setText(trailerTitle);

    }

    @Override
    public int getItemCount() {
        return (null != mCursor ? mCursor.getCount() : 0);
    }

    void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public interface TrailerAdapterOnClickHandler {
        void onTrailerClick(String youTubeKey);
    }

    public class TrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView mTrailerNameTextView;

        public TrailerAdapterViewHolder(View itemView) {
            super(itemView);
            mTrailerNameTextView = (TextView) itemView.findViewById(R.id.tv_trailer_title);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            String youTubeKey = mCursor.getString(MovieDetailActivity.INDEX_TRAILER_KEY);
            mClickHandler.onTrailerClick(youTubeKey);
        }
    }
}

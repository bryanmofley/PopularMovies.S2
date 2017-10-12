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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewAdapterViewHolder> {
    private static final String LOG_TAG = ReviewAdapter.class.getSimpleName();

    private Context mContext;
    private Cursor mCursor;

    public ReviewAdapter(Context mContext) {
        this.mContext = mContext;
    }

    @Override
    public ReviewAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.review_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);
        return new ReviewAdapter.ReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ReviewAdapterViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        final String reviewAuthor = mCursor.getString(MovieDetailActivity.INDEX_REVIEW_AUTHOR);
        final String reviewTitle = mCursor.getString(MovieDetailActivity.INDEX_REVIEW_CONTENT);
        final String reviewAuthorDisplay = mContext.getText(R.string.activity_detail_reviews_author) + " " + reviewAuthor;
        holder.mReviewAuthorTextView.setText(reviewAuthorDisplay);
        holder.mReviewContentTextView.setText(reviewTitle);
    }

    @Override
    public int getItemCount() {
        return (null != mCursor ? mCursor.getCount() : 0);
    }

    void swapCursor(Cursor cursor) {
        mCursor = cursor;
        notifyDataSetChanged();
    }

    public class ReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView mReviewAuthorTextView;
        public final TextView mReviewContentTextView;

        public ReviewAdapterViewHolder(View itemView) {
            super(itemView);
            mReviewContentTextView = (TextView) itemView.findViewById(R.id.tv_review_content);
            mReviewAuthorTextView = (TextView) itemView.findViewById(R.id.tv_review_author);
        }
    }
}

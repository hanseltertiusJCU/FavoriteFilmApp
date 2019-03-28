package com.example.favoritefilmapp.support;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.example.favoritefilmapp.R;

// Class ini berguna untuk enable click di recyclerview item
public class RecyclerViewItemClickSupport {

    // Create variable recycler view and item click listener interface
    private final RecyclerView mRecyclerView;
    private OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener;

    // Create View.OnClickListener object
    private View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (mOnRecyclerViewItemClickListener != null) {
                // Initiate RecyclerView.ViewHolder object
                RecyclerView.ViewHolder holder = mRecyclerView.getChildViewHolder(view);
                // Use the interface method based on adapter position in RecyclerView
                mOnRecyclerViewItemClickListener.onItemClicked(mRecyclerView, holder.getAdapterPosition(), view);
            }
        }
    };

    // Constructor yang menampung recycler view dan gunanya itu untuk add click listener ke recycler view item
    private RecyclerViewItemClickSupport(RecyclerView recyclerView) {
        this.mRecyclerView = recyclerView;
        mRecyclerView.setTag(R.id.recycler_view_item_click_support, this); // Set tag ke recycler view
        // Attach on click listener to recyclerview
        RecyclerView.OnChildAttachStateChangeListener mAttachListener = new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
                if (mOnRecyclerViewItemClickListener != null) {
                    view.setOnClickListener(mOnClickListener); // Method ini terhubung dengan onClick (alias call back method since event (action) listener berhubungan dengan callback (reaction)) di variable View.OnClickListener object
                }
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {

            }
        };
        mRecyclerView.addOnChildAttachStateChangeListener(mAttachListener); // Add RecyclerView.OnChildAttachStageChangeListener ke RecyclerView
    }

    // Method to return support that attach support to the view
    public static RecyclerViewItemClickSupport addClickSupportToView(RecyclerView recyclerView) {
        // Get recycler view tag {@link constructor yg memiliki parameter RecyclerView} dan return RecyclerViewItemClickSupport dengan cast
        RecyclerViewItemClickSupport recyclerViewItemClickSupport = (RecyclerViewItemClickSupport) recyclerView.getTag(R.id.recycler_view_item_click_support);
        // Cek jika objectnya itu tidak ada
        if (recyclerViewItemClickSupport == null) {
            recyclerViewItemClickSupport = new RecyclerViewItemClickSupport(recyclerView); // Initiate new object by bringing the recyclerview parameter as the parameter of the constructor of new object
        }
        return recyclerViewItemClickSupport; // Return the object
    }

    // Setter method untuk set recyclerview item click listener bedasarkan value dari parameter
    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener mOnRecyclerViewItemClickListener) {
        this.mOnRecyclerViewItemClickListener = mOnRecyclerViewItemClickListener;
    }

    // Create Interface and create a method without implementation
    public interface OnRecyclerViewItemClickListener {
        void onItemClicked(RecyclerView recyclerView, int position, View view);
    }
}

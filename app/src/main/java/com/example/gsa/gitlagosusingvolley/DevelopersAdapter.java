package com.example.gsa.gitlagosusingvolley;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gsa.gitlagosusingvolley.R;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * Created by GSA on 10/10/2017.
 * This handles the whole operation of providing the items
 * displayed in the {@Link RecyclerView} Object
 */
public class DevelopersAdapter extends RecyclerView.Adapter<DevelopersAdapter.MyViewHolder> {


    // Variable declarations
    private List<DeveloperList> developerLists;
    private Context context;

    // A public Constructor for the class
    public DevelopersAdapter(Context context, List<DeveloperList> developerLists){
        this.context = context;
        this.developerLists = developerLists;
    }

    /**Declare and find reference to the child view of the layout
     * created in {@Link OnCreateViewHolder} method
     * It also represented all the views to be updated in the UI
     */
    public class  MyViewHolder extends RecyclerView.ViewHolder {
        private TextView username;
        private LinearLayout layout;
        private CircleImageView imageView;


        public MyViewHolder(View itemView) {
            super(itemView);
            username = (TextView) itemView.findViewById(R.id.user_name);
            imageView = (CircleImageView) itemView.findViewById(R.id.profile_pic);
            layout = (LinearLayout) itemView.findViewById(R.id.layout);
        }
    }

    // Inflate a new view that contains other child view
    // which are to be updated in the UI
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_developer_list,parent,false);
        return new MyViewHolder(view);
    }

    /**
     * This actually updates the UI with the right information
     * It updates @param username with the developer's username,
     * the profile picture using the Picasso library
     * and handle the click event on each developer list.
     * It also sends intent to each developer's profile
     */
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final DeveloperList developer = developerLists.get(position);
        holder.username.setText(developer.getLogin());
        Picasso.with(this.context)
                .load(developer.getAvatarUrl())
                .noFade()
                .placeholder(R.drawable.main_dummy)
                .into(holder.imageView);
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // send an intent to open each developer profile
                // together with extra information of the
                // username,image url and the html url.
                Intent userProfile = new Intent(v.getContext(), com.example.gsa.gitlagosusingvolley.Userprofile_Activity.class);
                userProfile.putExtra("username",developer.getLogin());
                userProfile.putExtra("image_url",developer.getAvatarUrl());
                userProfile.putExtra("html_url",developer.getHtmlUrl());
                v.getContext().startActivity(userProfile);
            }
        });
    }

    // Return the size of the dataset (invoked by the layout manager)
    // i.e number of items in the adapter
    @Override
    public int getItemCount() {
        return developerLists.size();
    }
}

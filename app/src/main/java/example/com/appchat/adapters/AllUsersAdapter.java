/**
 * Module Name/Class			:	AllUsersAdapter
 * Author Name					:	Sachin Arora
 * Date							:	May,31 2018
 * Purpose						:	This adapter shows all user
 */

package example.com.appchat.adapters;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import java.util.List;

import example.com.appchat.R;
import example.com.appchat.databinding.AllUserLayoutBinding;
import example.com.appchat.interfaces.ClickListenerInterface;
import example.com.appchat.models.User;

public class AllUsersAdapter extends RecyclerView.Adapter<AllUsersAdapter.MyViewHolder> {

    private AppCompatActivity appCompatActivity;
    private List<User> allUsersArrayList;
    private ClickListenerInterface mInterface;

    public AllUsersAdapter(AppCompatActivity appCompatActivity, List<User> posts, ClickListenerInterface clickListenerInterface) {

        this.appCompatActivity = appCompatActivity;
        allUsersArrayList = posts;
        mInterface = clickListenerInterface;

    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        private AllUserLayoutBinding mViewDataBinding;

        public MyViewHolder(AllUserLayoutBinding viewDataBinding) {
            super(viewDataBinding.getRoot());

            mViewDataBinding = viewDataBinding;
            mViewDataBinding.executePendingBindings();
        }

        public AllUserLayoutBinding getViewDataBinding() {
            return mViewDataBinding;
        }
    }

    @Override
    public AllUsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        AllUserLayoutBinding binding = DataBindingUtil.inflate(LayoutInflater
                .from(viewGroup.getContext()), R.layout.all_user_layout, viewGroup, false);

        return new AllUsersAdapter.MyViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        User user = allUsersArrayList.get(position);
        AllUserLayoutBinding viewDataBinding = holder.getViewDataBinding();

        viewDataBinding.tvName.setText(user.getName());
        viewDataBinding.cvUser.setOnClickListener(v -> {

            mInterface.onClickPosition(position);

        });
    }

    @Override
    public int getItemCount() {

        return allUsersArrayList.size();

    }
}

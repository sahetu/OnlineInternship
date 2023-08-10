package online.internship;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HomeFragment extends Fragment {

    RecyclerView recyclerView;
    String[] nameArray = {"Person 1","Person 2","Person 3"};
    int[] imageArray = {R.drawable.ic_home,R.drawable.ic_user,R.drawable.login_image};


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.home_recyclerview);

        //Display Data In List
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        //Display Data In Grid
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL));

        //Display Data In Horizontal Scroll
        //recyclerView.setLayoutManager(new StaggeredGridLayoutManager(1,StaggeredGridLayoutManager.HORIZONTAL));

        recyclerView.setItemAnimator(new DefaultItemAnimator());

        ProductAdapter adapter = new ProductAdapter(getActivity(),nameArray,imageArray);
        recyclerView.setAdapter(adapter);

        return view;
    }
}
package com.vpaliy.melophile.ui.user.favorite;

import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vpaliy.domain.model.Track;
import com.vpaliy.melophile.App;
import com.vpaliy.melophile.R;
import com.vpaliy.melophile.di.component.DaggerViewComponent;
import com.vpaliy.melophile.di.module.PresenterModule;
import com.vpaliy.melophile.ui.base.bus.RxBus;
import com.vpaliy.melophile.ui.user.UserTracksAdapter;
import com.vpaliy.melophile.ui.utils.Constants;
import java.util.List;
import butterknife.ButterKnife;
import android.widget.TextView;
import static com.vpaliy.melophile.ui.user.favorite.FavoriteContract.Presenter;
import butterknife.BindView;
import butterknife.OnClick;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import javax.inject.Inject;

public class FavoriteFragment extends BottomSheetDialogFragment
        implements FavoriteContract.View{

    private Presenter presenter;
    private String id;

    @BindView(R.id.favorites)
    protected RecyclerView favorites;

    @Inject
    protected RxBus rxBus;

    @BindView(R.id.title)
    protected TextView title;

    @BindView(R.id.action_bar)
    protected View actionBar;

    public static FavoriteFragment newInstance(Bundle args){
        FavoriteFragment fragment=new FavoriteFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        DaggerViewComponent.builder()
                .applicationComponent(App.appInstance().appComponent())
                .presenterModule(new PresenterModule())
                .build().inject(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_user_favorite,container,false);
        ButterKnife.bind(this,root);
        return root;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        extractId(savedInstanceState);
        if(view!=null){
            presenter.start(id);
            favorites.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    actionBar.setActivated(dy>0);
                }
            });
        }
    }

    @OnClick(R.id.close)
    public void close(){
        getActivity().onBackPressed();
    }

    @Override
    public void showTracks(@NonNull List<Track> tracks) {
        UserTracksAdapter adapter=new UserTracksAdapter(getContext(),rxBus);
        adapter.setData(tracks);
        favorites.setAdapter(adapter);
    }

    @Override
    public void showEmpty() {
        //TODO show add a message
    }

    @Override
    public void showError() {
        //TODO add an error message
    }

    private void extractId(Bundle bundle){
        if(bundle==null) bundle=getArguments();
        id=bundle.getString(Constants.EXTRA_ID);
    }

    @Inject
    @Override
    public void attachPresenter(@NonNull Presenter presenter) {
        this.presenter=presenter;
        this.presenter.attachView(this);
    }
}

package com.mTapWiki.shaktis.wikipedia.Article;




import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mTapWiki.shaktis.wikipedia.Helper.Volley.VolleyController;
import com.mTapWiki.shaktis.wikipedia.R;

import java.util.List;

/**
 * Created by shaktis on 28/11/17.
 */

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.MyViewHolder>  {
    private List<Article> ArticleList;
    private List<Article> FilteredArticleList;
    private FilteredArticleListListener listener;


    public class MyViewHolder extends RecyclerView.ViewHolder{
        LinearLayout lv,LinearLayout1;
        TextView textvwTitle,txtvwExtract;
        TextView img;
        public ImageLoader imageLoader;
        NetworkImageView thumbNail;
        public MyViewHolder(View view){
            super(view);
            textvwTitle = (TextView) view.findViewById(R.id.txtvwTitle);
            txtvwExtract = (TextView) view.findViewById(R.id.txtvwExtract);
            thumbNail = (NetworkImageView) view.findViewById(R.id.thumbnail);
            imageLoader = VolleyController.getInstance().getImageLoader();
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onArticleSelected(FilteredArticleList.get(getAdapterPosition()));
                }
            });


        }
    }

    public ArticleAdapter(List<Article> ArticleList,FilteredArticleListListener listener){
        this.ArticleList=ArticleList;
        this.FilteredArticleList=ArticleList;
        this.listener=listener;




    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent,int viewType){
        View itemView=LayoutInflater.from(parent.getContext()).inflate(R.layout.article_list_row,parent,false);

        return new MyViewHolder(itemView);

    }
    @Override
    public void onBindViewHolder(MyViewHolder holder,int position){
        Article article=FilteredArticleList.get(position);
        holder.textvwTitle.setText(article.getTitle());
        holder.txtvwExtract.setText(article.getExtract());
        holder.thumbNail.setImageUrl(article.getImgSrc(),VolleyController.getInstance().getImageLoader());

    }
    @Override
    public int getItemCount() {
        return FilteredArticleList.size();
    }


    public interface FilteredArticleListListener{
        void onArticleSelected(Article article);
    }

}


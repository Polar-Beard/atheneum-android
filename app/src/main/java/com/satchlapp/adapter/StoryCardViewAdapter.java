package com.satchlapp.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.graphics.Palette;
import android.support.v7.graphics.Target;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.satchlapp.PaletteTransformation;
import com.satchlapp.R;
import com.satchlapp.model.Category;
import com.satchlapp.model.Story;
import com.satchlapp.model.User;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

/**
 * Created by Sara on 3/9/2017.
 */
public class StoryCardViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Category category;
    private Context context;

    public StoryCardViewAdapter(Category category){
        this.category = category;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView;
        RecyclerView.ViewHolder viewHolder = null;

        switch (viewType){
            case Category.DISPLAY_TYPE_DETAILED:
                itemView = inflater.inflate(R.layout.list_item_detailed_story, parent, false);
                viewHolder = new DetailedStoryViewHolder(itemView);
                break;
            case Category.DISPLAY_TYPE_SIMPLE:
                itemView = inflater.inflate(R.layout.list_item_simple_story, parent, false);
                viewHolder = new SimpleStoryViewHolder(itemView);
                break;
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position){
        final Story story = category.getStoryList().get(position);

        if(viewHolder instanceof DetailedStoryViewHolder){
            final DetailedStoryViewHolder castedViewHolder = (DetailedStoryViewHolder) viewHolder;

            User author = story.getAuthor();
            String name = author.getFirstName() + " " + author.getLastName();
            castedViewHolder.textViewAuthorsName.setText(name);
            Picasso.with(context)
                    .load(author.getProfilePictureUrl())
                    .into(castedViewHolder.imageViewAuthorProfile);

            //final List<Palette.Swatch> swatch = new ArrayList<>();
            Picasso.with(context)
                    .load(story.getCoverImageUrl())
                    .fit().centerCrop()
                    .transform(PaletteTransformation.getInstance())
                    .into(castedViewHolder.imageViewCoverImage, new Callback.EmptyCallback() {
                        @Override
                        public void onSuccess() {
                            Bitmap bitmap = ((BitmapDrawable) castedViewHolder.imageViewCoverImage.getDrawable()).getBitmap();
                            Palette palette = PaletteTransformation.getPalette(bitmap);
                            Palette.Swatch swatch = getColorSwatch(palette);
                            if (swatch != null) {
                                castedViewHolder.textViewTitle.setBackgroundColor(ColorUtils.setAlphaComponent(swatch.getRgb(), 166));
                                castedViewHolder.textViewTitle.setTextColor(ColorUtils.setAlphaComponent(swatch.getTitleTextColor(), 255));
                            }
                        }
                    });
            castedViewHolder.textViewTitle.setText(story.getTitle());
            castedViewHolder.textViewDescriptionShort.setText(story.getDescription().substring(0, 150));

            castedViewHolder.buttonExpandDescription.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    castedViewHolder.textViewDescriptionShort.setVisibility(View.GONE);
                    castedViewHolder.textViewDescriptionFull.setText(story.getDescription());
                    castedViewHolder.textViewDescriptionFull.setVisibility(View.VISIBLE);
                }
            });

            castedViewHolder.textViewDescriptionFull.setVisibility(View.GONE);

        } else if(viewHolder instanceof SimpleStoryViewHolder){
            SimpleStoryViewHolder castedViewHolder = (SimpleStoryViewHolder) viewHolder;
            //do something else
        }
    }

    @Override
    public int getItemCount(){
        return category.getStoryList().size();
    }

    @Override
    public int getItemViewType(int position){
        return category.getDisplayType();
    }

    class DetailedStoryViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public TextView textViewTitle;
        public TextView textViewAuthorsName;
        public ImageView imageViewAuthorProfile;
        public TextView textViewDescriptionShort;
        public ImageView imageViewCoverImage;
        public ImageButton buttonExpandDescription;
        public TextView textViewDescriptionFull;

        public DetailedStoryViewHolder(View itemView){
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.listItemDetailedStoryCardView);
            textViewTitle = (TextView) itemView.findViewById(R.id.listItemDetailedStoryTextViewTitle);
            textViewAuthorsName = (TextView) itemView.findViewById(R.id.listItemDetailedStoryTextViewAuthor);
            imageViewAuthorProfile = (ImageView) itemView.findViewById(R.id.listItemDetailedStoryImageViewAuthorProfile);
            textViewDescriptionShort = (TextView) itemView.findViewById(R.id.listItemDetailedStoryTextViewDescriptionShort);
            imageViewCoverImage = (ImageView) itemView.findViewById(R.id.listItemDetailedStoryImageViewCoverImage);
            buttonExpandDescription = (ImageButton) itemView.findViewById(R.id.listItemDetailedStoryButtonExpandDescription);
            textViewDescriptionFull = (TextView) itemView.findViewById(R.id.listItemDetailedStoryTextViewDescriptionFull);
        }
    }

    class SimpleStoryViewHolder extends RecyclerView.ViewHolder{

        public SimpleStoryViewHolder(View itemView){
            super(itemView);
        }
    }

    private Palette.Swatch getColorSwatch(Palette palette){
        //We want the vibrant swatch by default. Sometimes this fails, so we'll take any swatch
        //at that point. And if no swatch exists, we'll return a grey one.
        Palette.Swatch swatch = palette.getVibrantSwatch();

        //We also want to make sure the swatch selected is not the dominate color in the image.
        //It just looks bad.

        //It's set to false out of the if statement so that I can use it later.
        boolean swatchIsDominateColor = false;
        if(swatch != null) {
            swatchIsDominateColor = true;
            for (Palette.Swatch s : palette.getSwatches()) {
                if (s.getPopulation() > swatch.getPopulation()) {
                    swatchIsDominateColor = false;
                    break;
                }
            }
        }
        if(swatch == null || swatchIsDominateColor){
            for(Target target: palette.getTargets()){
                if(!target.equals(Target.VIBRANT)){
                    if(palette.getSwatchForTarget(target) != null){
                        swatch = palette.getSwatchForTarget(target);
                        break;
                    }
                }
            }
        }

        if(swatch == null){
            swatch = new Palette.Swatch(Color.GRAY,0);
        }

        return swatch;
    }

}

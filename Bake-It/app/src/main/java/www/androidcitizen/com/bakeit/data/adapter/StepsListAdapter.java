package www.androidcitizen.com.bakeit.data.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import www.androidcitizen.com.bakeit.R;
import www.androidcitizen.com.bakeit.data.custominterface.StepClickListenerInterface;
import www.androidcitizen.com.bakeit.data.model.Step;


/**
 * Created by Mahi on 27/08/18.
 * www.androidcitizen.com
 */

public class StepsListAdapter extends RecyclerView.Adapter<StepsListAdapter.StepsViewHolder>{

    private List<Step> steps = null;

    private final Context context;
    private final StepClickListenerInterface stepClickListenerInterface;

    public StepsListAdapter(Context context, StepClickListenerInterface stepClickListenerInterface) {
        this.context = context;
        this.stepClickListenerInterface = stepClickListenerInterface;
    }

    @NonNull
    @Override
    public StepsViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {

        View rootView = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.steps_item_view, viewGroup, false);

        return new StepsViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(@NonNull StepsViewHolder recipeViewHolder, int iPosition) {

        recipeViewHolder.onBind(iPosition);
    }

    @Override
    public int getItemCount() {
        if (null != steps)
            return steps.size();
        else
            return 0;
    }

    class StepsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ConstraintLayout stepView;
        ImageView stepImage;
        TextView stepText;


        StepsViewHolder(@NonNull View view) {
            super(view);
            stepView = view.findViewById(R.id.stepView);
            stepImage = view.findViewById(R.id.stepImage);
            stepText = view.findViewById(R.id.stepText);

            stepView.setOnClickListener(this);
        }

        void onBind(int iPosition) {
            stepText.setText(context.getString(R.string.step,
                    steps.get(iPosition).getId(), steps.get(iPosition).getShortDescription()));

            if(0 == iPosition % 2) {
                stepView.setBackgroundColor(context.getResources().getColor(R.color.lightGrey));
            }

            if(isVideoPresent(iPosition)){
                stepImage.setImageResource(R.drawable.ic_notes);
            } else {
                stepImage.setImageResource(R.drawable.ic_video);
            }
        }

        @Override
        public void onClick(View view) {
            int index = getAdapterPosition();
            stepClickListenerInterface.onStepSelected(index);
        }
    }

    public void updateSteps(List<Step> newSteps){
        steps = newSteps;
        notifyDataSetChanged();
    }

    private boolean isVideoPresent(int iIndex){
        return steps.get(iIndex).getVideoURL().isEmpty();
    }

}

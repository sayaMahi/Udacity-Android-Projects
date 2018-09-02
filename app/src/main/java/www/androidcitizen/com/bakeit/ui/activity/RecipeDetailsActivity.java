package www.androidcitizen.com.bakeit.ui.activity;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import www.androidcitizen.com.bakeit.R;
import www.androidcitizen.com.bakeit.data.custominterface.StepClickListenerInterface;
import www.androidcitizen.com.bakeit.data.model.Ingredient;
import www.androidcitizen.com.bakeit.data.model.Recipe;
import www.androidcitizen.com.bakeit.data.model.Step;
import www.androidcitizen.com.bakeit.ui.fragment.IngredientsListFragment;
import www.androidcitizen.com.bakeit.ui.fragment.StepsListFragment;
import www.androidcitizen.com.bakeit.util.Constants;

public class RecipeDetailsActivity extends AppCompatActivity implements StepClickListenerInterface{

    List<Step> steps;
    List<Ingredient> ingredients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_detail);

        if(null == savedInstanceState) {
            Intent intent = getIntent();
            if(intent.hasExtra(Constants.RECIPE_KEY)) {
                Recipe recipe = intent.getParcelableExtra(Constants.RECIPE_KEY);
                ingredients = recipe.getIngredients();
                steps = recipe.getSteps();

                setTitle(recipe.getName());
            }

            if (null != ingredients) {
                IngredientsListFragment ingredientsFragment = IngredientsListFragment.newInstance(ingredients);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.recipeListContainer, ingredientsFragment)
                        .commit();
            }

            if (null != steps) {
                StepsListFragment stepsListFragment = StepsListFragment.newInstance(steps);

                getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.stepsListContainer, stepsListFragment)
                        .commit();
            }

        }
    }

    @Override
    public void onStepSelected(int iIndex) {
        Intent intent = new Intent(this, StepActivity.class);
        intent.putExtra(Constants.STEP_SELECTED_INDEX_KEY, iIndex);
        intent.putParcelableArrayListExtra(Constants.STEPS_KEY, (ArrayList<Step>) steps);
        startActivity(intent);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}

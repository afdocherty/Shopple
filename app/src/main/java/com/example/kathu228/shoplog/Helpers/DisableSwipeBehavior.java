package com.example.kathu228.shoplog.Helpers;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.SwipeDismissBehavior;
import android.view.View;

/**
 * Created by kathu228 on 7/27/17.
 */

public class DisableSwipeBehavior extends SwipeDismissBehavior<Snackbar.SnackbarLayout> {
    @Override
    public boolean canSwipeDismissView(@NonNull View view) {
        return false;
    }
}
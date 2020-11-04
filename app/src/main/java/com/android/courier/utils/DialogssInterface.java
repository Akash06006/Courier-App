package com.android.courier.utils;

/*
 * Created by admin on 22-12-2017.
 */

import android.view.View;

public interface DialogssInterface {

    void onDialogConfirmAction(View mView, String mKey);
    void onDialogCancelAction(View mView, String mKey);
}

package hotchemi.android.rate;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.view.View;

import static hotchemi.android.rate.IntentHelper.createIntentForAmazonAppstore;
import static hotchemi.android.rate.IntentHelper.createIntentForGooglePlay;
import static hotchemi.android.rate.PreferenceHelper.setAgreeShowDialog;
import static hotchemi.android.rate.PreferenceHelper.setRemindInterval;
import static hotchemi.android.rate.Utils.getDialogBuilder;

final class DialogManager {

    private DialogManager() {
    }

    static Dialog create(final Context context, final DialogOptions options) {
        AlertDialog.Builder builder = getDialogBuilder(context, options.getDialogStyleResId());
        builder.setMessage(options.getMessageText(context));

        if (options.shouldShowTitle()) builder.setTitle(options.getTitleText(context));

        builder.setCancelable(options.getCancelable());

        View view = options.getView();
        if (view != null) builder.setView(view);

        final OnClickButtonListener listener = options.getListener();

        builder.setPositiveButton(options.getPositiveText(context), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final Intent intentToAppstore = options.getStoreType() == AppRate.StoreType.GOOGLEPLAY ?
                createIntentForGooglePlay(context) : createIntentForAmazonAppstore(context);
                context.startActivity(intentToAppstore);
                setAgreeShowDialog(context, false);
                if (listener != null) listener.onClickButton(which);
            }
        });

        if (options.shouldShowNeutralButton()) {
            builder.setNeutralButton(options.getNeutralText(context), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setRemindInterval(context);
                    if (listener != null) listener.onClickButton(which);
                }
            });
        }

        builder.setNegativeButton(options.getNegativeText(context), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setAgreeShowDialog(context, false);
                if (listener != null) listener.onClickButton(which);
            }
        });

        return builder.create();
    }

}
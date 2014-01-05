package com.example.brainyquote;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.util.AttributeSet;

import com.example.brainyquote.Tools.DeleteAllFavsTask;
  
public class ClearFavsDiagPref extends DialogPreference  
{  
    //An object that will store the Activity's context  
    protected Context context;  
  
    public ClearFavsDiagPref(Context context, AttributeSet attrs)   
    {  
        super(context, attrs);  
  
        //Store the calling Activity's context  
        this.context = context;  
    }  
  
    @Override  
    public void onClick(DialogInterface dialog, int which)   
    {  
        super.onClick(dialog, which);  
  
        //delete all favorited quotes if user selects
        //the positive button
        if(which == DialogInterface.BUTTON_POSITIVE)  
        {  
            new DeleteAllFavsTask().execute(BaseActivity.appDir);
        }  
    }  
}  
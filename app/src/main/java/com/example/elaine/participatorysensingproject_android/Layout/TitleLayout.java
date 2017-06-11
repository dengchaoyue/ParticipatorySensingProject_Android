/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.Layout;


import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

public class TitleLayout extends LinearLayout {
	static TextView button1,button2;
	static TextView textview;
	public TitleLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.title_layout, this);//remember
		button1 = (TextView) findViewById(R.id.Button1);
		button2 = (TextView) findViewById(R.id.Button2);
		textview = (TextView)findViewById(R.id.centerText);
	}
}

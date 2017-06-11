/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.Layout;

import com.example.elaine.participatorysensingproject_android.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * @author dengchaoyue
 *
 */
public class TaskLayout extends LinearLayout {
	static Button button1,button2;
	static TextView textview;
	public TaskLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		LayoutInflater.from(context).inflate(R.layout.task_navigationbar_layout, this);//remember
		button1 = (Button)findViewById(R.id.Button1);
		button2 = (Button)findViewById(R.id.Button2);
	}
}

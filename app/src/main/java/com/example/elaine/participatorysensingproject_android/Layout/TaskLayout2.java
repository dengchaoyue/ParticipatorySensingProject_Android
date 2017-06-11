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

/**
 * @author dengchaoyue
 *
 */
public class TaskLayout2 extends LinearLayout {
		static Button button1,button2,button3,button4;
		public TaskLayout2(Context context, AttributeSet attrs) {
			super(context, attrs);
			// TODO Auto-generated constructor stub
			LayoutInflater.from(context).inflate(R.layout.task_classify_layout, this);//remember
			button1 = (Button)findViewById(R.id.Button1);
			button2 = (Button)findViewById(R.id.Button2);
			button3 = (Button)findViewById(R.id.Button3);
			button4 = (Button)findViewById(R.id.Button4);
		}
}

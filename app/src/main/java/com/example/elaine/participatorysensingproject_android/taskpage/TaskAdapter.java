/**
 * class name: 
 * class description: 
 * author: dengchaoyue 
 * version: 1.0
 */
package com.example.elaine.participatorysensingproject_android.taskpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.elaine.participatorysensingproject_android.R;

import java.util.List;

public class TaskAdapter extends ArrayAdapter<TaskItem> {
	private int resourceId;
	View view;
	ViewHolder viewHolder;
	LayoutInflater inflater;
	public TaskAdapter(Context context, int textViewResourceId, List<TaskItem> objects) {
		super(context, textViewResourceId, objects);
		resourceId = textViewResourceId;
		// TODO Auto-generated constructor stub
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public View getView(int position, View convertView, ViewGroup parent){
		TaskItem task = getItem(position);
		if(convertView == null){
			view = inflater.inflate(resourceId, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.taskName = (TextView)view.findViewById(R.id.task_position);
			//viewHolder.taskID = (TextView)view.findViewById(R.id.taskID);
			viewHolder.taskNum = (TextView)view.findViewById(R.id.task_num);
			viewHolder.taskMoney = (TextView)view.findViewById(R.id.task_money);
			viewHolder.taskTime = (TextView)view.findViewById(R.id.task_time);
			view.setTag(viewHolder);
		}else{
			view = convertView;
			viewHolder = (ViewHolder)view.getTag();
		}
		viewHolder.taskName.setText("地址：" + task.getTaskDescription());
		if (task.getIncentiveType() == 2) {
			viewHolder.taskMoney.setText("竞价参与~");
		} else {
			viewHolder.taskMoney.setText("奖励：" + task.getEarn());
		}
		viewHolder.taskNum.setText("任务：拍摄" + task.getDataNumber() + "张照片");
		viewHolder.taskTime.setText("剩余时间：" + task.getDiffTime());
		if(task.getIncentiveType() == 1){
			viewHolder.taskImage.setImageResource(R.drawable.tasktype_1);
		}else if(task.getIncentiveType() == 2){
			viewHolder.taskImage.setImageResource(R.drawable.tasktype_2);
		}else if(task.getIncentiveType() == 3){
			viewHolder.taskImage.setImageResource(R.drawable.tasktype_3);
		}
/*		TextView taskName = (TextView)view.findViewById(R.id.task_position);
		taskName.setText("地址：" + task.getTaskName());
		TextView taskNum = (TextView)view.findViewById(R.id.task_num);
		taskNum.setText("任务：拍摄" + task.getTaskNum() + "张照片");
		TextView taskMoney = (TextView)view.findViewById(R.id.task_money);
		taskMoney.setText("奖励：" + task.getTaskMoney() + "元");
		TextView taskTime = (TextView)view.findViewById(R.id.task_time);
		taskTime.setText("剩余时间：" + task.getTaskTime() + "分钟");
		ImageView taskImage = (ImageView)view.findViewById(R.id.task_type);
		if(task.getIncentiveType() == 1){
			taskImage.setImageResource(R.drawable.tasktype_1);
		}else if(task.getIncentiveType() == 2){
			taskImage.setImageResource(R.drawable.tasktype_2);
		}else if(task.getIncentiveType() == 3){
			taskImage.setImageResource(R.drawable.tasktype_3);
		}*/
		return view;
	}
	
	class ViewHolder{
		TextView taskName, taskNum, taskMoney, taskTime;
		ImageView taskImage;
	}

}

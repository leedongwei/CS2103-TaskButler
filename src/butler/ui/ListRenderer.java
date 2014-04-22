// @author A0097802Y

package butler.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.logging.Logger;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

import butler.common.LoggerPreset;
import butler.common.Task;


class ListRenderer extends DefaultListCellRenderer
{
	private List<Task> tasks;
	private int page;
	private int size;

	private static final Logger Log = LoggerPreset.getLogger();
	
	public ListRenderer(List<Task> tasks, int page, int size){
		
		assert tasks != null;
		assert page != 0;
		assert size != 0;
		
		this.tasks = tasks;
		this.page = page;
		this.size = size;
	}


	public Component getListCellRendererComponent(JList list,
			Object value,
			int index,
			boolean isSelected,
			boolean cellHasFocus)
	{

		Component c = super.getListCellRendererComponent(
				list,value,index,isSelected,cellHasFocus);

		for (int i = 0 ; i < tasks.size() ; i++){

			if (tasks.get(i).isFinished()){

				colorFinishedTasks(i, index, c);

			}else if (tasks.get(i).isOverdue()){

				colorOverdueTasks(i, index, c);
			}
		}
		
		Log.info("Tasks are highlighted based on their status");
		
		return c;
	}
	
	public void colorFinishedTasks(int i, int index, Component c){
		int multiplicity = i / size + 1;

		if (multiplicity == page && i % size == index){
			c.setForeground(Color.GREEN.darker());
		}
	}
	
	public void colorOverdueTasks(int i, int index, Component c){
		int multiplicity = i / size + 1;

		if (multiplicity == page && i % size == index){
			c.setForeground(Color.red);
		}
	}
}

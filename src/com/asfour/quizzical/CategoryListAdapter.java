package com.asfour.quizzical;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.asfour.quizzical.R;

/**Custom Base Adapter. 
 * Uses custom font in ListView
 * 
 * @author Waqqas
 *
 */
public class CategoryListAdapter extends BaseAdapter {

	private static final int FONT_SIZE_BIG = 20;
	
	private Context context;
	private List<String> items;
	private Typeface font;
	
	/**Constructor
	 * 
	 * @param context Activity
	 * @param items list of Categories.
	 */
	public CategoryListAdapter(Context context,List<String> items)
	{
		this.context = context;
		this.items = items;
		//load custom font.
		this.font = Typeface.createFromAsset(context.getAssets(), "ArchitectsDaughter.ttf"); 
	}

	/**
	 * @return size of categories list.
	 */
	@Override
	public int getCount() {
		return items.size();
	}

	/**
	 * @param position 
	 * @return category at position.
	 */
	@Override
	public String getItem(int position) {
		return items.get(position);
	}


	/**
	 * @return id of selected item.
	 */
	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * @param position of item in list
	 * @param covertView view to be converted
	 * @return recycled view
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		//store category name at the position in a string.
		String category = items.get(position);

		//set textview text to category name
	    TextView tv = new TextView(context);
	    tv.setText(category);
	    
	    //set textview font to custom font
	    tv.setTypeface(font);
	    
	    //set font style
	    tv.setTextSize(FONT_SIZE_BIG);
	    tv.setPadding(5, 5, 5, 5);
	    tv.setTextColor(Color.parseColor("#ffffff"));
	    return tv;
	}


}

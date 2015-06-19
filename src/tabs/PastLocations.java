import java.util.List;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

/**
 * A simple {@link android.support.v4.app.Fragment} subclass.
 * 
 */
public class HistoryMark extends Fragment implements OnItemClickListener{

	private List<SQLiteLocation> historyList;
	private ListView historyListView;
	
	public HistoryMark() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_history_mark, container,
				false);

		historyList = Menu.db.getAllLocations();
		historyListView = (ListView)rootView.findViewById(android.R.id.list);
		historyListView.setOnItemClickListener(this);
		
		ArrayAdapter<SQLiteLocation> adapter = new HistoryAdapter();
		
		historyListView.setAdapter(adapter);
		TextView noHistory = (TextView) rootView.findViewById(R.id.noHistory);
		if(historyList.size() < 1){
			historyListView.setVisibility(View.GONE);
			noHistory.setVisibility(View.VISIBLE);
		}
		else{
			noHistory.setVisibility(View.GONE);
			historyListView.setVisibility(View.VISIBLE);
		}
		return rootView;
	}
	
	private class HistoryAdapter extends ArrayAdapter<SQLiteLocation>{
		public HistoryAdapter(){
			super(getActivity(), R.layout.item_view, historyList);
			
		}
	
		public View getView(int position, View convertView, ViewGroup parent){
			View itemView = convertView;
			if(itemView == null){
				itemView = getActivity().getLayoutInflater().inflate(
						R.layout.item_view, parent, false);
			}
			SQLiteLocation currentPast = historyList.get(historyList.size()-position-1);
		
			TextView address = (TextView) itemView.findViewById(R.id.item_address);
			address.setText(currentPast.getAddress());
			
			TextView time = (TextView) itemView.findViewById(R.id.item_time);
			time.setText(currentPast.getTime());
			
			TextView date = (TextView) itemView.findViewById(R.id.item_date);
			date.setText(currentPast.getDate());
			
			ImageView note = (ImageView) itemView.findViewById(R.id.item_note);
			if(currentPast.getNote() == null || currentPast.getNote().equals(""))
				note.setImageBitmap(null);
			else
				note.setImageBitmap(BitmapFactory.decodeResource(getResources(),
						R.drawable.ic_action_view_as_list));
			
			ImageView image = (ImageView) itemView.findViewById(R.id.item_image);
			if(currentPast.getPicture().length < 1)
				image.setImageBitmap(null);
			else
				image.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.ic_action_picture));
			return itemView;
		}
	}
	public void onPause(){
		super.onPause();
	}
	public void onResume(){
		super.onResume();
		historyListView.setClickable(true);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent data = new Intent(getView().getContext(), Confirm.class);
		view.setBackgroundResource(R.drawable.revgradient);
		SQLiteLocation info = historyList.get(historyList.size()-position-1);		
		data.putExtra("id", info.getId());
		
		startActivity(data);
		historyListView.setClickable(false);
	}
}

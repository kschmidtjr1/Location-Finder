import java.util.ArrayList;

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
public class FavoritedLocations extends Fragment implements OnItemClickListener {

	private ArrayList<SQLiteLocation> favoriteList;
	private ListView favoriteListView;
	
	public FavoritedLocations() {
		// Required empty public constructor
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_favorite, container,
				false);
		
		favoriteList = Menu.favoriteList;
		favoriteListView = (ListView)rootView.findViewById(android.R.id.list);
		favoriteListView.setOnItemClickListener(this);

		ArrayAdapter<SQLiteLocation> adapter = new FavoriteAdapter();
		favoriteListView.setAdapter(adapter);
		TextView noFavorites = (TextView) rootView.findViewById(R.id.noFavorite);
		if(favoriteList.size() < 1) {
			favoriteListView.setVisibility(View.GONE);
			noFavorites.setVisibility(View.VISIBLE);
		}
		else{
			noFavorites.setVisibility(View.GONE);
			favoriteListView.setVisibility(View.VISIBLE);
		}
		return rootView;
	}
	
	private class FavoriteAdapter extends ArrayAdapter<SQLiteLocation>{
		public FavoriteAdapter(){
			super(getActivity(), R.layout.fav_item_view, favoriteList);
		}
		
		public View getView(int position, View convertView, ViewGroup parent){
			View itemView = convertView;
			if(itemView == null){
				itemView = getActivity().getLayoutInflater().inflate(
						R.layout.fav_item_view, parent, false);
			}
			SQLiteLocation currentPast = favoriteList.get(favoriteList.size()-position-1);

			TextView nick = (TextView) itemView.findViewById(R.id.fav_nick);
			nick.setText(currentPast.getName());
			
			TextView address = (TextView) itemView.findViewById(R.id.fav_address);
			address.setText(currentPast.getAddress());
			
			ImageView note = (ImageView) itemView.findViewById(R.id.fav_note);
			if(currentPast.getNote() == null || currentPast.getNote().equalsIgnoreCase(""))
				note.setImageBitmap(null);
			else
				note.setImageBitmap(BitmapFactory.decodeResource(
						getResources(), R.drawable.ic_action_view_as_list));
			
			ImageView image = (ImageView) itemView.findViewById(R.id.fav_image);
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
		favoriteListView.setClickable(true);
	}
	
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Intent data = new Intent(getView().getContext(), Confirm.class);
		view.setBackgroundResource(R.drawable.revgradient);
		SQLiteLocation info = favoriteList.get(favoriteList.size()-position-1);
		data.putExtra("favPos", info.getPos());
		
		startActivity(data);
		favoriteListView.setClickable(false);
	}
}

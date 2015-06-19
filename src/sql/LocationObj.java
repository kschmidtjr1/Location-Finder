public class SQLiteLocation {
	private int _id;		//0
	private double _lat;	//1
	private double _lng;	//2
	private String _address;//3
	private String _date;	//4
	private String _time;	//5
	private String _name;	//6
	private String _note;	//7
	private byte[] _picture;//8 blob
	private boolean _favorite;//9 boolean
	
	public SQLiteLocation(){}
	
	public SQLiteLocation(int id, double lat, double lng, 
			String address, String date, String time, 
			String name, String note, byte[] picture,
			boolean favorite){
		this._id = id;
		this._lat = lat;
		this._lng = lng;
		this._address = address;
		this._date = date;
		this._time = time;
		this._name = name;
		this._note = note;
		this._picture = picture;
		this._favorite = favorite;
	}
	
	public SQLiteLocation(SQLiteLocation pastLoc){
		this._lat = pastLoc.getLat();
		this._lng = pastLoc.getLng();
		this._address = pastLoc.getAddress();
		this._date = pastLoc.getDate();
		this._time = pastLoc.getTime();
		this._name = pastLoc.getName();
		this._picture = pastLoc.getPicture();
		this._note = pastLoc.getNote();
		this._favorite = pastLoc.isFavorite();
	}

	public int getId() {
		return _id;
	}

	public void setId(int id) {
		this._id = id;
	}
	
	public double getLat() {
		return _lat;
	}

	public void setStringLat(String lat) {
		this._lat = Double.parseDouble(lat);
	}
	
	public void setDoubleLat(double lat) {
		this._lat = lat;
	}

	public double getLng() {
		return _lng;
	}

	public void setStringLng(String lng) {
		this._lng = Double.parseDouble(lng);
	}
	
	public void setDoubleLng(double lng) {
		this._lng = lng;
	}

	public String getAddress() {
		return _address;
	}

	public void setAddress(String address) {
		this._address = address;
	}

	public String getDate() {
		return _date;
	}

	public void setDate(String date) {
		this._date = date;
	}

	public String getTime() {
		return _time;
	}

	public void setTime(String time) {
		this._time = time;
	}

	public String getName() {
		return _name;
	}

	public void setName(String name) {
		this._name = name;
	}

	public byte[] getPicture() {
		return _picture;
	}

	public void setPicture(byte[] picture) {
		this._picture = picture;
	}

	public String getNote() {
		return _note;
	}

	public void setNote(String note) {
		this._note = note;
	}

	public boolean isFavorite() {
		return _favorite;
	}

	public void setFavorite(boolean favorite) {
		this._favorite = favorite;
	}
	
}

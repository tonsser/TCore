package dk.nodes.tasks;

import android.os.AsyncTask;

public abstract class NAsync extends AsyncTask<String, Void, Integer> {

	protected abstract void onPreExecute();
	protected abstract Integer doInBackground(String... params);
	protected abstract void onPostExecute(Integer code);
	protected abstract void onCancelled();
	protected abstract void onProgressUpdate(Void... values);
}

package dk.tonsser.webservice;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import dk.tonsser.utils.TLog;
import dk.tonsser.utils.TUtils;

/**
 * @author Casper Rasmussen - 2013
 */
public class TQueryBuilder {
    private static String TAG = TQueryBuilder.class.getName();


    /**
     * This method will return a query for a http request of the inputted Map<String,String>
     *
     * @param inputQuery
     * @return String
     * @throws Exception
     */
    public static String mapToQuery(Map<String, String> inputQuery) throws Exception {
        String output = "";
        if (inputQuery != null && inputQuery.size() > 0) {
            output += "?";
            Iterator<Entry<String, String>> it = inputQuery.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, String> pairs = it.next();
                output += pairs.getKey() + "=" + URLEncoder.encode(pairs.getValue(), "UTF-8");
                if (it.hasNext())
                    output += "&";
            }
        } else {
            TLog.e(TAG + " mapToString", "Map/JSON input is either null or empty");
        }
        return output;
    }

    /**
     * Will return a query for a http request of the inputted JSONObject, all values should be added in 1 layer
     *
     * @param inputJSON
     * @return String
     * @throws Exception
     */
    public static String jsonToQuery(JSONObject inputJSON) throws Exception {
        return mapToQuery(TUtils.jsonToHashMap(inputJSON));
    }

    /**
     * Will return a query for a http request with inputted key/value
     *
     * @param key
     * @param value
     * @return
     * @throws Exception
     */
    public static String pairToQuery(String key, String value) throws Exception {
        JSONObject queryJson = new JSONObject();
        queryJson.put(key, value);
        return jsonToQuery(queryJson);
    }
}

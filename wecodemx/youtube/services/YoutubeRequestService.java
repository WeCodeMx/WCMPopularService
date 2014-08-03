package youtube.services;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import youtube.enums.YoutubeKey;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * --------------------------------------------------------
 * Created by Carlos Bedoy on 2/08/14.
 * CBYoutubeApi
 * Mobile Developer
 * Aguascalientes Mexico
 * Email:       carlos.bedoy@gmail.com
 * Facebook:    https://www.facebook.com/carlos.bedoy
 * ---------CODE && MUSIC ----------------------------------
 */
public class YoutubeRequestService{

    private URLConnection               urlConnection;
    private BufferedReader              bufferedReader;
    private String                      response;
    private URL                         url;
    private JSONParser                  jsonParser;
    private Object                      objectParsed;
    private JSONObject                  jsonObject;
    private static YoutubeRequestService instance;
    private List<Object>                dataModel;
    private String                      updated;
    public static YoutubeRequestService getInstance()
    {
        if(instance == null)
            instance = new YoutubeRequestService();
        return instance;
    }

    private YoutubeRequestService()
    {
        this.jsonParser                 = new JSONParser();
        this.dataModel                  = new ArrayList<Object>();
    }


    private void executeRequest(String requestLink){
        try {
            url                     = new URL(requestLink);
            urlConnection           = url.openConnection();
            bufferedReader          = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            response                = bufferedReader.readLine();
            objectParsed            = jsonParser.parse(response);
            jsonObject              = (JSONObject)objectParsed;
            dataModel.clear();
            parseData(jsonObject);
        } catch (MalformedURLException ex) {

        } catch (IOException ex) {

        } catch (org.json.simple.parser.ParseException e) {

        }

    }

    public void executeWithValue(String value)
    {
        executeRequest(createUrl(validateValue(value)));
    }

    private String createUrl(String valueFormatted)
    {
        return new String("https://gdata.youtube.com/feeds/api/videos?q="+valueFormatted+"&max-results=50&v=2&alt=jsonc");
    }

    private String validateValue(String value)
    {
        return value.contains(" ")?value.replace(" ", "%20"):value;
    }

    private void parseData(JSONObject requestObject){
        this.jsonObject                         = (JSONObject) requestObject.get("data");
        this.updated                            = (String) jsonObject.get("updated");
        JSONArray jsonArray                     = (JSONArray) jsonObject.get("items");
        HashMap<YoutubeKey, Object> information = null;
        for(Object object : jsonArray)
        {
            if(object!=null)
            {
                information                                 = new HashMap<YoutubeKey, Object>();
                JSONObject currentItem                      = (JSONObject) object;
                JSONObject player                           = (JSONObject) currentItem.get("player");
                JSONObject thumbnail                        = (JSONObject) currentItem.get("thumbnail");
                information.put(YoutubeKey.RATING,          currentItem.get("rating"));
                information.put(YoutubeKey.DESCRIPTION,     currentItem.get("description"));
                information.put(YoutubeKey.ASPECT_RATIO,    currentItem.get("aspectRatio"));
                information.put(YoutubeKey.LIKE_COUNT,      currentItem.get("likeCount"));
                information.put(YoutubeKey.TITLE,           currentItem.get("title"));
                information.put(YoutubeKey.RATING_COUNT,    currentItem.get("ratingCount"));
                information.put(YoutubeKey.COMMENT_COUNT,   currentItem.get("commentCount"));
                information.put(YoutubeKey.DURATION,        currentItem.get("duration"));
                information.put(YoutubeKey.UPLOADER,        currentItem.get("uploader"));
                information.put(YoutubeKey.UPLOADED,        currentItem.get("uploaded"));
                information.put(YoutubeKey.FAVORITE_COUNT,  currentItem.get("favoriteCount"));
                information.put(YoutubeKey.ID,              currentItem.get("id"));
                information.put(YoutubeKey.VIEW_COUNT,      currentItem.get("viewCount"));
                information.put(YoutubeKey.THUMBNAIL_HQ,    thumbnail.get("hqDefault"));
                information.put(YoutubeKey.THUMBNAIL_HQ,    thumbnail.get("sqDefault"));
                information.put(YoutubeKey.PLAYER_DEFAULT,  player.get("default"));
                information.put(YoutubeKey.PLAYER_MOBILE,   player.get("mobile"));
                dataModel.add(information);
            }
        }
    }

    public List<Object> getDataModel()
    {
        return this.dataModel;
    }

}
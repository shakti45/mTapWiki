package com.mTapWiki.shaktis.wikipedia.config;

public class Config {
     static String url;
     static String latlng;

    public static String getLatLng() {
        return latlng;
    }

    public static void setLatLng(String lat) {
        Config.latlng = lat;
    }



    public static void setUrl(String type, String query) {
        switch (type){
            case "query":
                //do something
                Config.url = "https://en.wikipedia.org/w/api.php?" +
                        "format=json" +
                        "&action=query" +
                        "&generator=search" +
                        "&gsrnamespace=0" +
                        "&gsrsearch="+query +
                        "&srlimit=20" +
                        "&prop=pageimages|extracts" +
                        "&pilimit=max" +
                        "&exintro" +
                        "&explaintext" +
                        "&exsentences=1" +
                        "&exlimit=max";
                break;
            default :
                Config.url = "https://en.wikipedia.org/w/api.php?" +
                        "action=query" +
                        "&prop=coordinates|pageimages|pageterms|extracts" +
                        "&colimit=50" +
                        "&piprop=thumbnail" +
                        "&pithumbsize=144" +
                        "&pilimit=50" +
                        "&wbptterms=description" +
                        "&generator=geosearch" +
                        "&ggscoord="+(query=((query!=null)?query:"17.426870|78.414966") )+
                        "&ggsradius=10000" +
                        "&ggslimit=50" +
                        "&explaintext" +
                        "&format=json" +
                        "&exsentences=1"+
                        "&exintro";

        }
    }

    public static String getUrl() {
        return url;
    }
}

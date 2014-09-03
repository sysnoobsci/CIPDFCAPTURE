package com.ameraz.android.cipdfcapture.app;

/**
 * Created by adrian.meraz on 6/24/2014.
 */
import android.util.Log;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
/**
 * Created by adrian.meraz on 5/16/2014.
 */
public class XmlParser {
    private Boolean is_xml = false;
    private static String xmlVals;
    private static String xmlResponse;
    StringBuilder total = new StringBuilder();

    private final static String EMPTY_STRING = "";

    ArrayList<String> textTag = new ArrayList<String>();

    public String getxmlVals() {
        return xmlVals;
    }

    public void setxmlVals(String xmlstring) {
        this.xmlVals = xmlstring;
    }

    public static String getXmlResponse() {
        return xmlResponse;
    }

    public static void setXmlResponse(String xmlResponse) {
        XmlParser.xmlResponse = xmlResponse;
    }

    public Boolean getIs_xml() {
        return is_xml;
    }

    public void setIs_xml(Boolean is_xml) {
        this.is_xml = is_xml;
    }

    public ArrayList<String> getTextTag() {
        return textTag;
    }

    public void setTextTag(ArrayList<String> textTag) {
        this.textTag = textTag;
    }

    public XmlParser(String xmlResponse) throws IOException, XmlPullParserException {
        setXmlResponse(xmlResponse);
        parseXMLfunc();//start parsing when the object is created
    }

    public String parseXMLfunc() throws XmlPullParserException, IOException{
        String xresp = getXmlResponse();
        Log.d("Variable","parseXMLfunc() xmlVals value: " + xresp);
        clearXMLString();//clear the String before adding a new XMLString
        clearXMLTags();
        ArrayList<String> listOfTextTags = new ArrayList<String>();//a list contain all the text inside XML tags
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput( new StringReader (xmlResponse) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.TEXT) {
                listOfTextTags.add(xpp.getText());
                total.append(xpp.getText() + ",");
            }
            eventType = xpp.next();
        }
        setTextTag(listOfTextTags);
        setxmlVals(total.toString());
        Log.d("XML", "Contents of XML Response: " + getxmlVals());
        return total.toString();//return parsed contents of XML
    }

    public void clearXMLString(){
        setxmlVals(EMPTY_STRING);
    }

    public void clearXMLTags(){
        textTag.clear();
    }

    public String findTagText(String tag) throws XmlPullParserException, IOException {//pass in a tag, and get the tag contents
        if (tag.equals("")) {//if nothing is being searched for, return all the xml results
            Log.d("Variable", "No tag being searched for.");
            return getxmlVals();
        }
        String xmlstring = getXmlResponse();
        Log.d("SearchTag", "Value of tag: " + tag);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput(new StringReader(xmlstring));//get the XML string that was created from parsing the query response
        int eventType = xpp.getEventType();
        StringBuilder tagText = new StringBuilder();
        String matcher = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG) {
                //Log.d("xpp", "xpp.getName() value: " + xpp.getName());
                matcher = xpp.getName();
            }
            else if (eventType == XmlPullParser.TEXT) {
                //Log.d("matcher", "matcher value: " + matcher);
                if (matcher.equals(tag)) {//if the tag name matches what you're searching for, append the contents
                    //Log.d("xpp","xpp.getText() value: " + xpp.getText());
                    tagText.append(xpp.getText()).append(",");
                    matcher = "";//clear out the String again
                }
            }
            eventType = xpp.next();
        }
        return tagText.toString();
    }

    protected Boolean isXMLformat(String xmlstring){
        String str2 = "<?xml version=";
        setIs_xml(xmlstring.toLowerCase().contains(str2.toLowerCase()));
        return getIs_xml();
    }

}
package com.ameraz.android.cipdfcapture.app;

/**
 * Created by adrian.meraz on 6/24/2014.
 */
import android.util.Log;
import android.util.Xml;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;
/**
 * Created by adrian.meraz on 5/16/2014.
 */
public class XmlParser {
    private Boolean is_xml = false;
    private String xmlstring = "";

    private final static String EMPTY_STRING = "";

    ArrayList<String> textTag = new ArrayList<String>();

    public String getXmlstring() {
        return xmlstring;
    }

    public void setXmlstring(String xmlstring) {
        this.xmlstring = xmlstring;
    }




















    StringBuilder total = new StringBuilder();

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

    public String parseXMLfunc(String xmlstring)
            throws XmlPullParserException, IOException{
        clearXMLString();//clear the String before adding a new XMLString
        clearXMLTags();
        ArrayList<String> listOfTextTags = new ArrayList<String>();//a list contain all the text inside XML tags
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();

        xpp.setInput( new StringReader ( xmlstring ) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_DOCUMENT) {
                //total.append("Start document\n");
            } else if(eventType == XmlPullParser.START_TAG) {

                //total.append("Start tag "+xpp.getName()+"\n");
            } else if(eventType == XmlPullParser.END_TAG) {
                //total.append("End tag "+xpp.getName()+"\n");
            } else if(eventType == XmlPullParser.TEXT) {
                listOfTextTags.add(xpp.getText());
                total.append(xpp.getText() + ",");
                //total.append("Text "+xpp.getText()+"\n");
            }
            eventType = xpp.next();
        }
        //total.append("End document\n");
        setTextTag(listOfTextTags);
        setXmlstring(total.toString());
        Log.d("XML", "Contents of XML Response: " + getXmlstring());
        return total.toString();//return parsed contents of XML
    }

    public void clearXMLString(){
        setXmlstring(EMPTY_STRING);
    }

    public void clearXMLTags(){
        textTag.clear();
    }

    public String findTagText(String tag,String xmlstring) throws XmlPullParserException, IOException {//pass in a tag, and get the tag contents
        if(tag.equals("")){//if nothing is being searched for, return all the xml results
            Log.d("Variable","No tag being searched for.");
            return getXmlstring();
        }
        Log.d("SearchTag","Value of tag: " + tag);
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput( new StringReader ( xmlstring ) );//get the XML string that was created from parsing the query response
        int eventType = xpp.getEventType();
        StringBuilder tagText = new StringBuilder();
        String matcher = "";
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.START_TAG) {
                //Log.d("xpp", "xpp.getName() value: " + xpp.getName());
                matcher = xpp.getName();
            }
            else if(eventType == XmlPullParser.TEXT) {
                //Log.d("matcher", "matcher value: " + matcher);
                if(matcher.equals(tag)){//if the tag name matches what you're searching for, append the contents
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

    public Boolean goodRC(String xml){
        if(xml.contains("<rc>0</rc><xrc>0</xrc><xsrc>0</xsrc>")){
            Log.d("Message","XML contains good return codes...");
            return true;
        }
        else{
            Log.d("Message","XML contains bad return codes...");
            return false;
        }
    }

}
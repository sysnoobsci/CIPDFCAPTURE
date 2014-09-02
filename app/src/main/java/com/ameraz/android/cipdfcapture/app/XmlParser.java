package com.ameraz.android.cipdfcapture.app;

/**
 * Created by adrian.meraz on 6/24/2014.
 */
import android.util.Log;
import android.util.Xml;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
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
    private static String xmlstring = "";

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
        Log.d("Variable","parseXMLfunc() xmlstring value: " + xmlstring);
        clearXMLString();//clear the String before adding a new XMLString
        clearXMLTags();
        ArrayList<String> listOfTextTags = new ArrayList<String>();//a list contain all the text inside XML tags
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();
        xpp.setInput( new StringReader ( xmlstring ) );
        int eventType = xpp.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if(eventType == XmlPullParser.TEXT) {
                listOfTextTags.add(xpp.getText());
                total.append(xpp.getText() + ",");
            }
            eventType = xpp.next();
        }
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
        StringBuilder tagcontents = new StringBuilder();
        StringBuffer line = new StringBuffer();
        String startTag = "<" + tag + ">";
        String endTag = "<" + tag + "/>";
        int start = xmlstring.indexOf(startTag) + startTag.length()-1;
        int end = xmlstring.indexOf(endTag);
        //InputStream stream = new ByteArrayInputStream(xmlstring.getBytes(StandardCharsets.UTF_8));
        InputStream stream = new ByteArrayInputStream(xmlstring.getBytes());
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        while(line.append(reader.readLine()) != null){
            tagcontents.append(xmlstring.substring(start,end) + ",");
        }
        Log.d("Variable","tagcontents.toString(): " + tagcontents.toString());
        return tagcontents.toString();
    }

    protected Boolean isXMLformat(String xmlstring){
        String str2 = "<?xml version=";
        setIs_xml(xmlstring.toLowerCase().contains(str2.toLowerCase()));
        return getIs_xml();
    }

}
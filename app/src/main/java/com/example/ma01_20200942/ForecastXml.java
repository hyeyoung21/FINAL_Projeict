package com.example.ma01_20200942;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class ForecastXml {

    //    parsing 대상인 tag를 상수로 선언
    private final static String FAULT_RESULT = "faultResult";
    private final static String TIME = "fcstTime";
    private final static String CATEGORY = "category";
    private final static String VALUE = "fcstValue";


    private XmlPullParser parser;

    public ForecastXml() {
//        xml 파서 관련 변수들은 필요에 따라 멤버변수로 선언 후 생성자에서 초기화
//        파서 준비
        XmlPullParserFactory factory = null;

//        파서 생성
        try {
            factory = XmlPullParserFactory.newInstance();
            parser = factory.newPullParser();
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }

    }

    public List<ForecastItem> parse(String xml) {
        List<ForecastItem> resultList = new ArrayList();
        for (int i = 0; i < 6; i++)
            resultList.add(new ForecastItem());

        try {
            parser.setInput(new StringReader(xml));
            int eventType = parser.getEventType();      // 태그 유형 구분 변수 준비

            int i = 0;
            String tagType = "";
            String value;
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    switch (parser.getName())
                    {
                        case TIME:
                            parser.next();
                            value = parser.getText();
                            resultList.get(i).setTime(value);
                            break;
                        case CATEGORY:
                            parser.next();
                            value = parser.getText();
                            tagType = value;
                            break;
                        case VALUE:
                            parser.next();
                            value = parser.getText();
                            if(tagType.equals("RN1")) {
                                resultList.get(i).setRain(value);
                            } else if (tagType.equals("T1H"))
                                resultList.get(i).setTemp(value);
                            else if (tagType.equals("REH"))
                                resultList.get(i).setReh(value);
                            i++;
                            break;
                    }
                }
                eventType = parser.next();
                if (i > 5 ) i = 0;
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultList;
    }
}

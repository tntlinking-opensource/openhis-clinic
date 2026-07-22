package com.geeke.hosdata.config;

import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.InputStream;

// 读取 XML 配置文件，解析字段映射，并将其应用到代码中
public class FieldMappingConfig {
    private static final Logger logger = LoggerFactory.getLogger(FieldMappingConfig.class);
    public static JSONObject readFieldMappingFromXML() {
        JSONObject fieldMapping = new JSONObject();
        String filePath = "HisDataCollect.fxml";
        try (InputStream inputStream = FieldMappingConfig.class.getClassLoader().getResourceAsStream(filePath)) {
            if (inputStream != null) {
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(inputStream);
                doc.getDocumentElement().normalize();
                NodeList mappingList = doc.getElementsByTagName("mapping");
                for (int i = 0; i < mappingList.getLength(); i++) {
                    Element mappingElement = (Element) mappingList.item(i);
                    String oldFieldName = mappingElement.getElementsByTagName("oldFieldName").item(0).getTextContent();
                    String newFieldName = mappingElement.getElementsByTagName("newFieldName").item(0).getTextContent();
                    fieldMapping.put(oldFieldName, newFieldName);
                }
            }
        } catch (Exception e) {
            logger.error("读取字段映射配置失败", e);
        }
        return fieldMapping;
    }




//    public static void main(String[] args) {
//        String filePath = "back/src/main/resources/HisDataCollect.fxml";
//        JSONObject fieldMapping = readFieldMappingFromXML(filePath);
//    }
}

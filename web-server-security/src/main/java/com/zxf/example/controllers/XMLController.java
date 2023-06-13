package com.zxf.example.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;

@RestController
@RequestMapping("/xml")
public class XMLController {
    @PostMapping("/DocumentBuilder/security-1")
    public String securityDocumentBuilder1(@RequestBody String xml) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("XMLController::securityDocumentBuilder1" + xml);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        dbf.setXIncludeAware(false);
        return getContent(dbf, xml);
    }

    @PostMapping("/DocumentBuilder/security-2")
    public String securityDocumentBuilder2(@RequestBody String xml) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("XMLController::securityDocumentBuilder2" + xml);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        dbf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
        dbf.setXIncludeAware(false);
        dbf.setExpandEntityReferences(false);
        return getContent(dbf, xml);
    }

    @PostMapping("/DocumentBuilder/un-security")
    public String unSecurityDocumentBuilder(@RequestBody String xml) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("XMLController::unSecurityDocumentBuilder" + xml);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        return getContent(dbf, xml);
    }

    private String getContent(DocumentBuilderFactory dbf, String xml) throws ParserConfigurationException, IOException, SAXException {
        Document document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
        Element element = document.getDocumentElement();
        return element.getFirstChild().getTextContent();
    }
}

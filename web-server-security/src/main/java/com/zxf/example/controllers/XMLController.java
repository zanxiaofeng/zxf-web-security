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
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

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

    @PostMapping("/XMLInputFactory/security-1")
    public String securityXMLInputFactory1(@RequestBody String xml) throws XMLStreamException {
        System.out.println("XMLController::securityXMLInputFactory1" + xml);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        return getContent(xmlInputFactory, xml);
    }

    @PostMapping("/XMLInputFactory/security-2")
    public String securityXMLInputFactory2(@RequestBody String xml) throws XMLStreamException {
        System.out.println("XMLController::securityXMLInputFactory2" + xml);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        return getContent(xmlInputFactory, xml);
    }

    @PostMapping("/XMLInputFactory/un-security")
    public String unSecurityXMLInputFactory(@RequestBody String xml) throws XMLStreamException {
        System.out.println("XMLController::unSecurityXMLInputFactory" + xml);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        return getContent(xmlInputFactory, xml);
    }

    private String getContent(DocumentBuilderFactory dbf, String xml) throws ParserConfigurationException, IOException, SAXException {
        Document document = dbf.newDocumentBuilder().parse(new ByteArrayInputStream(xml.getBytes()));
        Element root = document.getDocumentElement();
        return root.getFirstChild().getTextContent();
    }

    private String getContent(XMLInputFactory xmlInputFactory, String xml) throws XMLStreamException {
        XMLStreamReader xmlStreamReader = xmlInputFactory.createXMLStreamReader(new StringReader(xml));
        while (xmlStreamReader.hasNext()) {
            int event = xmlStreamReader.next();
            if (event == XMLStreamConstants.START_ELEMENT) {
                return xmlStreamReader.getElementText();
            }
        }
        return null;
    }
}

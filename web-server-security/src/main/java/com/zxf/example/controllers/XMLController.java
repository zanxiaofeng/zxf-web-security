package com.zxf.example.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

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
    @PostMapping("/DocumentBuilder/security")
    public String securityDocumentBuilder1(@RequestBody String xml) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("XMLController::securityDocumentBuilder" + xml);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            dbf.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException("ParserConfigurationException was thrown. The feature 'disallow-doctype-decl' is not supported by your XML processor.", e);
        }
        return getContent(dbf, xml);
    }

    @PostMapping("/DocumentBuilder/un-security")
    public String unSecurityDocumentBuilder(@RequestBody String xml) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("XMLController::unSecurityDocumentBuilder" + xml);
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        return getContent(dbf, xml);
    }

    @PostMapping("/XMLInputFactory/security")
    public String securityXMLInputFactory2(@RequestBody String xml) throws XMLStreamException {
        System.out.println("XMLController::securityXMLInputFactory" + xml);
        XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
        xmlInputFactory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
        xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
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

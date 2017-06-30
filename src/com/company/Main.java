package com.company;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
	// write your code here

        try {
            URL u = new URL("http://www.ncbi.nlm.nih.gov/pubmed/?term=25207815&report=xml&format=text");
            InputStream in = u.openStream();
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

            dbFactory.setIgnoringComments(true);
            dbFactory.setIgnoringElementContentWhitespace(true);
            dbFactory.setValidating(false);

            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(in);

            //Test
          //  File fXmlFile = new File("pubmed.xml");
         //   Document doc = dBuilder.parse(fXmlFile);



            System.out.println("?");
            doc.getDocumentElement().normalize();
            System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

            NodeList nList = doc.getElementsByTagName("MeshHeading");

            for (int temp = 0; temp < nList.getLength(); temp++) {
                Node nNode = nList.item(temp);
                System.out.println("\nCurrent Element :" + nNode.getNodeName());
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

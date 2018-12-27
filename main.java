package dom_avec_xpath;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;

import dom_avec_xpath.SimpleErrorHandler;

public class main {

	// DOM avec XPath !
	public static void main(String[] args) throws SAXException, IOException, XPathExpressionException {
		String xmlStr = "<?xml version=\"1.0\" encoding=\"UTF-8\">";
		System.out.println(xmlStr);
		String doctype = "<!DOCTYPE nantais SYSTEM \"ex.dtd\">";
		System.out.println(doctype);
		String baliseNantais = "<nantais>";
		System.out.println(baliseNantais);
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		NodeList nodeList;
		NodeList nodeListMandats;
		// on souhaite ignorer les �l�ments textes
		factory.setIgnoringElementContentWhitespace(true);
		factory.setIgnoringComments(true);
		
		try {
			factory.setValidating(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			ErrorHandler errHandler = new SimpleErrorHandler();
			builder.setErrorHandler(errHandler);
			
			XPathFactory xpf = XPathFactory.newInstance();
	        XPath path = xpf.newXPath();
	        
			
			// chargement du fichier historique.xml
			Document document = builder.parse(new File("historique.xml"));
			Node racine = document.getDocumentElement();
			
			Element root = document.getDocumentElement();
			//System.out.println("<" + racine.getNodeName() + ">"); // OK !
			
			XPath xPath = XPathFactory.newInstance().newXPath();
			String expression = "/export/acteurs/acteur[./etatCivil/infoNaissance/villeNais = 'Nantes' and ./mandats/mandat/infosQualite/codeQualite = 'Pr�sident']"; // uid ?
			nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			Element element = null;
			for(int i=0; i<nodeList.getLength(); i++) {
				element = (Element) nodeList.item(i);
				//System.out.println("<personne nom=" + element.getFirstChild().getNextSibling().getFirstChild().getFirstChild().getNextSibling().getTextContent());
				Node uid = element.getFirstChild();
				Node etatCivil = uid.getNextSibling();
				Node civ = etatCivil.getFirstChild().getFirstChild();
				String prenom = civ.getNextSibling().getTextContent();
				System.out.println("<personne nom='" + civ.getNextSibling().getTextContent() + " " + civ.getNextSibling().getNextSibling().getTextContent() + "'>");
				
				// parcours des diff�rents mandats
				//String expressionMandats = "/mandats";
				expression += "/mandats";
				nodeListMandats = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
				Element elementMandats = null;
				for(int j = 0; j < nodeListMandats.getLength(); j++) {
					elementMandats = (Element) nodeListMandats.item(j);
					Node uidMandat = elementMandats.getFirstChild().getFirstChild().getNextSibling();
					System.out.println(uidMandat.getTextContent());
				}
			}
			
			
			
			System.out.println("</nantais>");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
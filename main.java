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
		String expressionMandats;
		// on souhaite ignorer les éléments textes
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
			String expression = "/export/acteurs/acteur[./etatCivil/infoNaissance/villeNais = 'Nantes' and ./mandats/mandat/infosQualite/codeQualite = 'Président']"; // uid ?
			nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			Element element = null;
			for(int i=0; i<nodeList.getLength(); i++) {
				element = (Element) nodeList.item(i);
				//System.out.println("<personne nom=" + element.getFirstChild().getNextSibling().getFirstChild().getFirstChild().getNextSibling().getTextContent());
				Node uid = element.getFirstChild();
				Node etatCivil = uid.getNextSibling();
				Node civ = etatCivil.getFirstChild().getFirstChild();
				Node mandatsNode = element.getLastChild();
				String prenom = civ.getNextSibling().getTextContent();
				String uidActeur = uid.getTextContent(); // correspond à l'identifiant de l'acteur nantais président
				System.out.println("<personne nom='" + civ.getNextSibling().getTextContent() + " " + civ.getNextSibling().getNextSibling().getTextContent() + "'>");
				//System.out.println(uidActeur);
				
				// parcours des différents mandats
				//expressionMandats = expression + "/mandats";
				//System.out.println(expressionMandats);
				//expressionMandats = "/export/acteurs/acteur[./uid = " + uidActeur + " ]"; // on sélectionne l'ensemble des mandats de l'acteur
				// uid doit correspondre à un acteur nantais président + parcours de l'ensemble de ces mandats en tant que président !
				//nodeListMandats = (NodeList) xPath.compile(expressionMandats).evaluate(document, XPathConstants.NODESET);
				Element elementMandats = null;
				for(int j = 0; j < ((NodeList) mandatsNode).getLength(); j++) {
					elementMandats = (Element) ((NodeList) mandatsNode).item(j);
					Node uidMandat = mandatsNode.getFirstChild().getFirstChild().getNextSibling();
					//System.out.println(uidMandat.getTextContent());
					System.out.println(j);
				}
			}
			
			
			
			System.out.println("</nantais>");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
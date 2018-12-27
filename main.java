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
			String expression = "/export/acteurs/acteur[etatCivil/infoNaissance/villeNais = 'Nantes' and mandats/mandat/infosQualite/codeQualite = 'Pr�sident']"; // uid ?
			//nodeList = (NodeList) xPath.compile(expression).evaluate(document, XPathConstants.NODESET);
			nodeList = (NodeList) path.evaluate(expression, root, XPathConstants.NODESET);
			Element element = null;
			for(int i=0; i<nodeList.getLength(); i++) {
				element = (Element) nodeList.item(i);
				//System.out.println("<personne nom=" + element.getFirstChild().getNextSibling().getFirstChild().getFirstChild().getNextSibling().getTextContent());
				Node uid = element.getFirstChild();
				Node etatCivil = uid.getNextSibling();
				Node civ = etatCivil.getFirstChild().getFirstChild();
				Node mandatsNode = element.getLastChild();
				String prenom = civ.getNextSibling().getTextContent();
				String uidActeur = uid.getTextContent(); // correspond � l'identifiant de l'acteur nantais pr�sident
				System.out.println("<personne nom='" + civ.getNextSibling().getTextContent() + " " + civ.getNextSibling().getNextSibling().getTextContent() + "'>");
				//System.out.println(uidActeur);
				
				expressionMandats = "mandats/mandat[./infosQualite/codeQualite = 'Pr�sident']";
				nodeListMandats = (NodeList) path.evaluate(expressionMandats, nodeList.item(i), XPathConstants.NODESET);
				Element elementMandats = null;
				
				
				//System.out.println(nodeListMandats.getLength());
				
				// parcours des diff�rents mandats
				//expressionMandats = expression + "/mandats";
				//System.out.println(expressionMandats);
				//expressionMandats = "/export/acteurs/acteur[./uid = " + uidActeur + " ]"; // on s�lectionne l'ensemble des mandats de l'acteur
				// uid doit correspondre � un acteur nantais pr�sident + parcours de l'ensemble de ces mandats en tant que pr�sident !
				//nodeListMandats = (NodeList) xPath.compile(expressionMandats).evaluate(document, XPathConstants.NODESET);
				
				// parcours de l'ensemble des mandats de chaque acteur nantais qui a �t� pr�sident au moins une fois
				for(int j = 0; j < nodeListMandats.getLength(); j++) {
					//System.out.println("ICI");
					// s�lection des mandats de pr�sident -> if !!!
					elementMandats = (Element) nodeListMandats.item(j);
					Node uidMandat = elementMandats.getFirstChild();
					//System.out.println(uidMandat.getTextContent()); // OK !
					System.out.println("<md code=");
				}
			}
			
			
			
			System.out.println("</nantais>");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
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
		NodeList nodeListLibelle;
		String expressionMandats;
		String expressionLibelle;
		Node organeRef = null;
		Node dateDeb = null;
		Node numeroLegislature=null;
		Node dateFin=null;
		String fin = null;
		String pub = null;
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
				String uidActeur = uid.getTextContent(); // correspond à l'identifiant de l'acteur nantais président
				System.out.println("<personne nom='" + civ.getNextSibling().getTextContent() + " " + civ.getNextSibling().getNextSibling().getTextContent() + "'>");
				//System.out.println(uidActeur);
				
				expressionMandats = "mandats/mandat[./infosQualite/codeQualite = 'Président']";
				nodeListMandats = (NodeList) path.evaluate(expressionMandats, nodeList.item(i), XPathConstants.NODESET);
				Element elementMandats = null;
				
				// parcours de l'ensemble des mandats de chaque acteur nantais qui a été président au moins une fois
				for(int j = 0; j < nodeListMandats.getLength(); j++) {
					elementMandats = (Element) nodeListMandats.item(j);
					Node uidMandat = elementMandats.getFirstChild();
					
					
					NodeList contenuMandat = nodeListMandats.item(j).getChildNodes();
					int nbContenuMandat = contenuMandat.getLength();
					for (int c = 0; c < nbContenuMandat; c++) { // c = contenu
						// récupération de la date de publication
						if (contenuMandat.item(c).getNodeName().equals("datePublication")) {
							pub= contenuMandat.item(c).getTextContent();
						}
						
						// récupération de la date de fin
						if (contenuMandat.item(c).getNodeName().equals("dateFin")) {
							fin= contenuMandat.item(c).getTextContent();
						}
						
						// récupération de la legislature
						if (contenuMandat.item(c).getNodeName().equals("legislature")){
							Node legislature = contenuMandat.item(c);
							numeroLegislature = legislature.getFirstChild();
						}
						
						// récupération de la date de début
						if (contenuMandat.item(c).getNodeName().equals("dateDebut")){
							Node date = contenuMandat.item(c);
							dateDeb = date.getFirstChild();
						}
						
						// récupération du code
						if (contenuMandat.item(c).getNodeName().equals("organes")){
							Node organes = contenuMandat.item(c);
							organeRef = organes.getFirstChild();
						}	
					}
					String mdd = "<md code='" + organeRef.getTextContent() + "' début='" + dateDeb.getTextContent() + "' legislature='" + numeroLegislature.getTextContent() + "'";
					// on ajoute la date de fin si elle est présente
					if(fin != "") {
						mdd+=" fin='" + fin + "'";
					}
					// on ajoute la date de publication si elle est présente
					if(pub != "") {
						mdd+=" pub='" + pub + "'";
					}
					mdd+=">";
					System.out.println(mdd);
					
					// récupération du libellé grâce à l'uid
					//System.out.println("ICI : " + uidMandat.getTextContent()); // OK !
					//System.out.println(organeRef.getTextContent());
					expressionLibelle = "/export/organes/organe[./uid = '" + organeRef.getTextContent() + "']";
					//System.out.println(expressionLibelle);
					nodeListLibelle = (NodeList) path.evaluate(expressionLibelle, root, XPathConstants.NODESET);
					Element elementLibelle = null;
					//System.out.println(nodeListLibelle.getLength());
					for(int l = 0; l < nodeListLibelle.getLength(); l++) {
						elementLibelle = (Element) nodeListLibelle.item(l);
						Node libelle = elementLibelle.getFirstChild().getNextSibling().getNextSibling();
						String libelleMandat = libelle.getTextContent();
						System.out.println(libelleMandat);
						
					}
					//Node uidLibelle = elementLibelle.getFirstChild().getNextSibling().getNextSibling();
				}
			}
			System.out.println("</nantais>");
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
}
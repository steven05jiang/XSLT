package edu.neu.cs5200.xslt.dao;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import edu.neu.cs5200.xslt.model.Site;
import edu.neu.cs5200.xslt.model.SiteList;

public class SiteDAO {

	EntityManagerFactory factory = Persistence
			.createEntityManagerFactory("XSLT and JPA");
	EntityManager em = null;

	public Site findSite(int siteId) {
		Site site = null;

		em = factory.createEntityManager();
		em.getTransaction().begin();

		site = em.find(Site.class, siteId);

		em.getTransaction().commit();
		em.close();

		return site;
	}

	@SuppressWarnings("unchecked")
	public List<Site> findAllSites() {
		List<Site> sites = new ArrayList<Site>();

		em = factory.createEntityManager();
		em.getTransaction().begin();

		Query query = em.createQuery("select site from Site site");
		sites = query.getResultList();

		em.getTransaction().commit();
		em.close();

		return sites;
	}

	public void exportDirectorsToXmlFile(SiteList sitelist, String xmlFileName) {
		File xmlFile = new File(xmlFileName);
		try {
			JAXBContext jaxb = JAXBContext.newInstance(SiteList.class);
			Marshaller marshaller = jaxb.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			marshaller.marshal(sitelist, xmlFile);
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void convertXmlFileToOutputFile(String sitesXmlFileName,
			String outputFileName, String xsltFileName) {
		File inputXmlFile = new File(sitesXmlFileName);
		File outputXmlFile = new File(outputFileName);
		File xsltFile = new File(xsltFileName);

		StreamSource source = new StreamSource(inputXmlFile);
		StreamSource xslt = new StreamSource(xsltFile);
		StreamResult output = new StreamResult(outputXmlFile);

		TransformerFactory factory = TransformerFactory.newInstance();
		try {
			Transformer transformer = factory.newTransformer(xslt);
			transformer.transform(source, output);
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		SiteDAO dao = new SiteDAO();
		
		//find all sites
		List<Site> sites = dao.findAllSites();
		for(Site s : sites){
			System.out.println(s.getName());
		}
	
		//export sites to "xml/sites.xml"
		SiteList siteList = new SiteList();
		siteList.setSites(sites);
		dao.exportDirectorsToXmlFile(siteList, "xml/sites.xml");
		
		//convert "xml/sites.xml" to "xml/sites.html" 
		//dao.convertXmlFileToOutputFile("xml/sites.xml", "xml/sites.html", "xml/sites2html.xsl");
		dao.convertXmlFileToOutputFile("xml/sites.xml", "xml/equipments.html", "xml/sites2equipments.xsl");
	}
}

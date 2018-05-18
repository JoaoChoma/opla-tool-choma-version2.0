package arquitetura.io;

import java.io.File;
import java.io.IOException;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import arquitetura.helpers.XmiHelper;

/**
 * 
 * @author edipofederle<edipofederle@gmail.com>
 *
 */
public class SaveAndMove extends XmiHelper{
	
	 static Logger LOGGER = LogManager.getLogger(SaveAndMove.class.getName());
	
	public static void saveAndMove(Document docNotation, Document docUml, Document docDi, String originalModelName, String newModelName) throws TransformerException, IOException{
		String targetDir = ReaderConfig.getDirTarget();
		String targetDirExport = ReaderConfig.getDirExportTarget();
		
		String notationCopy = targetDir+originalModelName+".notation";
		String umlCopy = targetDir+originalModelName +".uml";
		String diCopy = targetDir+originalModelName +".di";
		
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		
		Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");


		//Necessario atualizar referencias pois nome do modelo pode ter mudado
		NodeList elements = docNotation.getElementsByTagName("element");
		for (int i = 0; i < elements.getLength(); i++) {
			String idXmi = getOnlyIdOfXmiAttribute(elements, i);
			if(idXmi !=null)
				elements.item(i).getAttributes().getNamedItem("href").setNodeValue(newModelName+".uml#"+idXmi);
		}
		
		NodeList elementsUml = docDi.getElementsByTagName("emfPageIdentifier");
		for (int i = 0; i < elementsUml.getLength(); i++) {
			String idXmi = getOnlyIdOfXmiAttribute(elementsUml, i);
			elementsUml.item(i).getAttributes().getNamedItem("href").setNodeValue(newModelName+".notation#"+idXmi);
		}
		
		DOMSource source = new DOMSource(docNotation);
		StreamResult result = new StreamResult(new File(notationCopy));
		transformer.transform(source, result);
		
		DOMSource sourceUml = new DOMSource(docUml);
		StreamResult resultUml = new StreamResult(new File(umlCopy));
		transformer.transform(sourceUml, resultUml);
		
		
		DOMSource sourceDi = new DOMSource(docDi);
		StreamResult resultDi = new StreamResult(new File(diCopy));
		transformer.transform(sourceDi, resultDi);

		FileUtils.moveFiles(notationCopy, targetDirExport+ newModelName + ".notation");
		FileUtils.moveFiles(umlCopy, targetDirExport+ newModelName +".uml");
		FileUtils.moveFiles(diCopy, targetDirExport+newModelName +".di");
		
	}


}
